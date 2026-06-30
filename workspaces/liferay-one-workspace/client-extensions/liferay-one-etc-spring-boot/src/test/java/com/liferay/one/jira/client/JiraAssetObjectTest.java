/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.client;

import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Drew Brokke
 */
public class JiraAssetObjectTest {

	@Test
	public void testGetAttributeValue() {
		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			_jsonObject, _attributeNameToIdsMap);

		Assertions.assertEquals(
			"ACME", jiraAssetObject.getAttributeDisplayValue("Name"));
		Assertions.assertEquals(
			"OBJ-42", jiraAssetObject.getAttributeValue("Account"));
		Assertions.assertEquals(
			"5", jiraAssetObject.getAttributeValue("Event Status"));
		Assertions.assertEquals(
			"", jiraAssetObject.getAttributeValue("Description"));
	}

	@Test
	public void testGetObjectId() {
		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			_jsonObject, _attributeNameToIdsMap);

		Assertions.assertEquals("123", jiraAssetObject.getObjectId());
	}

	@Test
	public void testReadGetAttributeDisplayValueResolvesReferenceLabel() {
		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			_jsonObject, _attributeNameToIdsMap);

		Assertions.assertEquals(
			"Acme Corp", jiraAssetObject.getAttributeDisplayValue("Account"));
		Assertions.assertEquals(
			"Done", jiraAssetObject.getAttributeDisplayValue("Event Status"));
	}

	@Test
	public void testToAttributesJSONArray() {
		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			_attributeNameToIdsMap);

		jiraAssetObject.setAttributeValue("Name", "ACME");
		jiraAssetObject.setAttributeValue("Account", "OBJ-42");
		jiraAssetObject.setAttributeValue("Description", null);
		jiraAssetObject.setAttributeValue("Nope", "whatever");

		JSONArray attributesJSONArray = jiraAssetObject.toAttributesJSONArray();

		Assertions.assertEquals(2, attributesJSONArray.length());

		JSONObject nameJSONObject = attributesJSONArray.getJSONObject(0);

		Assertions.assertEquals("100", _objectTypeAttributeId(nameJSONObject));
		Assertions.assertEquals("ACME", _firstValue(nameJSONObject));

		JSONObject accountJSONObject = attributesJSONArray.getJSONObject(1);

		Assertions.assertEquals(
			"200", _objectTypeAttributeId(accountJSONObject));
		Assertions.assertEquals("OBJ-42", _firstValue(accountJSONObject));
	}

	private JSONObject _attributeJSONObject(
		String objectTypeAttributeId,
		JSONObject objectAttributeValueJSONObject) {

		return new JSONObject(
		).put(
			"objectAttributeValues",
			new JSONArray(
			).put(
				objectAttributeValueJSONObject
			)
		).put(
			"objectTypeAttributeId", objectTypeAttributeId
		);
	}

	private String _firstValue(JSONObject jsonObject) {
		JSONArray objectAttributeValuesJSONArray = jsonObject.getJSONArray(
			"objectAttributeValues");

		JSONObject objectAttributeValueJSONObject =
			objectAttributeValuesJSONArray.getJSONObject(0);

		return objectAttributeValueJSONObject.getString("value");
	}

	private String _objectTypeAttributeId(JSONObject jsonObject) {
		return jsonObject.getString("objectTypeAttributeId");
	}

	private final Map<String, String> _attributeNameToIdsMap =
		HashMapBuilder.put(
			"Account", "200"
		).put(
			"Description", "300"
		).put(
			"Event Status", "400"
		).put(
			"Name", "100"
		).build();
	private final JSONObject _jsonObject = new JSONObject(
	).put(
		"attributes",
		new JSONArray(
		).put(
			_attributeJSONObject(
				"100",
				new JSONObject(
				).put(
					"value", "ACME"
				))
		).put(
			_attributeJSONObject(
				"400",
				new JSONObject(
				).put(
					"displayValue", "Done"
				).put(
					"value", "5"
				))
		).put(
			_attributeJSONObject(
				"200",
				new JSONObject(
				).put(
					"displayValue", "Acme Corp"
				).put(
					"referencedObject",
					new JSONObject(
					).put(
						"id", "OBJ-42"
					)
				))
		)
	).put(
		"id", "123"
	);

}