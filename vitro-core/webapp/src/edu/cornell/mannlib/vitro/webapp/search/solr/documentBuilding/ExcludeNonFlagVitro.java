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

import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Exclude individuals with types from the Vitro namespace from the
 * search index. (Other than old vitro Flag types).
 */
public class ExcludeNonFlagVitro implements SearchIndexExcluder {

    @Override
    public String checkForExclusion(Individual ind) {            
        if( ind != null && ind.getVClasses() != null ) {                                
            String excludeMsg = skipIfVitro(ind,  ind.getVClasses() );
            if( excludeMsg != null)
                return excludeMsg;
        }
        return null;
    }

    String skipIfVitro(Individual ind, List<VClass> vclasses) {
        for( VClass type: vclasses ){
            if( type != null && type.getURI() != null ){                
                String typeURI = type.getURI();
                
                if(typeURI.startsWith( VitroVocabulary.vitroURI ) 
                   && ! typeURI.startsWith(VitroVocabulary.vitroURI + "Flag") ){
                    
                    return "Skipped " + ind.getURI()+" because in " 
                            + VitroVocabulary.vitroURI + " namespace";
                }
            }
        }   
        return null;
    }
    
}
