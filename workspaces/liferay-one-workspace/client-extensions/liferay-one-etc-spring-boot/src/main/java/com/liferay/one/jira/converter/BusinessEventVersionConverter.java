/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.client.JiraAssetObject;
import com.liferay.one.jira.constants.BusinessEventConstants;
import com.liferay.one.jira.model.BusinessEventVersion;

import java.util.Map;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * Maps a JSM "Business Event Version" asset object to a
 * {@link BusinessEventVersion}. A pure, read-only mapper -- see
 * {@link BusinessEventConverter} for the conventions.
 *
 * @author Amos Fong
 */
@Component
public class BusinessEventVersionConverter {

	public BusinessEventVersion toBusinessEventVersion(
		JSONObject jiraAssetObjectJSONObject,
		Map<String, String> attributeIds) {

		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			jiraAssetObjectJSONObject, attributeIds);

		return new BusinessEventVersion(
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_AUTHOR),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_CHANGE),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_COMMENT),
			jiraAssetObject.getKey(
				BusinessEventConstants.ATTRIBUTE_NAME_CREATED));
	}

}