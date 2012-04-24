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

package edu.cornell.mannlib.semservices.bo;

public class Concept {

   private String definedBy;
   private String conceptId;
   private String bestMatch;
   private String label;
   private String type;
   private String definition;
   private String uri;

   /**
    * default constructor
    */
   public Concept() {

   }
   /**
    * @return the conceptId
    */
   public String getConceptId() {
      return conceptId;
   }
   /**
    * @param conceptId the conceptId to set
    */
   public void setConceptId(String conceptId) {
      this.conceptId = conceptId;
   }
   /**
    * @return the label
    */
   public String getLabel() {
      return label;
   }
   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label = label;
   }
   /**
    * @return the type
    */
   public String getType() {
      return type;
   }
   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }
   /**
    * @return the definition
    */
   public String getDefinition() {
      return definition;
   }
   /**
    * @param definition the definition to set
    */
   public void setDefinition(String definition) {
      this.definition = definition;
   }
   /**
    * @return the uri
    */
   public String getUri() {
      return uri;
   }
   /**
    * @param uri the uri to set
    */
   public void setUri(String uri) {
      this.uri = uri;
   }
   /**
    * @return the definedBy
    */
   public String getDefinedBy() {
      return definedBy;
   }
   /**
    * @param definedBy the definedBy to set
    */
   public void setDefinedBy(String definedBy) {
      this.definedBy = definedBy;
   }
   /**
    * @return the bestMatch
    */
   public String getBestMatch() {
      return bestMatch;
   }
   /**
    * @param bestMatch the bestMatch to set
    */
   public void setBestMatch(String bestMatch) {
      this.bestMatch = bestMatch;
   }

}