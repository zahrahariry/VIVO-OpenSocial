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

package edu.cornell.mannlib.vitro.webapp.utils.dataGetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.VClassGroupTemplateModel;

/**
 * This will pass these variables to the template:
 * classGroupUri: uri of the classgroup associated with this page.
 * vClassGroup: a data structure that is the classgroup associated with this page.     
 */
public class ClassGroupPageData extends DataGetterBase implements DataGetter{
	
    private static final Log log = LogFactory.getLog(ClassGroupPageData.class);
    String dataGetterURI;
    String classGroupUri;
    VitroRequest vreq;
    ServletContext context;

    /**
     * Constructor with display model and data getter URI that will be called by reflection.
     */
    public ClassGroupPageData(VitroRequest vreq, Model displayModel, String dataGetterURI){
        this.configure(vreq, displayModel,dataGetterURI);
    }        
    
    /**
     * Configure this instance based on the URI and display model.
     */
    protected void configure(VitroRequest vreq, Model displayModel, String dataGetterURI) {
    	if( vreq == null ) 
    		throw new IllegalArgumentException("VitroRequest  may not be null.");
        if( displayModel == null ) 
            throw new IllegalArgumentException("Display Model may not be null.");
        if( dataGetterURI == null )
            throw new IllegalArgumentException("PageUri may not be null.");
                
        this.vreq = vreq;
        this.context = vreq.getSession().getServletContext();
        this.dataGetterURI = dataGetterURI;        
        this.classGroupUri = 	DataGetterUtils.getClassGroupForDataGetter(displayModel, dataGetterURI);
    }
    
    
    @Override
    public Map<String, Object> getData(Map<String, Object> pageData) { 
    	  HashMap<String, Object> data = new HashMap<String,Object>();
          data.put("classGroupUri", this.classGroupUri);

          VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(context);
          List<VClassGroup> vcgList = vcgc.getGroups();
          VClassGroup group = null;
          for( VClassGroup vcg : vcgList){
              if( vcg.getURI() != null && vcg.getURI().equals(classGroupUri)){
                  group = vcg;
                  break;
              }
          }
          if( classGroupUri != null && !classGroupUri.isEmpty() && group == null ){ 
              /*This could be for two reasons: one is that the classgroup doesn't exist
               * The other is that there are no individuals in any of the classgroup's classes */
              group = vreq.getWebappDaoFactory().getVClassGroupDao().getGroupByURI(classGroupUri);
              if( group != null ){
                  List<VClassGroup> vcgFullList = vreq.getWebappDaoFactory().getVClassGroupDao()
                      .getPublicGroupsWithVClasses(false, true, false);
                  for( VClassGroup vcg : vcgFullList ){
                      if( classGroupUri.equals(vcg.getURI()) ){
                          group = vcg;
                          break;
                      }                                
                  }
                  
                  setAllClassCountsToZero(group);

                  log.debug("Retrieved class group " + group.getURI()
                        + " and returning to template");
                  if (log.isDebugEnabled()) {
                      List<VClass> groupClasses = group.getVitroClassList();
                      for (VClass v : groupClasses) {
                        log.debug("Class " + v.getName() + " - " + v.getURI()
                                + " has " + v.getEntityCount() + " entities");
                      }
                  }               
              }else{
                  throw new RuntimeException("classgroup " + classGroupUri + " does not exist in the system");
              }              
          }          
         
          data.put("vClassGroup", group);  //may put null
          
          //This page level data getters tries to set its own template,
          // not all of the data getters need to do this.
          data.put("bodyTemplate", "page-classgroup.ftl");
          
          //Also add data service url
          //Hardcoding for now, need a more dynamic way of doing this
          data.put("dataServiceUrlIndividualsByVClass", this.getDataServiceUrl());
          return data;
    }
     
    
    public static VClassGroupTemplateModel getClassGroup(String classGroupUri, ServletContext context, VitroRequest vreq){
        
        VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(context);
        List<VClassGroup> vcgList = vcgc.getGroups();
        VClassGroup group = null;
        for( VClassGroup vcg : vcgList){
            if( vcg.getURI() != null && vcg.getURI().equals(classGroupUri)){
                group = vcg;
                break;
            }
        }
        
        if( classGroupUri != null && !classGroupUri.isEmpty() && group == null ){ 
            /*This could be for two reasons: one is that the classgroup doesn't exist
             * The other is that there are no individuals in any of the classgroup's classes */
            group = vreq.getWebappDaoFactory().getVClassGroupDao().getGroupByURI(classGroupUri);
            if( group != null ){
                List<VClassGroup> vcgFullList = vreq.getWebappDaoFactory().getVClassGroupDao()
                    .getPublicGroupsWithVClasses(false, true, false);
                for( VClassGroup vcg : vcgFullList ){
                    if( classGroupUri.equals(vcg.getURI()) ){
                        group = vcg;
                        break;
                    }                                
                }
                if( group == null ){
                    log.error("Cannot get classgroup '" + classGroupUri + "'");
                    return null;
                }else{
                    setAllClassCountsToZero(group);
                }
            }else{
                log.error("classgroup " + classGroupUri + " does not exist in the system");
                return null;
            }            
        }
        
        return new VClassGroupTemplateModel(group);
    }
    
  //Get data servuice
    public String getDataServiceUrl() {
    	return UrlBuilder.getUrl("/dataservice?getRenderedSolrIndividualsByVClass=1&vclassId=");
    }
    
    /**
     * Query to get the definition of the ClassGroupDataGetter for a given data getter URI - specifically
     * the class group uri
     */
    private static final String forClassGroupURI = "<" + DisplayVocabulary.FOR_CLASSGROUP + ">";
    private static final String dataGetterQuery =
        "PREFIX display: <" + DisplayVocabulary.DISPLAY_NS +"> \n" +
        "SELECT ?classGroupUri WHERE { \n" +
        "  ?dataGetterUri "+forClassGroupURI+" ?classGroupUri . \n" +
        "}";      

    
    
    /**
     * For processing of JSONObject
     */
    //Currently empty, TODO: Review requirements
    public JSONObject convertToJSON(Map<String, Object> dataMap, VitroRequest vreq) {
    	JSONObject rObj = null;
    	return rObj;
    }
    protected static void setAllClassCountsToZero(VClassGroup vcg){
        for(VClass vc : vcg){
            vc.setEntityCount(0);
        }
    }
}