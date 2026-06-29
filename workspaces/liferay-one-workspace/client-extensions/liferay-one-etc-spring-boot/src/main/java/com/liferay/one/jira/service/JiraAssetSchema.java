/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.model.AssetObjectType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Owns the JSM Assets schema that holds the migration-bound (Koroneiki-origin)
 * object types, and stamps it onto a type name to produce an
 * {@link AssetObjectType}.
 *
 * <p>
 * The schema is a single deploy setting ({@code liferay.one.jsm.schema.name}),
 * so the Koroneiki -&gt; One Liferay cutover moves every migration-bound type at
 * once. Callers ask for a type by name and never see the schema.
 * </p>
 *
 * @author Drew Brokke
 */
@Component
public class JiraAssetSchema {

	public AssetObjectType objectType(String objectTypeName) {
		return new AssetObjectType(_schemaName, objectTypeName);
	}

	@Value("${liferay.one.jsm.schema.name}")
	private String _schemaName;

}