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

package edu.cornell.mannlib.vitro.webapp.utils;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ObjectPropertyDaoJena;

public class FrontEndEditingUtils {
 
    private static final Log log = LogFactory.getLog(FrontEndEditingUtils.class);

    public static enum EditMode {
        ADD, EDIT, REPAIR, ERROR;
    }
    
    /* Determine whether a property editing form is in add, edit, or repair mode. */
    public static EditMode getEditMode(HttpServletRequest request, String relatedPropertyUri) {
       
        Individual obj = (Individual)request.getAttribute("object");
        return getEditMode(request, obj, relatedPropertyUri);
    }
    
    public static EditMode getEditMode(HttpServletRequest request, Individual obj, String relatedPropertyUri) {
    	 EditMode mode = EditMode.ADD;
         if( obj != null){
             List<ObjectPropertyStatement> stmts = obj.getObjectPropertyStatements(relatedPropertyUri);
             if( stmts != null){
                 if( stmts.size() > 1 ){
                     mode = EditMode.ERROR; // Multiple roleIn statements, yuck.
                     log.debug("Multiple statements found for property " + relatedPropertyUri + ". Setting edit mode to ERROR.");
                 }else if( stmts.size() == 0 ){
                     mode = EditMode.REPAIR; // need to repair the role node
                     log.debug("No statements found for property " + relatedPropertyUri + ". Setting edit mode to REPAIR.");
                 }else if(stmts.size() == 1 ){
                     mode = EditMode.EDIT; // editing single statement
                     log.debug("Single statement found for property " + relatedPropertyUri + ". Setting edit mode to EDIT.");
                 } 
             } else {
                 log.debug("Statements null for property " + relatedPropertyUri + " . Setting edit mode to ADD.");
             }
         } else {
             log.debug("No object. Setting edit mode to ADD.");        
         }
         return mode;
    }
 
   
}
