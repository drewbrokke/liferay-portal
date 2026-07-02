/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.one.jira.constants.TeamConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AssetObjectUpsertService;
import com.liferay.one.jira.service.AssetReferenceResolverService;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Drew Brokke
 */
@RequestMapping("/organizations")
@RestController
public class OrganizationsRestController extends OneBaseRestController {

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() {
		try {
			List<Organization> organizations = _getOrganizations();

			if (organizations == null) {
				if (_log.isInfoEnabled()) {
					_log.info("No organizations to sync to JSM");
				}

				return new ResponseEntity<>(HttpStatus.OK);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Syncing " + organizations.size() +
						" organizations to JSM");
			}

			for (Organization organization : organizations) {
				_syncOrganization(organization);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Synced " + organizations.size() + " organizations to JSM");
			}
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing organizations to JSM",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String _getAccountExternalReferenceCode(Organization organization) {
		return null;
	}

	private List<String> _getExternalLinkObjectIds(Organization organization) {
		return null;
	}

	private String _getFLSGroup(Organization organization) {
		return null;
	}

	private List<Organization> _getOrganizations() {
		return null;
	}

	private Boolean _getSupportTeam(Organization organization) {
		return null;
	}

	private Boolean _getSystem(Organization organization) {
		return null;
	}

	private List<String> _getTeamRoleObjectIds(Organization organization) {
		return null;
	}

	private List<UserAccount> _getUserAccounts(Organization organization) {
		return null;
	}

	private void _syncOrganization(Organization organization) {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing organization " +
					organization.getExternalReferenceCode() + " to JSM");
		}

		JiraAssetObject assetObject = _teamConverter.toAssetObject(
			organization);

		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_FLS_GROUP, _getFLSGroup(organization));
		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_SUPPORT_TEAM,
			_getSupportTeam(organization));
		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_SYSTEM, _getSystem(organization));

		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceResolverService.resolveObjectIdOrThrow(
				_accountConverter,
				_getAccountExternalReferenceCode(organization)));

		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_CONTACTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_contactConverter, _getUserAccounts(organization),
				UserAccount::getExternalReferenceCode,
				_contactConverter::toAssetObject));
		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_getExternalLinkObjectIds(organization));
		assetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_TEAM_ROLES,
			_getTeamRoleObjectIds(organization));

		_assetObjectUpsertService.upsert(_teamConverter, assetObject);
	}

	private static final Log _log = LogFactory.getLog(
		OrganizationsRestController.class);

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AssetObjectUpsertService _assetObjectUpsertService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private TeamConverter _teamConverter;

}