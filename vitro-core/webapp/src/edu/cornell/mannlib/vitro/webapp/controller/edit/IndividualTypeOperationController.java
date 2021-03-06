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

package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;

public class IndividualTypeOperationController extends BaseEditController {

    private static final Log log = LogFactory.getLog(IndividualTypeOperationController.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse response) {
		if (!isAuthorizedToDisplayPage(req, response,
				SimplePermission.DO_BACK_END_EDITING.ACTIONS)) {
        	return;
        }

    	VitroRequest request = new VitroRequest(req);
    	String defaultLandingPage = getDefaultLandingPage(request);
    	
        HashMap epoHash = null;
        EditProcessObject epo = null;
        try {
            epoHash = (HashMap) request.getSession().getAttribute("epoHash");
            epo = (EditProcessObject) epoHash.get(request.getParameter("_epoKey"));
        } catch (NullPointerException e) {
            //session or edit process expired
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException f) {
                log.error(f, f);
                throw new RuntimeException(f);
            }
            return;
        }

        if (epo == null) {
            log.error("null epo");
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException e) {
                log.error(e, e);
                throw new RuntimeException(e);
            }
            return;
        }

        IndividualDao dao = request.getAssertionsWebappDaoFactory().getIndividualDao();
        
        if (request.getParameter("_cancel") == null) {
	        try {
	            if (request.getParameter("operation").equals("remove")) {
	                String[] typeURIstrs = request.getParameterValues("TypeURI");
	                String individualURIstr = request.getParameter("individualURI");
	                    if (individualURIstr != null) {
	                        for (int i=0; i<typeURIstrs.length; i++) {                        	
	                            dao.removeVClass(individualURIstr, typeURIstrs[i]);
	                        }
	                    }
	            } else if (request.getParameter("operation").equals("add")) {
		                dao.addVClass(request.getParameter("individualURI"),request.getParameter("TypeURI"));
	            }
	        } catch (Exception e) {
	            log.error(e, e);
	        }
        }

        //if no page forwarder was set, just go back to referring page:
        //the referer stuff all will be changed so as not to rely on the HTTP header
        
        String referer = epo.getReferer();
        if (referer == null) {
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException e) {
                log.error(e, e);
                throw new RuntimeException(e);
            }
        } else {
            try {
                response.sendRedirect(referer);
            } catch (IOException e) {
                log.error(e, e);
                throw new RuntimeException(e);
            }
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }
	
}
