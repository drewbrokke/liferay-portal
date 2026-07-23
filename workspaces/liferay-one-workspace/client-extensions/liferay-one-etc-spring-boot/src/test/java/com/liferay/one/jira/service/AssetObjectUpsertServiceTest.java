/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.converter.BaseJiraAssetObjectConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Map;

import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Drew Brokke
 */
public class AssetObjectUpsertServiceTest {

	@BeforeEach
	public void setUp() {
		_assetObjectUpsertService = new AssetObjectUpsertService();

		_converter = Mockito.mock(BaseJiraAssetObjectConverter.class);
		_jiraAssetService = Mockito.mock(JiraAssetService.class);

		Mockito.when(
			_converter.getExternalKeyAttributeName()
		).thenReturn(
			_ATTRIBUTE_NAME_EXTERNAL_KEY
		);

		Mockito.when(
			_converter.getExternalUpdatedAtAttributeName()
		).thenReturn(
			_ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT
		);

		Mockito.when(
			_converter.getAQLWithBuilder(Mockito.any())
		).thenReturn(
			"test-aql"
		);

		ReflectionTestUtils.setField(
			_assetObjectUpsertService, "_jiraAssetService", _jiraAssetService);
	}

	@Test
	public void testUpsertWithNoUnchangedPredicateAlwaysUpdates() {
		Mockito.when(
			_jiraAssetService.searchObjects(Mockito.anyString(), Mockito.any())
		).thenReturn(
			Collections.singletonList(
				_existingJiraAssetObject("1", "2026-01-01T00:00:00Z"))
		);

		_assetObjectUpsertService.upsert(
			_converter, _jiraAssetObject("role-1", "2026-01-01T00:00:00Z"));

		Mockito.verify(
			_jiraAssetService
		).updateObject(
			Mockito.anyString(), Mockito.any()
		);
	}

	@Test
	public void testUpsertWithUnchangedPredicateSkipsUpdateWhenTrue() {
		Mockito.when(
			_jiraAssetService.searchObjects(Mockito.anyString(), Mockito.any())
		).thenReturn(
			Collections.singletonList(
				_existingJiraAssetObject("1", "2026-01-01T00:00:00Z"))
		);

		_assetObjectUpsertService.upsert(
			_converter, _jiraAssetObject("role-1", "2026-01-01T00:00:00Z"),
			(existingJiraAssetObject, jiraAssetObject) ->
				_assetObjectUpsertService.isUnchangedByExternalUpdatedAt(
					_converter, existingJiraAssetObject, jiraAssetObject));

		Mockito.verify(
			_jiraAssetService, Mockito.never()
		).updateObject(
			Mockito.anyString(), Mockito.any()
		);
	}

	@Test
	public void testUpsertWithUnchangedPredicateUpdatesWhenFalse() {
		Mockito.when(
			_jiraAssetService.searchObjects(Mockito.anyString(), Mockito.any())
		).thenReturn(
			Collections.singletonList(
				_existingJiraAssetObject("1", "2026-01-01T00:00:00Z"))
		);

		_assetObjectUpsertService.upsert(
			_converter, _jiraAssetObject("role-1", "2026-02-01T00:00:00Z"),
			(existingJiraAssetObject, jiraAssetObject) ->
				_assetObjectUpsertService.isUnchangedByExternalUpdatedAt(
					_converter, existingJiraAssetObject, jiraAssetObject));

		Mockito.verify(
			_jiraAssetService
		).updateObject(
			Mockito.anyString(), Mockito.any()
		);
	}

	private JiraAssetObject _existingJiraAssetObject(
		String objectId, String externalUpdatedAt) {

		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			new JSONObject(
			).put(
				"id", objectId
			),
			_attributeIds, Collections.emptyMap());

		jiraAssetObject.setAttributeValue(
			_ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT, externalUpdatedAt);

		return jiraAssetObject;
	}

	private JiraAssetObject _jiraAssetObject(
		String externalKey, String externalUpdatedAt) {

		JiraAssetObject jiraAssetObject = new JiraAssetObject(
			_attributeIds, Collections.emptyMap());

		jiraAssetObject.setAttributeValue(
			_ATTRIBUTE_NAME_EXTERNAL_KEY, externalKey);
		jiraAssetObject.setAttributeValue(
			_ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT, externalUpdatedAt);

		return jiraAssetObject;
	}

	private static final String _ATTRIBUTE_NAME_EXTERNAL_KEY = "External Key";

	private static final String _ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT =
		"External Updated At";

	private AssetObjectUpsertService _assetObjectUpsertService;
	private final Map<String, String> _attributeIds = HashMapBuilder.put(
		_ATTRIBUTE_NAME_EXTERNAL_KEY, _ATTRIBUTE_NAME_EXTERNAL_KEY
	).put(
		_ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT, _ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT
	).build();
	private BaseJiraAssetObjectConverter _converter;
	private JiraAssetService _jiraAssetService;

}