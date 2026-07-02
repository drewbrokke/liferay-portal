/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.one.jira.constants.ContactConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.EntitlementConverter;
import com.liferay.one.jira.converter.PhoneConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.jira.service.ContactAssetService;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.service.AccountService;
import com.liferay.one.service.ProjectMembershipService;
import com.liferay.one.service.ProvisioningEmailService;
import com.liferay.one.service.UserAccountService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Felipe Veloso
 */
@RequestMapping("/user-accounts")
@RestController
public class UserAccountsRestController extends OneBaseRestController {

	@PostMapping("/assignments")
	public void postAssignments(@RequestBody String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);

		if (jsonObject.isNull("accountId") || jsonObject.isNull("userId")) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"\"accountId\" and \"userId\" are required");
		}

		long accountId = jsonObject.getLong("accountId");
		long userId = jsonObject.getLong("userId");

		Long accountRoleId = null;

		if (!jsonObject.isNull("accountRoleId")) {
			accountRoleId = jsonObject.getLong("accountRoleId");
		}

		_accountService.addAccountUserAccount(accountId, userId, accountRoleId);

		JSONArray projectsJSONArray = jsonObject.optJSONArray("projects");

		if (projectsJSONArray != null) {
			for (int i = 0; i < projectsJSONArray.length(); i++) {
				JSONObject projectJSONObject = projectsJSONArray.getJSONObject(
					i);

				_projectMembershipService.addProjectMembership(
					accountId, userId,
					projectJSONObject.getString("projectExternalReferenceCode"),
					projectJSONObject.getString("roleExternalReferenceCode"));
			}
		}

		_provisioningEmailService.sendAssignedWelcomeEmail(
			userId, _accountService.fetchAccount(accountId));
	}

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() throws Exception {
		try {
			List<UserAccount> userAccounts =
				_userAccountService.getAllUserAccounts();

			if (_log.isInfoEnabled()) {
				_log.info(
					"Syncing " + userAccounts.size() + " user accounts to JSM");
			}

			for (UserAccount userAccount : userAccounts) {
				_syncUserAccount(userAccount);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Synced " + userAccounts.size() + " user accounts to JSM");
			}
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing user accounts to JSM",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private List<String> _getAgentSupportRegions(UserAccount userAccount) {
		return null;
	}

	private List<String> _getContactRoleObjectIds(UserAccount userAccount) {
		return null;
	}

	private Boolean _getEmailAddressVerified(UserAccount userAccount) {
		return null;
	}

	private List<EntitlementDefinition> _getEntitlementDefinitions(
		UserAccount userAccount) {

		return null;
	}

	private List<String> _getExternalLinkObjectIds(UserAccount userAccount) {
		return null;
	}

	private Boolean _getFLSPartner(UserAccount userAccount) {
		return null;
	}

	private String _getGroups(UserAccount userAccount) {
		return null;
	}

	private List<Phone> _getPhones(UserAccount userAccount) {
		return null;
	}

	private List<String> _getSkillObjectIds(UserAccount userAccount) {
		return null;
	}

	private String _getSlackId(UserAccount userAccount) {
		return null;
	}

	private String _getSuborganization(UserAccount userAccount) {
		return null;
	}

	private List<String> _getSupportGroupObjectIds(UserAccount userAccount) {
		return null;
	}

	private List<Organization> _getTeamOrganizations(UserAccount userAccount) {
		return null;
	}

	private String _getUUID(UserAccount userAccount) {
		return null;
	}

	private void _syncUserAccount(UserAccount userAccount) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing user account " +
					userAccount.getExternalReferenceCode() + " to JSM");
		}

		JiraAssetObject assetObject = _contactConverter.toAssetObject(
			userAccount);

		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_AGENT_SUPPORT_REGION,
			_getAgentSupportRegions(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_EMAIL_ADDRESS_VERIFIED,
			_getEmailAddressVerified(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_FLS_PARTNER,
			_getFLSPartner(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_GROUPS, _getGroups(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_SLACK_ID, _getSlackId(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_SUBORGANIZATION,
			_getSuborganization(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_UUID, _getUUID(userAccount));

		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_accountConverter,
				ListUtil.fromArray(userAccount.getAccountBriefs()),
				AccountBrief::getExternalReferenceCode,
				_accountConverter::toAssetObjectFromAccountBrief));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_CONTACT_ROLES,
			_getContactRoleObjectIds(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_ENTITLEMENTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_entitlementConverter, _getEntitlementDefinitions(userAccount),
				EntitlementDefinition::getName,
				_entitlementConverter::toAssetObject));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_getExternalLinkObjectIds(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_PHONES,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_phoneConverter, _getPhones(userAccount), Phone::getPhoneNumber,
				_phoneConverter::toAssetObject));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_SKILLS,
			_getSkillObjectIds(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_SUPPORT_GROUPS,
			_getSupportGroupObjectIds(userAccount));
		assetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_TEAMS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_teamConverter, _getTeamOrganizations(userAccount),
				Organization::getExternalReferenceCode,
				_teamConverter::toAssetObject));

		_contactAssetService.upsertJSMContact(
			userAccount.getExternalReferenceCode(), assetObject);
	}

	private static final Log _log = LogFactory.getLog(
		UserAccountsRestController.class);

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private ContactAssetService _contactAssetService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private EntitlementConverter _entitlementConverter;

	@Autowired
	private PhoneConverter _phoneConverter;

	@Autowired
	private ProjectMembershipService _projectMembershipService;

	@Autowired
	private ProvisioningEmailService _provisioningEmailService;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private UserAccountService _userAccountService;

}