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

package edu.cornell.mannlib.vitro.webapp.web.methods;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.web.images.PlaceholderUtil;
import freemarker.core.Environment;
import freemarker.template.TemplateModelException;

/**
 * Get a URL for the placeholder image for this Individual, based on the VClass 
 * that the Individual belongs to.
 */
public class IndividualPlaceholderImageUrlMethod extends BaseTemplateMethodModel {

    @SuppressWarnings("rawtypes")
    @Override
    public String exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Wrong number of arguments");
        }

        String uri = (String) args.get(0);        
        Environment env = Environment.getCurrentEnvironment();
        HttpServletRequest request = (HttpServletRequest) env.getCustomAttribute("request");
        VitroRequest vreq = new VitroRequest(request);
        String imageUrl = PlaceholderUtil.getPlaceholderImagePathForIndividual(vreq, uri);
        return UrlBuilder.getUrl(imageUrl);
    }

    @Override
    public Map<String, Object> help(String name) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("return value", "The URL of the placeholder image for this individual, " +
        		"based on the VClasses that the individual belongs to.");

        List<String>params = new ArrayList<String>();
        params.add("Uri of individual");
        map.put("parameters", params);
        
        List<String> examples = new ArrayList<String>();
        examples.add(name + "(individual.uri)");
        map.put("examples", examples);
        
        return map;
    }
    
}
