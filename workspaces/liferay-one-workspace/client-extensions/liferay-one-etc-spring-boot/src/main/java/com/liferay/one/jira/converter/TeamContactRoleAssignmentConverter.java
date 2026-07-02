/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.constants.TeamContactRoleAssignmentConstants;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.petra.string.StringBundler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class TeamContactRoleAssignmentConverter
	extends BaseAssetObjectConverter {

	@Override
	public String getExternalKeyAttributeName() {
		return TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_NAME;
	}

	public JiraAssetObject toAssetObject(
		String contactRoleExternalKey, String contactExternalKey,
		String teamExternalKey, boolean deleted) {

		JiraAssetObject jiraAssetObject = createJiraAssetObject();

		jiraAssetObject.setAttributeValue(
			TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_NAME,
			_getName(
				contactRoleExternalKey, contactExternalKey, teamExternalKey));
		jiraAssetObject.setAttributeValue(
			TeamContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_ROLE_EXTERNAL_KEY,
			contactRoleExternalKey);
		jiraAssetObject.setAttributeValue(
			TeamContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_EXTERNAL_KEY,
			contactExternalKey);
		jiraAssetObject.setAttributeValue(
			TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_EXTERNAL_KEY,
			teamExternalKey);
		jiraAssetObject.setAttributeValue(
			TeamContactRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED, deleted);

		return jiraAssetObject;
	}

	@Override
	protected String getObjectSchemaName() {
		return _schemaName;
	}

	@Override
	protected String getObjectTypeName() {
		return TeamContactRoleAssignmentConstants.OBJECT_TYPE_NAME;
	}

	private String _getName(
		String contactRoleExternalKey, String contactExternalKey,
		String teamExternalKey) {

		return StringBundler.concat(
			contactRoleExternalKey, ";", contactExternalKey, ";",
			teamExternalKey);
	}

	@Value("${liferay.one.jira.asset.schema.name}")
	private String _schemaName;

}