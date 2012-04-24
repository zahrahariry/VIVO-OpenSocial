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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount.Status;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsPage;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;

/**
 * Handle the "Add new account" form display and submission.
 */
public class UserAccountsAddPage extends UserAccountsPage {
	private static final Log log = LogFactory.getLog(UserAccountsAddPage.class);

	private static final String PARAMETER_SUBMIT = "submitAdd";
	private static final String PARAMETER_EMAIL_ADDRESS = "emailAddress";
	private static final String PARAMETER_EXTERNAL_AUTH_ID = "externalAuthId";
	private static final String PARAMETER_EXTERNAL_AUTH_ONLY = "externalAuthOnly";
	private static final String PARAMETER_FIRST_NAME = "firstName";
	private static final String PARAMETER_LAST_NAME = "lastName";
	private static final String PARAMETER_ROLE = "role";
	private static final String PARAMETER_ASSOCIATED_PROFILE_URI = "associatedProfileUri";
	private static final String PARAMETER_NEW_PROFILE_CLASS_URI = "newProfileClassUri";

	private static final String ERROR_NO_EMAIL = "errorEmailIsEmpty";
	private static final String ERROR_EMAIL_IN_USE = "errorEmailInUse";
	private static final String ERROR_EMAIL_INVALID_FORMAT = "errorEmailInvalidFormat";
	private static final String ERROR_EXTERNAL_AUTH_ID_IN_USE = "errorExternalAuthIdInUse";
	private static final String ERROR_NO_FIRST_NAME = "errorFirstNameIsEmpty";
	private static final String ERROR_NO_LAST_NAME = "errorLastNameIsEmpty";
	private static final String ERROR_NO_ROLE = "errorNoRoleSelected";

	private static final String TEMPLATE_NAME = "userAccounts-add.ftl";

	private final UserAccountsAddPageStrategy strategy;
	private final boolean matchingIsEnabled;

	/* The request parameters */
	private boolean submit;
	private String emailAddress = "";
	private String externalAuthId = "";
	private boolean externalAuthOnly;
	private String firstName = "";
	private String lastName = "";
	private String selectedRoleUri = "";
	private String associatedProfileUri = "";
	private String newProfileClassUri = "";

	/** The result of validating a "submit" request. */
	private String errorCode = "";

	/** The new user account, if one was created. */
	private UserAccount addedAccount;

	public UserAccountsAddPage(VitroRequest vreq) {
		super(vreq);

		this.strategy = UserAccountsAddPageStrategy.getInstance(vreq, this,
				isEmailEnabled());

		this.matchingIsEnabled = SelfEditingConfiguration.getBean(vreq)
				.isConfigured();

		parseRequestParameters();

		if (submit) {
			validateParameters();
		}
	}

	private void parseRequestParameters() {
		submit = isFlagOnRequest(PARAMETER_SUBMIT);
		emailAddress = getStringParameter(PARAMETER_EMAIL_ADDRESS, "");
		externalAuthId = getStringParameter(PARAMETER_EXTERNAL_AUTH_ID, "");
		externalAuthOnly = isFlagOnRequest(PARAMETER_EXTERNAL_AUTH_ONLY);
		firstName = getStringParameter(PARAMETER_FIRST_NAME, "");
		lastName = getStringParameter(PARAMETER_LAST_NAME, "");
		selectedRoleUri = getStringParameter(PARAMETER_ROLE, "");
		associatedProfileUri = getStringParameter(
				PARAMETER_ASSOCIATED_PROFILE_URI, "");
		newProfileClassUri = getStringParameter(
				PARAMETER_NEW_PROFILE_CLASS_URI, "");

		strategy.parseAdditionalParameters();
	}

	public boolean isSubmit() {
		return submit;
	}

