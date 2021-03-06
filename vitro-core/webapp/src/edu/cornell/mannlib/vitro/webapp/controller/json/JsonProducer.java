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

package edu.cornell.mannlib.vitro.webapp.controller.json;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * A base for the classes that produce JSON results. Contains some useful constants and convenience methods.
 */
public abstract class JsonProducer {
	private static final Log log = LogFactory.getLog(JsonProducer.class);
	
	/**
	 * Process a list of Individuals into a JSON array that holds the Names and URIs.
	 */
    protected JSONArray individualsToJson(List<Individual> individuals) throws ServletException {
        try{
        	JSONArray ja = new JSONArray();
        	for (Individual ent: individuals) {
                JSONObject entJ = new JSONObject();
                entJ.put("name", ent.getName());
                entJ.put("URI", ent.getURI());
                ja.put( entJ );
            }
        	return ja;
        }catch(JSONException ex){
            throw new ServletException("could not convert list of Individuals into JSON: " +  ex);
        }
    }

	/**
	 * Get the "vclassId" parameter from the request and instantiate the VClass.
	 * 
	 * There must be one, and it must be valid.
	 */
	protected VClass getVclassParameter(VitroRequest vreq) {
		String vclassId = vreq.getParameter("vclassId");
		if (StringUtils.isEmpty(vclassId)) {
			log.error("parameter vclassId expected but not found");
			throw new IllegalStateException("parameter vclassId expected ");
		}
		return instantiateVclass(vclassId, vreq);
	}

	/**
	 * Get one or more "vclassId" parameters from the request. Confirm that
	 * there is at least one, and that all are valid.
	 * 
	 * Return value is never null and never empty.
	 */
	protected List<String> getVclassIds(VitroRequest vreq) {
		String[] vclassIds = vreq.getParameterValues("vclassId");
		if ((vclassIds == null) || (vclassIds.length == 0)) {
			log.error("parameter vclassId expected but not found");
			throw new IllegalStateException("parameter vclassId expected ");
		}

		for (String vclassId : vclassIds) {
			instantiateVclass(vclassId, vreq);
		}

		return Arrays.asList(vclassIds);
	}
	
	private VClass instantiateVclass(String uri, VitroRequest vreq) {
		VClass vclass = vreq.getWebappDaoFactory().getVClassDao()
				.getVClassByURI(uri);
		if (vclass == null) {
			log.error("Couldn't retrieve vclass ");
			throw new IllegalStateException("Class " + uri + " not found");
		}
		return vclass;
	}

}
