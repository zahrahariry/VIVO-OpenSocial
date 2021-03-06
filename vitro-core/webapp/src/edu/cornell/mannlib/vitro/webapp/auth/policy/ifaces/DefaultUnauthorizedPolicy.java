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

package edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.BasicPolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 *a policy where every type of action is authorized as UNAUTHORIZED
 * by default.  This can be useful for a unauthenticated session or
 * as the last policy on a PolicyList to force INCONCLUSIVE decisions
 * to UNAUTHORIZED.
 */
public class DefaultUnauthorizedPolicy implements PolicyIface{
    protected static PolicyDecision UNAUTHORIZED_DECISION = new BasicPolicyDecision(
            Authorization.UNAUTHORIZED,
            "This is the default decision defined in DefaultUnauthorizedPolicy");

    public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
            RequestedAction whatToAuth) {
        if (whoToAuth == null)
            return new BasicPolicyDecision(Authorization.UNAUTHORIZED,
                    "null was passed as whoToAuth");
        if (whatToAuth == null)
            return new BasicPolicyDecision(Authorization.UNAUTHORIZED,
                    "null was passed as whatToAuth");
        return UNAUTHORIZED_DECISION;
    }

	@Override
	public String toString() {
		return "DefaultInconclusivePolicy";
	}     
}