	private void validateParameters() {
		if (emailAddress.isEmpty()) {
			errorCode = ERROR_NO_EMAIL;
		} else if (isEmailInUse()) {
			errorCode = ERROR_EMAIL_IN_USE;
		} else if (!isEmailValidFormat()) {
			errorCode = ERROR_EMAIL_INVALID_FORMAT;
		} else if (isExternalAuthIdInUse()) {
			errorCode = ERROR_EXTERNAL_AUTH_ID_IN_USE;
		} else if (firstName.isEmpty()) {
			errorCode = ERROR_NO_FIRST_NAME;
		} else if (lastName.isEmpty()) {
			errorCode = ERROR_NO_LAST_NAME;
		} else if (selectedRoleUri.isEmpty()) {
			errorCode = ERROR_NO_ROLE;
		} else {
			errorCode = strategy.additionalValidations();
		}
	}

	private boolean isEmailInUse() {
		return userAccountsDao.getUserAccountByEmail(emailAddress) != null;
	}

	private boolean isExternalAuthIdInUse() {
		if (externalAuthId.isEmpty()) {
			return false;
		}
		return userAccountsDao.getUserAccountByExternalAuthId(externalAuthId) != null;
	}

	private boolean isEmailValidFormat() {
		return Authenticator.isValidEmailAddress(emailAddress);
	}

	public boolean isValid() {
		return errorCode.isEmpty();
	}

	public void createNewAccount() {
		// Assemble the fields into a new UserAccount
		UserAccount u = new UserAccount();
		u.setEmailAddress(emailAddress);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setExternalAuthId(externalAuthId);
		u.setExternalAuthOnly(externalAuthOnly);
		u.setMd5Password("");
		u.setOldPassword("");
		u.setPasswordChangeRequired(false);
		u.setPasswordLinkExpires(0);
		u.setLoginCount(0);
		u.setLastLoginTime(0L);
		u.setStatus(Status.INACTIVE);
		u.setPermissionSetUris(Collections.singleton(selectedRoleUri));

		strategy.setAdditionalProperties(u);

		// Create the account.
		String uri = userAccountsDao.insertUserAccount(u);
		this.addedAccount = userAccountsDao.getUserAccountByUri(uri);

		// Associate the profile, as appropriate.
		if (matchingIsEnabled) {
			if (!newProfileClassUri.isEmpty()) {
				try {
					String newProfileUri = UserAccountsProfileCreator
							.createProfile(indDao, dpsDao, newProfileClassUri,
									this.addedAccount);
					associatedProfileUri = newProfileUri;
				} catch (InsertException e) {
					log.error("Failed to create new profile of class '"
							+ newProfileClassUri + "' for user '"
							+ this.addedAccount.getEmailAddress() + "'");
				}
			}

			SelfEditingConfiguration.getBean(vreq)
					.associateIndividualWithUserAccount(indDao, dpsDao,
							this.addedAccount, associatedProfileUri);
		}

		strategy.notifyUser();
	}

	public final ResponseValues showPage() {
		Map<String, Object> body = new HashMap<String, Object>();

		body.put(PARAMETER_EMAIL_ADDRESS, emailAddress);
		body.put(PARAMETER_EXTERNAL_AUTH_ID, externalAuthId);
		body.put(PARAMETER_FIRST_NAME, firstName);
		body.put(PARAMETER_LAST_NAME, lastName);
		body.put("selectedRole", selectedRoleUri);
		body.put("roles", buildRolesList());
		body.put("profileTypes", buildProfileTypesList());
		body.put(PARAMETER_NEW_PROFILE_CLASS_URI, newProfileClassUri);
		body.put("formUrls", buildUrlsMap());

		if (externalAuthOnly) {
			body.put(PARAMETER_EXTERNAL_AUTH_ONLY, Boolean.TRUE);
		}

		if (!associatedProfileUri.isEmpty()) {
			body.put("associatedProfileInfo",
					buildProfileInfo(associatedProfileUri));
		}

		if (!errorCode.isEmpty()) {
			body.put(errorCode, Boolean.TRUE);
		}

		if (matchingIsEnabled) {
			body.put("showAssociation", Boolean.TRUE);
		}

		strategy.addMoreBodyValues(body);

		return new TemplateResponseValues(TEMPLATE_NAME, body);
	}

	public UserAccount getAddedAccount() {
		return addedAccount;
	}

	public boolean wasPasswordEmailSent() {
		return this.strategy.wasPasswordEmailSent();
	}

	boolean isExternalAuthOnly() {
		return externalAuthOnly;
	}

}