/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.search.controller;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.SparqlUtils;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.SparqlUtils.AjaxControllerException;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

/**
 * DataAutocompleteController generates autocomplete content
 * for data properties using sparql queries
 * . 
 */

public class DataAutocompleteController extends VitroAjaxController {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(DataAutocompleteController.class);
        
    private static final String PARAM_QUERY = "term";
    //To get the data property
    private static final String PARAM_PROPERTY = "property";

    
    String NORESULT_MSG = "";    
    private static final int DEFAULT_MAX_HIT_COUNT = 1000; 

    public static final int MAX_QUERY_LENGTH = 500;
    
    @Override
    protected Actions requiredActions(VitroRequest vreq) {
    	//used to be basic vitro ajax permission but need to query full model
    	return SimplePermission.QUERY_FULL_MODEL.ACTIONS;
    }
    
    @Override
    protected void doRequest(VitroRequest vreq, HttpServletResponse response)
        throws IOException, ServletException {
        
        try {
            //This is the literal being searched for
            String qtxt = vreq.getParameter(PARAM_QUERY);
            String property = vreq.getParameter(PARAM_PROPERTY);
            //Get sparql query for this property
            String sparqlQuery = getSparqlQuery(qtxt, property);
            //Forward this to SparqlQueryAjaxController as that already
            //handles execution of query and will return json results
      //      String encodedQuery = URLEncoder.encode(sparqlQuery, "UTF-8");
      //      response.sendRedirect(vreq.getContextPath() + "/ajax/sparqlQuery?query=" + encodedQuery);
            //RequestDispatcher dispatcher = vreq.getRequestDispatcher("/ajax/sparqlQuery?query=" + encodedQuery );
            //dispatcher.forward(vreq, response);
            
            //Get Model and execute query
            Model model = getModel(vreq);
            Query query = SparqlUtils.createQuery(sparqlQuery);
			outputResults(response, query, model);
			return;
        } catch(AjaxControllerException ex) {
        	log.error(ex, ex);
			response.sendError(ex.getStatusCode());
        }
        catch (Throwable e) {
            log.error(e, e);            
            //doSearchError(response);
        }
    }
    
    private void outputResults(HttpServletResponse response, Query query,
			Model model)     throws IOException{
    	Dataset dataset = DatasetFactory.create(model);
    	//Iterate through results and print out array of strings
    	List<String> outputResults = new ArrayList<String>();
		QueryExecution qe = QueryExecutionFactory.create(query, dataset);
		try {
			ResultSet results = qe.execSelect();
			while(results.hasNext()) {
	    		QuerySolution qs = results.nextSolution();
	    		Literal dataLiteral = qs.getLiteral("dataLiteral");
	    		String dataValue = dataLiteral.getString();
	    		outputResults.add(dataValue);
	    	}
	        JSONArray jsonArray = new JSONArray(outputResults);
	        try {
	        	response.getWriter().write(jsonArray.toString());
	        } catch (Throwable e) {
	            log.error(e, e);            
	            doSearchError(response);
	        }
		} finally {
			qe.close();
		}

		
	}

	private Model getModel(VitroRequest vreq) throws AjaxControllerException{
    	Model model = vreq.getJenaOntModel();
	
		if (model == null) {
			throw new AjaxControllerException(SC_INTERNAL_SERVER_ERROR,
					"Model '' not found.");
		}	
		return model;
    }

	private String getSparqlQuery(String qtxt, String property) {
		//"i" denotes case insensitive
		//Searches for data literals whose string version begins with the text denoted
		String query = "SELECT DISTINCT ?dataLiteral where {" + 
		"?s <" + property + "> ?dataLiteral . " + 
		"FILTER(regex(str(?dataLiteral), \"^" + qtxt + "\", \"i\")) } ORDER BY ?dataLiteral";
		return query;
	}
	
	
	 private void doSearchError(HttpServletResponse response) throws IOException {
	        // For now, we are not sending an error message back to the client because 
	        // with the default autocomplete configuration it chokes.
	        doNoSearchResults(response);
	    }

	    private void doNoSearchResults(HttpServletResponse response) throws IOException {
	        response.getWriter().write("[]");
	    }

}
