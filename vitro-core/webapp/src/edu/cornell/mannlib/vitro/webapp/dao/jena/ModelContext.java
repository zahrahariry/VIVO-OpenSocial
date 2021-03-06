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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

public class ModelContext {
    
    private static final Log log = LogFactory.getLog(ModelContext.class);
	
	private static final String ONT_MODEL_SELECTOR = "ontModelSelector";
	private static final String UNION_ONT_MODEL_SELECTOR = "unionOntModelSelector";
	private static final String BASE_ONT_MODEL_SELECTOR = "baseOntModelSelector";
	private static final String INFERENCE_ONT_MODEL_SELECTOR = "inferenceOntModelSelector";
	
	private static final String JENA_ONT_MODEL = "jenaOntModel";
	private static final String BASE_ONT_MODEL = "baseOntModel";
	private static final String INFERENCE_ONT_MODEL = "inferenceOntModel";

	public ModelContext() {}
	
	public static OntModelSelector getOntModelSelector(ServletContext ctx) {
		return (OntModelSelector) ctx.getAttribute(ONT_MODEL_SELECTOR);
	}
	
	public static void setOntModelSelector(OntModelSelector oms, ServletContext ctx) {
		ctx.setAttribute(ONT_MODEL_SELECTOR, oms); 
	}
	
	public static OntModelSelector getUnionOntModelSelector(ServletContext ctx) {
		return (OntModelSelector) ctx.getAttribute(UNION_ONT_MODEL_SELECTOR);
	}
	
	public static void setUnionOntModelSelector(OntModelSelector oms, ServletContext ctx) {
		ctx.setAttribute(UNION_ONT_MODEL_SELECTOR, oms); 
	}
 	
	public static OntModelSelector getBaseOntModelSelector(ServletContext ctx) {
		return (OntModelSelector) ctx.getAttribute(BASE_ONT_MODEL_SELECTOR);
	}
	
	public static void setBaseOntModelSelector(OntModelSelector oms, ServletContext ctx) {
		ctx.setAttribute(BASE_ONT_MODEL_SELECTOR, oms); 
	}
	
	public static OntModelSelector getInferenceOntModelSelector(ServletContext ctx) {
		return (OntModelSelector) ctx.getAttribute(INFERENCE_ONT_MODEL_SELECTOR);
	}
	
	public static void setInferenceOntModelSelector(OntModelSelector oms, ServletContext ctx) {
		ctx.setAttribute(INFERENCE_ONT_MODEL_SELECTOR, oms); 
	}
	
	public static OntModel getJenaOntModel(ServletContext ctx) {
		return (OntModel) ctx.getAttribute(JENA_ONT_MODEL);
	}
	
	public static void setJenaOntModel(OntModel ontModel, ServletContext ctx) {
		ctx.setAttribute(JENA_ONT_MODEL, ontModel);
	}
	
	public static OntModel getBaseOntModel(ServletContext ctx) {
		return (OntModel) ctx.getAttribute(BASE_ONT_MODEL);
	}
	
	public static void setBaseOntModel(OntModel ontModel, ServletContext ctx) {
		ctx.setAttribute(BASE_ONT_MODEL, ontModel);
	}
	
	public static OntModel getInferenceOntModel(ServletContext ctx) {
		return (OntModel) ctx.getAttribute(INFERENCE_ONT_MODEL);
	}
	
	public static void setInferenceOntModel(OntModel ontModel, ServletContext ctx) {
		ctx.setAttribute(INFERENCE_ONT_MODEL, ontModel);
	}
	
	/**
	 * Register a listener to the models needed to get changes to:
	 *   Basic abox statemetns:
	 *      abox object property statements
	 *      abox data property statements
	 *      abox rdf:type statements
	 *      inferred types of individuals in abox
	 *      class group membership of individuals in abox
	 *      rdfs:labe annotations of things in abox.            
	 *   
	 *   Basic application annotations:
	 *       changes to annotations on classes
	 *       changes to annotations on class gorups
	 *       changes to annotations on properties
	 *       
	 *   Changes to application model
	 */
	public static void registerListenerForChanges(ServletContext ctx, ModelChangedListener ml){
	    
        try {
            RDFServiceUtils.getRDFServiceFactory(ctx).registerListener(
                    new JenaChangeListener(ml));
        } catch (RDFServiceException e) {
            log.error(e,e);
        }
        
	}
	
	public static OntModel getDisplayModel(ServletContext ctx){
	    return(OntModel) ctx.getAttribute( DisplayVocabulary.DISPLAY_ONT_MODEL );	    
	}
	public static void setDisplayModel(OntModel ontModel, ServletContext ctx){
	    ctx.setAttribute(DisplayVocabulary.DISPLAY_ONT_MODEL,ontModel);	    
	}
}
