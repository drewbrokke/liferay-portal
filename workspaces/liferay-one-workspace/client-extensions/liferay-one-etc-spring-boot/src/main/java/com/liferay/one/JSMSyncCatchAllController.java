/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.one.jira.constants.AccountContactRoleAssignmentConstants;
import com.liferay.one.jira.constants.AccountTeamRoleAssignmentConstants;
import com.liferay.one.jira.constants.ContactRoleConstants;
import com.liferay.one.jira.constants.DataConstants;
import com.liferay.one.jira.constants.SecondLevelOfferingConstants;
import com.liferay.one.jira.constants.SkillConstants;
import com.liferay.one.jira.constants.SupportGroupsConstants;
import com.liferay.one.jira.constants.TeamContactRoleAssignmentConstants;
import com.liferay.one.jira.constants.TeamRoleConstants;
import com.liferay.one.jira.converter.AccountContactRoleAssignmentConverter;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.AccountTeamRoleAssignmentConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.ContactRoleConverter;
import com.liferay.one.jira.converter.DataConverter;
import com.liferay.one.jira.converter.SecondLevelOfferingConverter;
import com.liferay.one.jira.converter.SkillConverter;
import com.liferay.one.jira.converter.SupportGroupsConverter;
import com.liferay.one.jira.converter.TeamContactRoleAssignmentConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.converter.TeamRoleConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AssetObjectUpsertService;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.jira.service.JiraAssetService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Synchronizes the JSM object types that have no clear Liferay data source and
 * no dedicated controller yet. Every source method is a stub returning null,
 * so each sync is a no-op until its data source is filled in; syncs whose type
 * gains a real source should graduate to a dedicated controller.
 *
 * @author Drew Brokke
 */
