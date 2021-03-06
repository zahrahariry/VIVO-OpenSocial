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

package edu.cornell.mannlib.vitro.webapp.controller.authenticate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean.AuthenticationSource;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

/**
 * A simple stub for unit tests that require an Authenticator. Call setup() to
 * put it into place.
 */
public class AuthenticatorStub extends Authenticator {
	public static final String FACTORY_ATTRIBUTE_NAME = AuthenticatorFactory.class
			.getName();

	// ----------------------------------------------------------------------
	// factory - store this in the context.
	//
	// Creates a single instance of the stub and returns it for all requests.
	// ----------------------------------------------------------------------

	public static class Factory implements Authenticator.AuthenticatorFactory {
		private final AuthenticatorStub instance = new AuthenticatorStub();

		@Override
		public AuthenticatorStub getInstance(HttpServletRequest request) {
			instance.setRequest(request);
			return instance;
		}
	}

	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private final Map<String, UserAccount> usersByEmail = new HashMap<String, UserAccount>();
	private final Map<String, UserAccount> usersByExternalAuthId = new HashMap<String, UserAccount>();

	private final Map<String, List<String>> editingPermissions = new HashMap<String, List<String>>();
	private final Map<String, String> associatedUris = new HashMap<String, String>();
	private final List<String> recordedLogins = new ArrayList<String>();
	private final Map<String, String> newPasswords = new HashMap<String, String>();

	private HttpServletRequest request;

	private void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void addUser(UserAccount user) {
		usersByEmail.put(user.getEmailAddress(), user);

		String externalAuthId = user.getExternalAuthId();
		if (!externalAuthId.isEmpty()) {
			usersByExternalAuthId.put(user.getExternalAuthId(), user);
		}
	}

	public void addEditingPermission(String username, String personUri) {
		if (!editingPermissions.containsKey(username)) {
			editingPermissions.put(username, new ArrayList<String>());
		}
		editingPermissions.get(username).add(personUri);
	}

	public void setAssociatedUri(String username, String individualUri) {
		associatedUris.put(username, individualUri);
	}

	public List<String> getRecordedLoginUsernames() {
		return recordedLogins;
	}

	public Map<String, String> getNewPasswordMap() {
		return newPasswords;
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public UserAccount getAccountForInternalAuth(String emailAddress) {
		return usersByEmail.get(emailAddress);
	}

	@Override
	public UserAccount getAccountForExternalAuth(String externalAuthId) {
		return usersByExternalAuthId.get(externalAuthId);
	}

	@Override
	public boolean isUserPermittedToLogin(UserAccount userAccount) {
		return true;
	}

	@Override
	public boolean isCurrentPassword(UserAccount userAccount,
			String clearTextPassword) {
		if (userAccount == null) {
			return false;
		} else {
			return userAccount.getMd5Password().equals(
					Authenticator.applyMd5Encoding(clearTextPassword));
		}
	}

	@Override
	public List<String> getAssociatedIndividualUris(UserAccount userAccount) {
		List<String> uris = new ArrayList<String>();

		String emailAddress = userAccount.getEmailAddress();
		if (associatedUris.containsKey(emailAddress)) {
			uris.add(associatedUris.get(emailAddress));
		}

		if (editingPermissions.containsKey(emailAddress)) {
			uris.addAll(editingPermissions.get(emailAddress));
		}

		return uris;
	}

	@Override
	public void recordNewPassword(UserAccount userAccount,
			String newClearTextPassword) {
		newPasswords.put(userAccount.getEmailAddress(), newClearTextPassword);
	}

	@Override
	public void recordLoginAgainstUserAccount(UserAccount userAccount,
			AuthenticationSource authSource) throws LoginNotPermitted {
		if (!isUserPermittedToLogin(userAccount)) {
			throw new LoginNotPermitted();
		}

		recordedLogins.add(userAccount.getEmailAddress());

		LoginStatusBean lsb = new LoginStatusBean(userAccount.getUri(),
				authSource);
		LoginStatusBean.setBean(request.getSession(), lsb);
	}

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

	@Override
	public void recordUserIsLoggedOut() {
		throw new RuntimeException(
				"AuthenticatorStub.recordUserIsLoggedOut() not implemented.");
	}

	@Override
	public boolean accountRequiresEditing(UserAccount userAccount) {
		throw new RuntimeException(
				"AuthenticatorStub.accountRequiresEditing() not implemented.");
	}

}
