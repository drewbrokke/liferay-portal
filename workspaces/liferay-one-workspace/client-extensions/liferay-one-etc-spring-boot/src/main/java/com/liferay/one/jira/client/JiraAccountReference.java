/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.client;

import com.liferay.one.jira.util.AQLUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves a Liferay account external reference code to its JSM Assets
 * "Account" objectKey.
 *
 * <p>
 * This is the single place that knows which schema currently holds the
 * canonical Account objects, so the Koroneiki -&gt; One Liferay migration is a
 * one-line change here rather than a hunt through the business-event code.
 * </p>
 *
 * @author Drew Brokke
 */
@Component
public class JiraAccountReference {

	public String getObjectKey(String externalReferenceCode) throws Exception {
		String aql = AQLUtil.builder(
			AQLUtil.getBaseAQL(_SCHEMA_NAME, _OBJECT_TYPE_NAME)
		).andEquals(
			externalReferenceCode, _ATTRIBUTE_NAME_EXTERNAL_KEY
		).build();

		return _jiraAssetsClient.findObjectKey(aql);
	}

	private static final String _ATTRIBUTE_NAME_EXTERNAL_KEY = "External Key";

	private static final String _OBJECT_TYPE_NAME = "Account";

	// Migration seam: the schema that currently holds canonical Account objects.

	private static final String _SCHEMA_NAME = "Koroneiki";

	@Autowired
	private JiraAssetsClient _jiraAssetsClient;

}