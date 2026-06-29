/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.model;

import com.liferay.one.jira.util.AQLUtil;

/**
 * Identifies a JSM Assets object type by its schema and type name, and builds
 * the base AQL that selects objects of that type.
 *
 * <p>
 * Pass this wherever an asset type needs to be named -- into an AQL builder or
 * (later) the schema service -- instead of carrying the schema/type name pair
 * around. Fixed-schema types are built directly; migration-bound types come
 * from {@code JiraAssetSchema}, which stamps the configured schema onto a type
 * name.
 * </p>
 *
 * @author Drew Brokke
 */
public final class AssetObjectType {

	public AssetObjectType(String schemaName, String objectTypeName) {
		_schemaName = schemaName;
		_objectTypeName = objectTypeName;
	}

	public String getBaseAQL() {
		return AQLUtil.getBaseAQL(_schemaName, _objectTypeName);
	}

	public String getObjectTypeName() {
		return _objectTypeName;
	}

	public String getSchemaName() {
		return _schemaName;
	}

	private final String _objectTypeName;
	private final String _schemaName;

}