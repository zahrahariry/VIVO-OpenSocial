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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/** 
 * Utilities for executing queries and working with query results. 
 * 
 */

public class QueryUtils {
    
    private static final Log log = LogFactory.getLog(QueryUtils.class);
    
    private QueryUtils() { }
    
    public static Map<String,Object> querySolutionToObjectValueMap( QuerySolution soln){
        Map<String,Object> map = new HashMap<String,Object>();
        Iterator<String> varNames = soln.varNames();
        while(varNames.hasNext()){
            String varName = varNames.next();
            Object value = nodeToObject( soln.get(varName));
            log.debug("Adding " + varName + " : " + value + " to query solution data.");            
            map.put(varName, value);
        }
        return map;
    }
 
    public static Map<String,String> querySolutionToStringValueMap( QuerySolution soln ){
        Map<String,String> map = new HashMap<String,String>();
        Iterator<String> varNames = soln.varNames();
        while(varNames.hasNext()){
            String varName = varNames.next();
            String value = nodeToString( soln.get(varName));
            log.debug("Adding " + varName + " : " + value + " to query solution data.");
            map.put(varName, value);           
        }
        return map;
    }
    
    public static Object nodeToObject( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getValue();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }

    public static String nodeToString( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getLexicalForm();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }
    
    /** Manually replace query variables with uris when prebinding causes the query to fail, probably
     * due to a Jena bug.
     */
    public static String subUrisForQueryVars(String queryString, Map<String, String> varsToUris) {
        
        for (String var : varsToUris.keySet()) {
           queryString = subUriForQueryVar(queryString, var, varsToUris.get(var));
        }
        return queryString;
    }

    /** Manually replace a query variable with a uri when prebinding causes the query to fail, probably
     * due to a Jena bug.
     */
    public static String subUriForQueryVar(String queryString, String varName, String uri) {
        return queryString.replaceAll("\\?" + varName + "\\b", "<" + uri + ">");
    }
    
    /**Replace one variable name with another**/
    public static String replaceQueryVar(String queryString, String varName, String newVarName) {
        return queryString.replaceAll("\\?" + varName + "\\b", "?" + newVarName);
    }
    
    public static ResultSet getQueryResults(String queryStr, VitroRequest vreq) {
        
        Dataset dataset = vreq.getDataset();
        dataset.getLock().enterCriticalSection(Lock.READ);
        QueryExecution qexec = null;
        ResultSet results = null;
        try {
            qexec = QueryExecutionFactory.create(queryStr, dataset);                    
            results = new ResultSetMem(qexec.execSelect());
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            dataset.getLock().leaveCriticalSection();
            if (qexec != null) {
                qexec.close();
            }
        } 
        
        return results;
    }

}