@RequestMapping("/jsm-sync-catch-all")
@RestController
public class JSMSyncCatchAllController extends OneBaseRestController {

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() {
		try {
			_syncAccountContactRoleAssignments();
			_syncAccountTeamRoleAssignments();
			_syncContactRoles();
			_syncData();
			_syncSecondLevelOfferings();
			_syncSkills();
			_syncSupportGroups();
			_syncTeamContactRoleAssignments();
			_syncTeamRoles();
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing catch all object types to " +
					"JSM",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String _getAccountContactRoleAssignmentExternalUpdatedAt(
		String[] tuple) {

		return null;
	}

	/**
	 * Each tuple is {contactRoleExternalKey, contactExternalKey,
	 * accountExternalReferenceCode}.
	 */
	private List<String[]> _getAccountContactRoleAssignmentTuples() {
		return null;
	}

	private String _getAccountTeamRoleAssignmentExternalUpdatedAt(
		String[] tuple) {

		return null;
	}

	/**
	 * Each tuple is {teamRoleExternalKey, teamExternalKey,
	 * accountExternalReferenceCode}.
	 */
	private List<String[]> _getAccountTeamRoleAssignmentTuples() {
		return null;
	}

	private String _getContactRoleDescription(String externalKey) {
		return null;
	}

	private String _getContactRoleExternalCreatedAt(String externalKey) {
		return null;
	}

	private List<String> _getContactRoleExternalKeys() {
		return null;
	}

	private List<String> _getContactRoleExternalLinkObjectIds(
		String externalKey) {

		return null;
	}

	private String _getContactRoleExternalUpdatedAt(String externalKey) {
		return null;
	}

	private String _getContactRoleName(String externalKey) {
		return null;
	}

	private Boolean _getContactRoleSystem(String externalKey) {
		return null;
	}

	private String _getContactRoleType(String externalKey) {
		return null;
	}

	private List<String> _getDataNames() {
		return null;
	}

	private String _getDataSkills(String name) {
		return null;
	}

	private List<String> _getSecondLevelOfferingNames() {
		return null;
	}

	private String _getSecondLevelOfferingNumber(String name) {
		return null;
	}

	private List<String> _getSecondLevelOfferingParentEntitlementObjectIds(
		String name) {

		return null;
	}

	private String _getSkillLevel1ContactExternalKey(String name) {
		return null;
	}

	private String _getSkillLevel2ContactExternalKey(String name) {
		return null;
	}

	private String _getSkillLevel3ContactExternalKey(String name) {
		return null;
	}

	private List<String> _getSkillNames() {
		return null;
	}

	private String _getSupportGroupContactExternalKey(String name) {
		return null;
	}

	private List<String> _getSupportGroupNames() {
		return null;
	}

	private String _getTeamContactRoleAssignmentExternalUpdatedAt(
		String[] tuple) {

		return null;
	}

	/**
	 * Each tuple is {contactRoleExternalKey, contactExternalKey,
	 * teamExternalKey}.
	 */
	private List<String[]> _getTeamContactRoleAssignmentTuples() {
		return null;
	}

	private String _getTeamRoleDescription(String externalKey) {
		return null;
	}

	private String _getTeamRoleExternalCreatedAt(String externalKey) {
		return null;
	}

	private List<String> _getTeamRoleExternalKeys() {
		return null;
	}

	private String _getTeamRoleExternalUpdatedAt(String externalKey) {
		return null;
	}

	private String _getTeamRoleName(String externalKey) {
		return null;
	}

	private String _getTeamRoleType(String externalKey) {
		return null;
	}

	private void _syncAccountContactRoleAssignments() {
		List<String[]> tuples = _getAccountContactRoleAssignmentTuples();

		if (tuples == null) {
			return;
		}

		for (String[] tuple : tuples) {
			JiraAssetObject assetObject =
				_accountContactRoleAssignmentConverter.toAssetObject(
					tuple[0], tuple[1], tuple[2], false);

			assetObject.setAttributeValue(
				AccountContactRoleAssignmentConstants.
					ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
				_getAccountContactRoleAssignmentExternalUpdatedAt(tuple));

			assetObject.setAttributeValue(
				AccountContactRoleAssignmentConstants.
					ATTRIBUTE_NAME_CONTACT_ROLE,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_contactRoleConverter, tuple[0]));
			assetObject.setAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_contactConverter, tuple[1]));
			assetObject.setAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_accountConverter, tuple[2]));

			_assetObjectUpsertService.upsert(
				_accountContactRoleAssignmentConverter, assetObject);
		}
	}

	private void _syncAccountTeamRoleAssignments() {
		List<String[]> tuples = _getAccountTeamRoleAssignmentTuples();

		if (tuples == null) {
			return;
		}

		for (String[] tuple : tuples) {
			JiraAssetObject assetObject =
				_accountTeamRoleAssignmentConverter.toAssetObject(
					tuple[0], tuple[1], tuple[2], false);

			assetObject.setAttributeValue(
				AccountTeamRoleAssignmentConstants.
					ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
				_getAccountTeamRoleAssignmentExternalUpdatedAt(tuple));

			assetObject.setAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_ROLE,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_teamRoleConverter, tuple[0]));
			assetObject.setAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_teamConverter, tuple[1]));
			assetObject.setAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_accountConverter, tuple[2]));

			_assetObjectUpsertService.upsert(
				_accountTeamRoleAssignmentConverter, assetObject);
		}
	}

	private void _syncContactRoles() {
		List<String> externalKeys = _getContactRoleExternalKeys();

		if (externalKeys == null) {
			return;
		}

		for (String externalKey : externalKeys) {
			JiraAssetObject assetObject =
				_contactRoleConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_EXTERNAL_KEY, externalKey);
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_NAME,
				_getContactRoleName(externalKey));
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_DESCRIPTION,
				_getContactRoleDescription(externalKey));
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_SYSTEM,
				_getContactRoleSystem(externalKey));
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_TYPE,
				_getContactRoleType(externalKey));
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_EXTERNAL_CREATED_AT,
				_getContactRoleExternalCreatedAt(externalKey));
			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
				_getContactRoleExternalUpdatedAt(externalKey));

			assetObject.setAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
				_getContactRoleExternalLinkObjectIds(externalKey));

			_assetObjectUpsertService.upsert(
				_contactRoleConverter, assetObject);
		}
	}

	private void _syncData() {
		List<String> names = _getDataNames();

		if (names == null) {
			return;
		}

		for (String name : names) {
			JiraAssetObject assetObject =
				_dataConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				DataConstants.ATTRIBUTE_NAME_NAME, name);
			assetObject.setAttributeValue(
				DataConstants.ATTRIBUTE_NAME_SKILLS, _getDataSkills(name));

			_jiraAssetService.createObject(
				_dataConverter.getObjectTypeId(), assetObject);
		}
	}

	private void _syncSecondLevelOfferings() {
		List<String> names = _getSecondLevelOfferingNames();

		if (names == null) {
			return;
		}

		for (String name : names) {
			JiraAssetObject assetObject =
				_secondLevelOfferingConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				SecondLevelOfferingConstants.ATTRIBUTE_NAME_NAME, name);
			assetObject.setAttributeValue(
				SecondLevelOfferingConstants.ATTRIBUTE_NAME_NUMBER,
				_getSecondLevelOfferingNumber(name));

			assetObject.setAttributeValue(
				SecondLevelOfferingConstants.ATTRIBUTE_NAME_PARENT_ENTITLEMENT,
				_getSecondLevelOfferingParentEntitlementObjectIds(name));

			_jiraAssetService.createObject(
				_secondLevelOfferingConverter.getObjectTypeId(), assetObject);
		}
	}

	private void _syncSkills() {
		List<String> names = _getSkillNames();

		if (names == null) {
			return;
		}

		for (String name : names) {
			JiraAssetObject assetObject =
				_skillConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				SkillConstants.ATTRIBUTE_NAME_NAME, name);

			assetObject.setAttributeValue(
				SkillConstants.ATTRIBUTE_NAME_LEVEL_1,
				_assetReferenceResolverService.resolveObjectId(
					_contactConverter,
					_getSkillLevel1ContactExternalKey(name)));
			assetObject.setAttributeValue(
				SkillConstants.ATTRIBUTE_NAME_LEVEL_2,
				_assetReferenceResolverService.resolveObjectId(
					_contactConverter,
					_getSkillLevel2ContactExternalKey(name)));
			assetObject.setAttributeValue(
				SkillConstants.ATTRIBUTE_NAME_LEVEL_3,
				_assetReferenceResolverService.resolveObjectId(
					_contactConverter,
					_getSkillLevel3ContactExternalKey(name)));

			_jiraAssetService.createObject(
				_skillConverter.getObjectTypeId(), assetObject);
		}
	}

	private void _syncSupportGroups() {
		List<String> names = _getSupportGroupNames();

		if (names == null) {
			return;
		}

		for (String name : names) {
			JiraAssetObject assetObject =
				_supportGroupsConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				SupportGroupsConstants.ATTRIBUTE_NAME_NAME, name);
			assetObject.setAttributeValue(
				SupportGroupsConstants.ATTRIBUTE_NAME_CONTACTS,
				_assetReferenceResolverService.resolveObjectId(
					_contactConverter,
					_getSupportGroupContactExternalKey(name)));

			_jiraAssetService.createObject(
				_supportGroupsConverter.getObjectTypeId(), assetObject);
		}
	}

	private void _syncTeamContactRoleAssignments() {
		List<String[]> tuples = _getTeamContactRoleAssignmentTuples();

		if (tuples == null) {
			return;
		}

		for (String[] tuple : tuples) {
			JiraAssetObject assetObject =
				_teamContactRoleAssignmentConverter.toAssetObject(
					tuple[0], tuple[1], tuple[2], false);

			assetObject.setAttributeValue(
				TeamContactRoleAssignmentConstants.
					ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
				_getTeamContactRoleAssignmentExternalUpdatedAt(tuple));

			assetObject.setAttributeValue(
				TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT_ROLE,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_contactRoleConverter, tuple[0]));
			assetObject.setAttributeValue(
				TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_contactConverter, tuple[1]));
			assetObject.setAttributeValue(
				TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM,
				_assetReferenceResolverService.resolveObjectIdOrThrow(
					_teamConverter, tuple[2]));

			_assetObjectUpsertService.upsert(
				_teamContactRoleAssignmentConverter, assetObject);
		}
	}

	private void _syncTeamRoles() {
		List<String> externalKeys = _getTeamRoleExternalKeys();

		if (externalKeys == null) {
			return;
		}

		for (String externalKey : externalKeys) {
			JiraAssetObject assetObject =
				_teamRoleConverter.createJiraAssetObject();

			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_KEY, externalKey);
			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_NAME,
				_getTeamRoleName(externalKey));
			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_DESCRIPTION,
				_getTeamRoleDescription(externalKey));
			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_TYPE,
				_getTeamRoleType(externalKey));
			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_CREATED_AT,
				_getTeamRoleExternalCreatedAt(externalKey));
			assetObject.setAttributeValue(
				TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
				_getTeamRoleExternalUpdatedAt(externalKey));

			_assetObjectUpsertService.upsert(_teamRoleConverter, assetObject);
		}
	}

	@Autowired
	private AccountContactRoleAssignmentConverter
		_accountContactRoleAssignmentConverter;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountTeamRoleAssignmentConverter
		_accountTeamRoleAssignmentConverter;

	@Autowired
	private AssetObjectUpsertService _assetObjectUpsertService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private ContactRoleConverter _contactRoleConverter;

	@Autowired
	private DataConverter _dataConverter;

	@Autowired
	private JiraAssetService _jiraAssetService;

	@Autowired
	private SecondLevelOfferingConverter _secondLevelOfferingConverter;

	@Autowired
	private SkillConverter _skillConverter;

	@Autowired
	private SupportGroupsConverter _supportGroupsConverter;

	@Autowired
	private TeamContactRoleAssignmentConverter
		_teamContactRoleAssignmentConverter;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private TeamRoleConverter _teamRoleConverter;

}