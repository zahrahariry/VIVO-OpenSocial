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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.webapp.util.ModelUtils;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.ontology.OntModel;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.RoleToActivityPredicatePreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditN3GeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.SelectListGeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;
/**
 * Generates the edit configuration for importing concepts from external
 * search services, e.g. UMLS etc.      
 * 
 * The N3 for this is set with the default settinf of 
 *
 */
public class AddUserDefinedConceptGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator {
	
	private Log log = LogFactory.getLog(AddUserDefinedConceptGenerator.class);
	private boolean isObjectPropForm = false;
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;
	private String datapropKeyStr= null;
	private int dataHash = 0;
	private DataPropertyStatement dps = null;
	private String dataLiteral = null;
	private String template = "addUserDefinedConcept.ftl";
	private static HashMap<String,String> defaultsForXSDtypes ;
	private static String SKOSConceptType = "http://www.w3.org/2004/02/skos/core#Concept";
	
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
    	EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
	  initBasics(editConfiguration, vreq);
      initPropertyParameters(vreq, session, editConfiguration);
      initObjectPropForm(editConfiguration, vreq);               
      
      editConfiguration.setTemplate(template);
      
      setVarNames(editConfiguration);
    	
    	//Assumes this is a simple case of subject predicate var
      editConfiguration.setN3Required(this.generateN3Required(vreq));
    	    	
      //n3 optional
      editConfiguration.setN3Optional(this.generateN3Optional());
    	
	//Todo: what do new resources depend on here?
	//In original form, these variables start off empty
	editConfiguration.setNewResources(generateNewResources(vreq));
	//In scope
	this.setUrisAndLiteralsInScope(editConfiguration, vreq);
	
	//on Form
	this.setUrisAndLiteralsOnForm(editConfiguration, vreq);
	
	editConfiguration.setFilesOnForm(new ArrayList<String>());
    	
    	//Sparql queries
    	this.setSparqlQueries(editConfiguration, vreq);
    	
