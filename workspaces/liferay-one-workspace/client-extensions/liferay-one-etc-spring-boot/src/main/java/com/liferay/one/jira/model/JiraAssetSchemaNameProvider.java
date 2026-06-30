/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The sole responsibility of this class is to read the JSM Schema that will be
 * used as the sync target. It can then create AssetObjectType instances with
 * the configured schema name applied.
 *
 * @author Drew Brokke
 */
@Component
public class JiraAssetSchemaNameProvider {

	public String getSchemaName() {
		return _schemaName;
	}

	@Value("${liferay.one.jira.asset.schema.name}")
	private String _schemaName;

}