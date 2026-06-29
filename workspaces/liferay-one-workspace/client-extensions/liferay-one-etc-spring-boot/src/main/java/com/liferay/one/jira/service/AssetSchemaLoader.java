/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.client.JiraAssetsClient;
import com.liferay.one.jira.exception.JiraAssetSchemaException;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Caches and resolves JSM Assets schema metadata to attribute name-to-id maps.
 * A thin resolver over {@link JiraAssetsClient}: the client owns all transport,
 * this owns the caching and the name-to-id mapping.
 *
 * @author Drew Brokke
 */
@Component
public class AssetSchemaLoader {

	@Cacheable("assetObjectTypeAttributeIds")
	public Map<String, String> getAttributeNameToIdsMap(String objectTypeId) {
		return _toNameIdMap(_getObjectTypeAttributesJSONArray(objectTypeId));
	}

	@Cacheable("assetObjectTypeIds")
	public Map<String, String> getObjectTypeNameToIdsMap(String schemaName) {
		return _toNameIdMap(
			_getObjectTypesJSONArray(_resolveSchemaId(schemaName)));
	}

	private JSONArray _getObjectSchemasJSONArray() {
		try {
			return _jiraAssetsClient.getObjectSchemas();
		}
		catch (Exception exception) {
			throw new JiraAssetSchemaException(
				"Unable to load object schemas", exception);
		}
	}

	private JSONArray _getObjectTypeAttributesJSONArray(String objectTypeId) {
		try {
			return _jiraAssetsClient.getObjectTypeAttributes(objectTypeId);
		}
		catch (Exception exception) {
			throw new JiraAssetSchemaException(
				"Unable to load attributes for object type " + objectTypeId,
				exception);
		}
	}

	private JSONArray _getObjectTypesJSONArray(String schemaId) {
		try {
			return _jiraAssetsClient.getObjectTypes(schemaId);
		}
		catch (Exception exception) {
			throw new JiraAssetSchemaException(
				"Unable to load object types for schema id " + schemaId,
				exception);
		}
	}

	private String _resolveSchemaId(String schemaName) {
		JSONArray objectSchemasJSONArray = _getObjectSchemasJSONArray();

		for (int i = 0; i < objectSchemasJSONArray.length(); i++) {
			JSONObject schemaJSONObject = objectSchemasJSONArray.getJSONObject(
				i);

			if (schemaName.equals(schemaJSONObject.optString("name"))) {
				return schemaJSONObject.getString("id");
			}
		}

		throw new JiraAssetSchemaException(
			"Object schema \"" + schemaName + "\" not found");
	}

	private Map<String, String> _toNameIdMap(JSONArray jsonArray) {
		Map<String, String> nameIdMap = new LinkedHashMap<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.optString("name");
			String id = jsonObject.optString("id");

			if (Validator.isNull(name) || Validator.isNull(id)) {
				continue;
			}

			nameIdMap.put(name, id);
		}

		return nameIdMap;
	}

	@Autowired
	private JiraAssetsClient _jiraAssetsClient;

}