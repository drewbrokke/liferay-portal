/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.model.JiraAssetObject;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class ContactAssetService {

	public JiraAssetObject fetchContactJiraAssetObjectByExternalKey(
		String externalKey) {

		List<JiraAssetObject> jiraAssetObjects =
			_jiraAssetService.searchObjects(
				_contactConverter.getAQLWithBuilder(
					aqlBuilder -> aqlBuilder.andEquals(
						externalKey,
						_contactConverter.getExternalKeyAttributeName())),
				_contactConverter::toJiraAssetObject);

		if (jiraAssetObjects.isEmpty()) {
			return null;
		}

		return jiraAssetObjects.get(0);
	}

	public void upsertJSMContact(String externalKey, JiraAssetObject payload) {
		JiraAssetObject jiraAssetObject =
			fetchContactJiraAssetObjectByExternalKey(externalKey);

		if (jiraAssetObject != null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Updating JSM contact for external key " + externalKey);
			}

			_jiraAssetService.updateObject(
				jiraAssetObject.getObjectId(), payload);
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Creating JSM contact for external key " + externalKey);
			}

			_jiraAssetService.createObject(
				_contactConverter.getObjectTypeId(), payload);
		}
	}

	private static final Log _log = LogFactory.getLog(
		ContactAssetService.class);

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private JiraAssetService _jiraAssetService;

}