    	//set fields
    	setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));
    	
    
    	setTemplate(editConfiguration, vreq);
    	
    	editConfiguration.addValidator(new AntiXssValidation());
    	
        //Add preprocessors
        addPreprocessors(editConfiguration, vreq.getWebappDaoFactory());
        //Adding additional data, specifically edit mode
        addFormSpecificData(editConfiguration, vreq);
        //One override for basic functionality, changing url pattern
        //and entity 
        //Adding term should return to this same page, not the subject
        //Return takes the page back to the individual form
        editConfiguration.setUrlPatternToReturnTo(getUrlPatternToReturnTo(vreq));
    	prepare(vreq, editConfiguration);
        return editConfiguration;
    }
    
 

	private String getUrlPatternToReturnTo(VitroRequest vreq) {
		String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		String generatorName = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAssociatedConceptGenerator";
		String editUrl = EditConfigurationUtils.getEditUrlWithoutContext(vreq);
		return editUrl + "?subjectUri=" + UrlBuilder.urlEncode(subjectUri) + 
		"&predicateUri=" + UrlBuilder.urlEncode(predicateUri) + 
		"&editForm=" + UrlBuilder.urlEncode(generatorName);
	}



	private void setVarNames(EditConfigurationVTwo editConfiguration) {
		  editConfiguration.setVarNameForSubject("subject");
	      editConfiguration.setVarNameForPredicate("predicate");
	      editConfiguration.setVarNameForObject("conceptNode");
	}

	protected void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
    	editConfiguration.setTemplate(template);
		
	}
   
   
    
   /*
    * N3 Required and Optional Generators as well as supporting methods 
    */
	
	private String getPrefixesString() {
		//TODO: Include dynamic way of including this
		return "@prefix core: <http://vivoweb.org/ontology/core#> .";
	}
	
	
	//Here, the node is typed as a skos concept
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3Required = list(    	            	        
    	        getPrefixesString() + "\n" +
    	        "?subject ?predicate ?conceptNode .\n" 
    	);
    	List<String> inversePredicate = getInversePredicate(vreq);
		//Adding inverse predicate if it exists
		if(inversePredicate.size() > 0) {
			n3Required.add("?conceptNode <" + inversePredicate.get(0) + "> ?subject .");
		}
    	return n3Required;
    }
    
   //Optional b/c user may select an existing SKOS concept
	private List<String> generateN3Optional() {
		return list(    
					"?conceptNode <" + VitroVocabulary.RDF_TYPE + "> <" + SKOSConceptType + "> .\n" + 
					"?conceptNode <" + label + "> ?conceptLabel ."
	    	);
		
    }
	
	
  
	
	/*
	 * Get new resources
	 */
	 private Map<String, String> generateNewResources(VitroRequest vreq) {
			HashMap<String, String> newResources = new HashMap<String, String>();
			newResources.put("conceptNode", null);
			//There are no new resources here, the concept node uri doesn't
			//get created but already exists, and vocab uri should already exist as well
			return newResources;
		}
    

	
	
	/*
	 * Set URIS and Literals In Scope and on form and supporting methods
	 */
    
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	//note that at this point the subject, predicate, and object var parameters have already been processed
    	//these two were always set when instantiating an edit configuration object from json,
    	//although the json itself did not specify subject/predicate as part of uris in scope
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	//Setting inverse role predicate
    	urisInScope.put("inverseRolePredicate", getInversePredicate(vreq));
    	
    	
    	editConfiguration.setUrisInScope(urisInScope);
    	//Uris in scope include subject, predicate, and object var
    	//literals in scope empty initially, usually populated by code in prepare for update
    	//with existing values for variables
    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }
    
    private List<String> getInversePredicate(VitroRequest vreq) {
		List<String> inversePredicateArray = new ArrayList<String>();
		ObjectProperty op = EditConfigurationUtils.getObjectProperty(vreq);
		if(op != null && op.getURIInverse() != null) {
			inversePredicateArray.add(op.getURIInverse());
		}
		return inversePredicateArray;
	}

	//n3 should look as follows
    //?subject ?predicate ?objectVar 
    
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	//The URI of the node that defines the concept
    	urisOnForm.add("conceptNode");
    	editConfiguration.setUrisOnform(urisOnForm);
    	//In case the user defines a new concept, will add a concept label
    	literalsOnForm.add("conceptLabel");
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }
    
    
    /**
     * Set SPARQL Queries and supporting methods
     */
    
    
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	Map<String, String> urisInScope = new HashMap<String, String>();
    	editConfiguration.setSparqlForAdditionalUrisInScope(new HashMap<String, String>());
    	editConfiguration.setSparqlForExistingLiterals(new HashMap<String, String>());
    	editConfiguration.setSparqlForExistingUris(new HashMap<String, String>());
    }

	/**
	 * 
	 * Set Fields and supporting methods
	 */
	
	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	setConceptNodeField(editConfiguration, vreq);
    	setConceptLabelField(editConfiguration, vreq);
    }
    
	//this field will be hidden and include the concept node URI
	private void setConceptNodeField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptNode").
				setOptionsType("UNDEFINED"));
		
	}


	private void setConceptLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptLabel").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}



	

	
    
   
    //Add preprocessor
	
   private void addPreprocessors(EditConfigurationVTwo editConfiguration, WebappDaoFactory wadf) {
	   //Will be a completely different type of preprocessor
	  /*
	   editConfiguration.addEditSubmissionPreprocessor(
			   new RoleToActivityPredicatePreprocessor(editConfiguration, wadf));
	   */
	}
     
   
	//Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		formSpecificData.put("conceptType", SKOSConceptType);
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	
	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);			
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);
		String query = "PREFIX core:<" + vivoCore + "> " + 
		"SELECT ?conceptNode WHERE { " + 
			"<" + subject + "> <" + predicate + "> ?conceptNode ." + 
			"?conceptNode <" + VitroVocabulary.RDF_TYPE + "> <" + SKOSConceptType + "> . }";
		return query;
	}
	
	//skos concepts can be added for either research areas or subject areas
	//IF coming in from a different form then can get the predicate here as it will be stored
	public String getCurrentPredicate(VitroRequest vreq) {
		return vreq.getParameter("conceptPredicate");
	}
	
	
}