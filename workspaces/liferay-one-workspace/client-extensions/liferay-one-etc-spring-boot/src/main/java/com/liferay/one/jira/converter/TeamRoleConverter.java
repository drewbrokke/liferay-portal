/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.one.jira.constants.TeamRoleConstants;
import com.liferay.one.jira.model.JiraAssetObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class TeamRoleConverter extends BaseJiraAssetObjectConverter {

	@Override
	public String getObjectTypeName() {
		return TeamRoleConstants.OBJECT_TYPE_NAME;
	}

	public JiraAssetObject toAssetObject(Role role) {
		JiraAssetObject jiraAssetObject = createJiraAssetObject();

		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_KEY,
			role.getExternalReferenceCode());
		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_NAME, role.getName());
		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_DESCRIPTION,
			role.getDescription());
		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_CREATED_AT,
			formatDate(role.getDateCreated()));
		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
			formatDate(role.getDateModified()));

		return jiraAssetObject;
	}

	/**
	 * Builds a sparse asset object from a {@link RoleBrief}, used only as the
	 * create fallback when a Team's Team Roles reference doesn't resolve yet.
	 * A RoleBrief carries no description or dates; those get backfilled the
	 * next time {@link
	 * com.liferay.one.jira.service.OrganizationRoleSynchronizer} syncs the
	 * full catalog.
	 */
	public JiraAssetObject toAssetObject(RoleBrief roleBrief) {
		JiraAssetObject jiraAssetObject = createJiraAssetObject();

		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_EXTERNAL_KEY,
			roleBrief.getExternalReferenceCode());
		jiraAssetObject.setAttributeValue(
			TeamRoleConstants.ATTRIBUTE_NAME_NAME, roleBrief.getName());

		return jiraAssetObject;
	}

	@Override
	protected String getObjectSchemaName() {
		return _schemaName;
	}

	@Value("${liferay.one.jira.asset.schema.name}")
	private String _schemaName;

}