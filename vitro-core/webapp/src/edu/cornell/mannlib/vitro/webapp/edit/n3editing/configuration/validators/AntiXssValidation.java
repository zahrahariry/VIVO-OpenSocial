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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.web.AntiScript;

/**
 * Check if the submitted text has potential XSS problems.
 * Error messages from this validator always start with XSS_ERROR_MESSAGE 
 * 
 * @author bdc34 
 */
public class AntiXssValidation implements N3ValidatorVTwo{
    List<String> fieldNamesToValidate;
    
       
    /**
     * Validate all fields on submission.
     */
    public AntiXssValidation(){
        this.fieldNamesToValidate = ALL_FIELDS;
    }

    /**
     * Validate only fields specified in fieldNamesToValidate.
     */
    public AntiXssValidation(List<String> fieldNamesToValidate){
        this.fieldNamesToValidate = fieldNamesToValidate;
    }
    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        
        if( editSub == null ) {
            return null;
        }
        
        Map<String,String>varToErrMsg = new HashMap<String,String>();
        if( fieldNamesToValidate == null ){
            if( editSub.getLiteralsFromForm() != null ){
                for( String name : editSub.getLiteralsFromForm().keySet()){
                    varToErrMsg.putAll( checkSubmissionForField( name, editSub));
                }
            }
            if( editSub.getUrisFromForm() != null ){
                for( String name : editSub.getUrisFromForm().keySet()){
                    varToErrMsg.putAll( checkSubmissionForField( name, editSub));
                }
            }
        }else{                
            for( String fieldName : fieldNamesToValidate){
                varToErrMsg.putAll( checkSubmissionForField(fieldName, editSub));            
            }
        }
        
        if( varToErrMsg.isEmpty() )
            return null;
        else            
            return varToErrMsg;    
    }
    
    /**
     * Check for XSS for a single field. Returns NO_ERROR if there 
     * are no errors so it can be added to a map with putAll() 
     */
    protected Map<String,String> checkSubmissionForField(
            String fieldName, MultiValueEditSubmission editSub){
        
        if( fieldName == null || fieldName.isEmpty() || editSub == null)
            return NO_ERROR;
        
        if( editSub.getLiteralsFromForm() != null && 
            editSub.getLiteralsFromForm().containsKey(fieldName) ){
            
            String error = null;
            try {
                error = literalHasXSS( editSub.getLiteralsFromForm().get(fieldName) );
            } catch (ScanException e) {
                error = e.getMessage();
            } catch (PolicyException e) {
                error = e.getMessage();
            }
            if( error != null ){                        
                return Collections.singletonMap(fieldName, XSS_ERROR_MESSAGE + " " + error);
            }else{
                return NO_ERROR;
            }
            
        } else if (editSub.getUrisFromForm() != null && 
                   editSub.getUrisFromForm().containsKey(fieldName)){
                    
            String error;
            try {
                error = uriHasXSS( editSub.getUrisFromForm().get(fieldName));
            } catch (ScanException e) {
                error = e.getMessage();
            } catch (PolicyException e) {
                error = e.getMessage();
            }
            if( error != null ){
                return Collections.singletonMap(fieldName, XSS_ERROR_MESSAGE + " " + error);
            }else{
                return NO_ERROR;
            }
            
        }else{
            //field wasn't in submission
            return  NO_ERROR;
        }                
    }

    /**
     * Check if a list of URIs has any XSS problems.
     * Return null if there are none and return an error message if there are problems.
     */
    private String uriHasXSS(List<String> uriList) throws ScanException, PolicyException {
        AntiSamy antiSamy = AntiScript.getAntiSamyScanner();
        ArrayList errorMsgs = new ArrayList();

        for( String uri : uriList ){
            CleanResults cr = antiSamy.scan( uri );
            errorMsgs.addAll( cr.getErrorMessages() );
        }
        
        if( errorMsgs.isEmpty() ){
            return null;
        }else{
            return StringUtils.join(errorMsgs, ", ");
        }
    }


    /**
     * Check if a List of Literals has any XSS problems.
     * Return null if there are none and return an error message if there are problems.
     */
    private String literalHasXSS(List<Literal> list) throws ScanException, PolicyException {
        AntiSamy antiSamy = AntiScript.getAntiSamyScanner();
        
        ArrayList errorMsgs = new ArrayList();        
        for( Literal literal : list ){   
        	if(literal != null) {
	            CleanResults cr = antiSamy.scan(literal.getLexicalForm());
	            errorMsgs.addAll( cr.getErrorMessages() );
	                         
	            String dt = literal.getDatatypeURI();
	            if( dt != null ){
	                cr = antiSamy.scan( dt );
	                errorMsgs.addAll( cr.getErrorMessages() );
	            }
	             
	            String lang = literal.getLanguage() ;
	            if( lang != null ){
	                cr = antiSamy.scan( lang );
	                errorMsgs.addAll( cr.getErrorMessages() );
	            }
        	}
        }
        
        if( errorMsgs.isEmpty() )
            return null;
        else
            return StringUtils.join(errorMsgs,", ");
        
    }


    /**
     * All error messages will start with this string. 
     */
    public static String XSS_ERROR_MESSAGE = "Field contains unacceptable markup";    
    
    private static final Map<String,String>NO_ERROR = Collections.emptyMap();
    
    //value indicates that all fields should be validated.
    private static final List<String> ALL_FIELDS = null;
}
