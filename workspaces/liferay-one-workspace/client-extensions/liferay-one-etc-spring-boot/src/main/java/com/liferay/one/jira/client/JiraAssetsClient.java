/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.client;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Transport for the JSM Assets (CMDB) API at
 * {@code api.atlassian.com/jsm/assets/workspace/{id}/v1}. Owns the URLs, the
 * basic auth header, and AQL paging. It speaks {@link JiraAssetObject}s and raw
 * JSON; it knows nothing about attribute names or any domain type.
 *
 * @author Drew Brokke
 */
@Component
public class JiraAssetsClient extends BaseService {

	public JSONObject createObject(
			String objectTypeId, JiraAssetObject jiraAssetObject)
		throws Exception {

		String response = post(
			new JSONObject(
			).put(
				"attributes", jiraAssetObject.toAttributesJSONArray()
			).put(
				"objectTypeId", objectTypeId
			).toString(),
			_headers(), _objectURI("create"));

		return new JSONObject(response);
	}

	public void deleteObject(String objectId) throws Exception {
		delete(_getAuthorization(), StringPool.BLANK, _objectURI(objectId));
	}

	public String findObjectKey(String aql) throws Exception {
		JSONArray valuesJSONArray = searchObjects(aql);

		if ((valuesJSONArray == null) || valuesJSONArray.isEmpty()) {
			return null;
		}

		JSONObject objectJSONObject = valuesJSONArray.getJSONObject(0);

		return objectJSONObject.optString("objectKey");
	}

	public JSONObject getObject(String objectId) throws Exception {
		return new JSONObject(get(_getAuthorization(), _objectURI(objectId)));
	}

	public JSONArray getObjectTypeAttributes(String objectTypeId)
		throws Exception {

		return new JSONArray(
			get(
				_getAuthorization(),
				_v1URI(
					StringBundler.concat(
						"objecttype/", objectTypeId, "/attributes"))));
	}

	public JSONArray searchObjects(String aql) throws Exception {
		JSONArray itemsJSONArray = new JSONArray();

		boolean last = false;
		int startAt = 0;

		while (!last) {
			JSONObject resultsJSONObject = _searchObjectsPage(aql, startAt);

			JSONArray valuesJSONArray = resultsJSONObject.optJSONArray(
				"values");

			if ((valuesJSONArray == null) || valuesJSONArray.isEmpty()) {
				break;
			}

			itemsJSONArray.putAll(valuesJSONArray);

			last = resultsJSONObject.optBoolean("last");

			startAt += _MAX_RESULTS;
		}

		return itemsJSONArray;
	}

	public JSONObject updateObject(
			String objectId, JiraAssetObject jiraAssetObject)
		throws Exception {

		String response = put(
			new JSONObject(
			).put(
				"attributes", jiraAssetObject.toAttributesJSONArray()
			).toString(),
			_headers(), _objectURI(objectId));

		return new JSONObject(response);
	}

	private String _getAuthorization() {
		Base64.Encoder encoder = Base64.getEncoder();

		String credentials =
			_jiraAPIEmailAddress + StringPool.COLON + _jiraAPIToken;

		return "Basic " +
			encoder.encodeToString(
				credentials.getBytes(StandardCharsets.UTF_8));
	}

	private Map<String, String> _headers() {
		return HashMapBuilder.put(
			HttpHeaders.AUTHORIZATION, _getAuthorization()
		).put(
			HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
		).build();
	}

	private URI _objectURI(String suffix) {
		return _v1URI("object/" + suffix);
	}

	private JSONObject _searchObjectsPage(String aql, int startAt)
		throws Exception {

		String response = post(
			new JSONObject(
			).put(
				"qlQuery", aql
			).toString(),
			_headers(),
			UriComponentsBuilder.fromUri(
				_v1URI("object/aql")
			).queryParam(
				"maxResults", _MAX_RESULTS
			).queryParam(
				"startAt", startAt
			).build(
			).toUri());

		if (Validator.isNull(response)) {
			return new JSONObject();
		}

		return new JSONObject(response);
	}

	private URI _v1URI(String path) {
		return UriComponentsBuilder.fromUriString(
			StringBundler.concat(
				_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/", _jiraWorkspaceId,
				"/v1/", path)
		).build(
		).toUri();
	}

	private static final String _JIRA_CLOUD_API_URL =
		"https://api.atlassian.com";

	private static final int _MAX_RESULTS = 100;

	@Value("${liferay.one.jira.api.email.address}")
	private String _jiraAPIEmailAddress;

	@Value("${liferay.one.jira.api.token}")
	private String _jiraAPIToken;

	@Value("${liferay.one.jira.workspace.id}")
	private String _jiraWorkspaceId;

}