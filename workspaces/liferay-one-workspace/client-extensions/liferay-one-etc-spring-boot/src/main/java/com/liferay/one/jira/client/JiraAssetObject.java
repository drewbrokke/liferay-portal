/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.client;

import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A JSM Assets object expressed in terms of attribute <em>names</em>.
 *
 * <p>
 * Wraps the JSM wire format (the {@code attributes} array of
 * {@code objectTypeAttributeId} / {@code objectAttributeValues} entries) and a
 * name-to-id map, so callers read and write attributes by their human-readable
 * names and never touch attribute ids or the wire shape. Use the reading
 * constructor for an object returned by the API, and the writing constructor to
 * assemble a create or update payload.
 * </p>
 *
 * @author Drew Brokke
 */
public class JiraAssetObject {

	/**
	 * For reading: wraps an object returned by the API; read by name with
	 * {@link #getValue(String)} / {@link #getKey(String)}.
	 */
	public JiraAssetObject(
		JSONObject jsonObject, Map<String, String> attributeNameToIdsMap) {

		_jsonObject = jsonObject;

		_attributeIds = attributeNameToIdsMap;
	}

	/**
	 * For writing: starts empty, populate with {@link #set(String, Object)}.
	 * {@code attributeNameToIdsMap} maps human-readable attribute names to JSM ids.
	 */
	public JiraAssetObject(Map<String, String> attributeNameToIdsMap) {
		this(new JSONObject(), attributeNameToIdsMap);
	}

	/**
	 * The object's own id (top-level {@code id}), not an attribute value.
	 */
	public String getId() {
		return _jsonObject.optString("id");
	}

	/**
	 * The stored value of the named attribute: the raw {@code value}, or a
	 * reference attribute's target id. Despite the name, "key" = the machine
	 * value; use this for durable values (a version key, a reference id). See
	 * {@link #getValue(String)} for the rendered label.
	 */
	public String getKey(String attributeName) {
		return _getAttributeKey(_getAttributeValueJSONObject(attributeName));
	}

	/**
	 * The display label of the named attribute ({@code displayValue}), falling
	 * back to {@link #getKey(String)}. Despite the name, "value" = the
	 * human-readable label; use this for text shown to a person.
	 */
	public String getValue(String attributeName) {
		JSONObject attributeValueJSONObject = _getAttributeValueJSONObject(
			attributeName);

		return attributeValueJSONObject.optString(
			"displayValue", _getAttributeKey(attributeValueJSONObject));
	}

	/**
	 * Stages a value for the named attribute (resolved to its id; logs an error
	 * and skips if the name is unmapped). A null value is skipped, so absent
	 * data is omitted rather than written as null. For a reference attribute,
	 * pass the target's key (e.g. an account objectKey).
	 */
	public void set(String attributeName, Object value) {
		String attributeId = _getAttributeId(attributeName);

		if ((attributeId == null) || (value == null)) {
			return;
		}

		_values.put(attributeId, value);
	}

	/**
	 * Serializes staged {@link #set(String, Object)} values into JSM's
	 * {@code attributes} wire shape. Called by the client.
	 */
	public JSONArray toAttributesJSONArray() {
		JSONArray attributesJSONArray = new JSONArray();

		for (Map.Entry<String, Object> entry : _values.entrySet()) {
			attributesJSONArray.put(
				new JSONObject(
				).put(
					"objectAttributeValues",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"value", entry.getValue()
						)
					)
				).put(
					"objectTypeAttributeId", entry.getKey()
				));
		}

		return attributesJSONArray;
	}

	private String _getAttributeId(String attributeName) {
		String attributeId = _attributeIds.get(attributeName);

		if (Validator.isNull(attributeId)) {
			_log.error(
				"No JSM attribute id is mapped for attribute name \"" +
					attributeName +
						"\"; a constant likely no longer matches the schema");

			return null;
		}

		return attributeId;
	}

	private String _getAttributeKey(JSONObject attributeValueJSONObject) {
		String key = attributeValueJSONObject.optString("value");

		if (Validator.isNull(key)) {
			JSONObject referencedObjectJSONObject =
				attributeValueJSONObject.optJSONObject("referencedObject");

			if (referencedObjectJSONObject != null) {
				key = referencedObjectJSONObject.optString("id");
			}
		}

		return key;
	}

	private JSONObject _getAttributeValueJSONObject(String attributeName) {
		String attributeId = _getAttributeId(attributeName);

		if (attributeId == null) {
			return new JSONObject();
		}

		JSONArray attributesJSONArray = _jsonObject.optJSONArray("attributes");

		if (attributesJSONArray == null) {
			return new JSONObject();
		}

		for (int i = 0; i < attributesJSONArray.length(); i++) {
			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				i);

			if (!attributeId.equals(
					attributeJSONObject.optString("objectTypeAttributeId"))) {

				continue;
			}

			JSONArray objectAttributeValuesJSONArray =
				attributeJSONObject.optJSONArray("objectAttributeValues");

			if ((objectAttributeValuesJSONArray == null) ||
				objectAttributeValuesJSONArray.isEmpty()) {

				break;
			}

			return objectAttributeValuesJSONArray.getJSONObject(0);
		}

		return new JSONObject();
	}

	private static final Log _log = LogFactory.getLog(JiraAssetObject.class);

	private final Map<String, String> _attributeIds;
	private final JSONObject _jsonObject;
	private final Map<String, Object> _values = new LinkedHashMap<>();

}