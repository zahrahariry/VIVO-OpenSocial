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
package edu.cornell.mannlib.vitro.webapp.sparql;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;

/**
 * This servlet gets all the data properties for a given subject.
 * 
 * @param vClassURI
 * @author yuysun
 */

public class GetClazzDataProperties extends BaseEditController {

	private static final Log log = LogFactory.getLog(GetClazzDataProperties.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isAuthorizedToDisplayPage(request, response,
				SimplePermission.USE_MISCELLANEOUS_PAGES.ACTIONS)) {
        	return;
		}

		VitroRequest vreq = new VitroRequest(request);

		String vClassURI = vreq.getParameter("vClassURI");
		if (vClassURI == null || vClassURI.trim().equals("")) {
			return;
		}

		String respo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		respo += "<options>";

		// Add rdfs:label to the list
		respo += "<option>" + "<key>" + "label" + "</key>" + "<value>"
				+ "http://www.w3.org/2000/01/rdf-schema#label" + "</value>"
				+ "</option>";

		DataPropertyDao ddao = vreq.getFullWebappDaoFactory()
				.getDataPropertyDao();

		Collection<DataProperty> dataProps = ddao
				.getDataPropertiesForVClass(vClassURI);
		Iterator<DataProperty> dataPropIt = dataProps.iterator();
		HashSet<String> dpropURIs = new HashSet<String>();
		while (dataPropIt.hasNext()) {
			DataProperty dp = dataPropIt.next();
			if (!(dpropURIs.contains(dp.getURI()))) {
				dpropURIs.add(dp.getURI());
				DataProperty dprop = (DataProperty) ddao
						.getDataPropertyByURI(dp.getURI());
				if (dprop != null) {
					respo += "<option>" + "<key>" + dprop.getLocalName()
							+ "</key>" + "<value>" + dprop.getURI()
							+ "</value>" + "</option>";
				}
			}
		}
		respo += "</options>";
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		out.println(respo);
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
