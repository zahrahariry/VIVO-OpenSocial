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

package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

public class ThumbnailImageURL implements DocumentModifier {
	
    private static final String prefix = "prefix owl: <http://www.w3.org/2002/07/owl#> "
        + " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  "
        + " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
        + " prefix core: <http://vivoweb.org/ontology/core#>  "
        + " prefix foaf: <http://xmlns.com/foaf/0.1/> "
        + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
        + " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  "
        + " prefix bibo: <http://purl.org/ontology/bibo/>  ";
    
    private static final String query = prefix + 
    
    " SELECT (str(?downloadLocation) as ?DownloadLocation) WHERE { " +
    " ?uri <http://vitro.mannlib.cornell.edu/ns/vitro/public#mainImage> ?a . " +
    " ?a <http://vitro.mannlib.cornell.edu/ns/vitro/public#downloadLocation> ?downloadLocation . } " ;
    //" ?b <http://vitro.mannlib.cornell.edu/ns/vitro/public#directDownloadUrl> ?thumbnailLocationURL . } ";
    
    private Model model;
    private Log log = LogFactory.getLog(ThumbnailImageURL.class);
    
	static VitroSearchTermNames term = new VitroSearchTermNames();
	String fieldForThumbnailURL = term.THUMBNAIL_URL;
	
	
	public ThumbnailImageURL(Model model){
		this.model = model;
	}
	
	@Override
	public void modifyDocument(Individual individual, SolrInputDocument doc,
			StringBuffer addUri) throws SkipIndividualException {
		
		//add a field for storing the location of thumbnail for the individual.
		doc.addField(fieldForThumbnailURL, runQueryForThumbnailLocation(individual));
		addThumbnailExistance(individual, doc);
	}

	/**
     * Adds if the individual has a thumbnail image or not.
     */
    protected void addThumbnailExistance(Individual ind, SolrInputDocument doc) {
        try{
            if(ind.hasThumb())
                doc.addField(term.THUMBNAIL, "1");
            else
                doc.addField(term.THUMBNAIL, "0");
        }catch(Exception ex){
            log.debug("could not index thumbnail: " + ex);
        }        
    }

	protected String runQueryForThumbnailLocation(Individual individual) {
		
		StringBuffer result = new StringBuffer();
		QuerySolutionMap initialBinding = new QuerySolutionMap();
		Resource uriResource = ResourceFactory.createResource(individual.getURI());
		initialBinding.add("uri", uriResource);
		
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
        model.getLock().enterCriticalSection(Lock.READ);
        try{
            QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, model, initialBinding);
            try{                
                ResultSet results = qExec.execSelect();                
                while(results.hasNext()){                    
                    QuerySolution soln = results.nextSolution();                                   
                    Iterator<String> iter =  soln.varNames() ;
                    while( iter.hasNext()){
                        String name = iter.next();
                        RDFNode node = soln.get( name );
                        if( node != null ){
                            result.append("" + node.toString());
                        }else{
                            log.info(name + " is null");
                        }                        
                    }
                }
            }catch(Throwable t){                
                log.error(t,t);
            } finally{
                qExec.close();
            } 
        }finally{
            model.getLock().leaveCriticalSection();
        }
		
		return result.toString();
	}

	@Override
	public void shutdown() {		
	}

}
