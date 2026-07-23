/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.constants.AccountTeamRoleAssignmentConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.AccountTeamRoleAssignmentConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.converter.TeamRoleConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.petra.string.StringBundler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class AccountTeamRoleAssignmentSynchronizer {

	public void syncAssignTeamRole(
			String teamRoleExternalKey, String teamExternalKey,
			String accountExternalKey)
		throws Exception {

		_syncAssignment(
			teamRoleExternalKey, teamExternalKey, accountExternalKey, false);
	}

	public void syncUnassignTeamRole(
			String teamRoleExternalKey, String teamExternalKey,
			String accountExternalKey)
		throws Exception {

		_syncAssignment(
			teamRoleExternalKey, teamExternalKey, accountExternalKey, true);
	}

	private void _syncAssignment(
			String teamRoleExternalKey, String teamExternalKey,
			String accountExternalKey, boolean deleted)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					deleted ? "Unassigning" : "Assigning",
					" account team role ", teamRoleExternalKey, " for team ",
					teamExternalKey, " on account ", accountExternalKey));
		}

		JiraAssetObject jiraAssetObject =
			_accountTeamRoleAssignmentConverter.toAssetObject(
				teamRoleExternalKey, teamExternalKey, accountExternalKey,
				deleted, new Date());

		jiraAssetObject.setAttributeValue(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_ROLE,
			_assetReferenceObjectService.getReferenceObjectId(
				_teamRoleConverter, teamRoleExternalKey));
		jiraAssetObject.setAttributeValue(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM,
			_assetReferenceObjectService.getReferenceObjectId(
				_teamConverter, teamExternalKey));
		jiraAssetObject.setAttributeValue(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceObjectService.getReferenceObjectId(
				_accountConverter, accountExternalKey));

		_assetObjectUpsertService.upsert(
			_accountTeamRoleAssignmentConverter, jiraAssetObject);
	}

	private static final Log _log = LogFactory.getLog(
		AccountTeamRoleAssignmentSynchronizer.class);

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountTeamRoleAssignmentConverter
		_accountTeamRoleAssignmentConverter;

	@Autowired
	private AssetObjectUpsertService _assetObjectUpsertService;

	@Autowired
	private AssetReferenceObjectService _assetReferenceObjectService;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private TeamRoleConverter _teamRoleConverter;

}