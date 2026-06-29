/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.client.JiraAssetObject;
import com.liferay.one.jira.constants.BusinessEventConstants;
import com.liferay.one.jira.model.BusinessEvent;
import com.liferay.petra.string.StringPool;

import java.util.Map;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * Maps between a {@link BusinessEvent} and a JSM "Business Event" asset object.
 *
 * <p>
 * A pure mapper: it works only in attribute <em>names</em> and the supplied
 * name-to-id map. It does not resolve the schema, build AQL, talk to the API,
 * or know the JSM wire shape -- those belong to the schema service, the client,
 * and {@link JiraAssetObject} respectively.
 * </p>
 *
 * @author Amos Fong
 */
@Component
public class BusinessEventConverter {

	public JiraAssetObject toAssetObject(
		String accountObjectKey, BusinessEvent businessEvent,
		Map<String, String> attributeNameToIdsMap) {

		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			attributeNameToIdsMap);

		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_ACTUAL_EVENT_DATE,
			businessEvent.getActualEventDate());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_ASSOCIATED_TICKETS,
			businessEvent.getAssociatedTickets());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_CURRENT_VERSION,
			businessEvent.getCurrentLiferayVersionKey());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_DESCRIPTION,
			businessEvent.getDescription());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_EVENT_STATUS,
			businessEvent.getEventStatusName());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_EVENT_TYPE,
			businessEvent.getEventTypeName());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_LAST_COMMENT,
			businessEvent.getLastComment());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_LAST_UPDATED_AUTHOR,
			businessEvent.getLastUpdatedAuthorEmailAddress());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_NAME,
			businessEvent.getName());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_NEW_VERSION,
			businessEvent.getNewLiferayVersionKey());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_PLANNED_EVENT_DATE,
			businessEvent.getPlannedEventDate());
		jiraAssetObject.set(
			BusinessEventConstants.ATTRIBUTE_NAME_TIME_ZONE,
			businessEvent.getTimeZoneName());

		if (accountObjectKey != null) {
			jiraAssetObject.set(
				BusinessEventConstants.ATTRIBUTE_NAME_ACCOUNT,
				accountObjectKey);
			jiraAssetObject.set(
				BusinessEventConstants.ATTRIBUTE_NAME_AUTHOR,
				businessEvent.getAuthorEmailAddress());
		}

		return jiraAssetObject;
	}

	public BusinessEvent toBusinessEvent(
		String accountExternalReferenceCode,
		JSONObject jiraAssetObjectJSONObject,
		Map<String, String> attributeNameToIdsMap) {

		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			jiraAssetObjectJSONObject, attributeNameToIdsMap);

		return new BusinessEvent(
			accountExternalReferenceCode,
			jiraAssetObject.getKey(
				BusinessEventConstants.ATTRIBUTE_NAME_ACTUAL_EVENT_DATE),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_ASSOCIATED_TICKETS),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_AUTHOR),
			jiraAssetObject.getId(),
			jiraAssetObject.getKey(
				BusinessEventConstants.ATTRIBUTE_NAME_CURRENT_VERSION),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_CURRENT_VERSION),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_DESCRIPTION),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_EVENT_STATUS),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_EVENT_TYPE),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_LAST_COMMENT),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_LAST_UPDATED_AUTHOR),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_NAME),
			jiraAssetObject.getKey(
				BusinessEventConstants.ATTRIBUTE_NAME_NEW_VERSION),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_NEW_VERSION),
			jiraAssetObject.getKey(
				BusinessEventConstants.ATTRIBUTE_NAME_PLANNED_EVENT_DATE),
			jiraAssetObject.getValue(
				BusinessEventConstants.ATTRIBUTE_NAME_TIME_ZONE));
	}

	public BusinessEvent toBusinessEvent(
		String accountExternalReferenceCode, String attributesJSON,
		String authorEmailAddress) {

		JSONObject attributesJSONObject = new JSONObject(attributesJSON);

		return new BusinessEvent(
			accountExternalReferenceCode,
			attributesJSONObject.optString("actualEventDate"),
			attributesJSONObject.optString("associatedTickets"),
			authorEmailAddress, StringPool.BLANK,
			attributesJSONObject.optString("currentLiferayVersion"),
			StringPool.BLANK, attributesJSONObject.optString("description"),
			attributesJSONObject.optString("eventStatus"),
			attributesJSONObject.optString("eventType"),
			attributesJSONObject.optString("lastComment"), authorEmailAddress,
			attributesJSONObject.optString("name"),
			attributesJSONObject.optString("newLiferayVersion"),
			StringPool.BLANK,
			attributesJSONObject.optString("plannedEventDate"),
			attributesJSONObject.optString("timeZone"));
	}

}