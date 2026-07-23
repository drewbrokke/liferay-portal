/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.constants.AccountTeamRoleAssignmentConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.AccountTeamRoleAssignmentConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.converter.TeamRoleConverter;
import com.liferay.one.jira.exception.JiraAssetObjectException;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Drew Brokke
 */
public class AccountTeamRoleAssignmentSynchronizerTest {

	@BeforeEach
	public void setUp() {
		_accountTeamRoleAssignmentSynchronizer =
			new AccountTeamRoleAssignmentSynchronizer();

		_accountTeamRoleAssignmentConverter =
			new AccountTeamRoleAssignmentConverter();

		Map<String, String> attributeIds = HashMapBuilder.put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT
		).put(
			AccountTeamRoleAssignmentConstants.
				ATTRIBUTE_NAME_ACCOUNT_EXTERNAL_KEY,
			AccountTeamRoleAssignmentConstants.
				ATTRIBUTE_NAME_ACCOUNT_EXTERNAL_KEY
		).put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED
		).put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_NAME,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_NAME
		).put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM
		).put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_EXTERNAL_KEY,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_EXTERNAL_KEY
		).put(
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_ROLE,
			AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_ROLE
		).put(
			AccountTeamRoleAssignmentConstants.
				ATTRIBUTE_NAME_TEAM_ROLE_EXTERNAL_KEY,
			AccountTeamRoleAssignmentConstants.
				ATTRIBUTE_NAME_TEAM_ROLE_EXTERNAL_KEY
		).build();

		AssetSchemaService assetSchemaService = Mockito.mock(
			AssetSchemaService.class);

		Mockito.when(
			assetSchemaService.getAttributeIds(Mockito.any(), Mockito.any())
		).thenReturn(
			attributeIds
		);

		Mockito.when(
			assetSchemaService.getAttributeOptions(Mockito.any(), Mockito.any())
		).thenReturn(
			Collections.emptyMap()
		);

		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentConverter, "_assetSchemaService",
			assetSchemaService);

		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentConverter, "_schemaName", "test-schema");

		_accountConverter = Mockito.mock(AccountConverter.class);
		_assetObjectUpsertService = Mockito.mock(
			AssetObjectUpsertService.class);
		_assetReferenceObjectService = Mockito.mock(
			AssetReferenceObjectService.class);
		_teamConverter = Mockito.mock(TeamConverter.class);
		_teamRoleConverter = Mockito.mock(TeamRoleConverter.class);

		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer,
			"_accountTeamRoleAssignmentConverter",
			_accountTeamRoleAssignmentConverter);
		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer, "_accountConverter",
			_accountConverter);
		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer, "_assetObjectUpsertService",
			_assetObjectUpsertService);
		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer,
			"_assetReferenceObjectService", _assetReferenceObjectService);
		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer, "_teamConverter",
			_teamConverter);
		ReflectionTestUtils.setField(
			_accountTeamRoleAssignmentSynchronizer, "_teamRoleConverter",
			_teamRoleConverter);
	}

	@Test
	public void testSyncAssignTeamRoleResolvesReferencesAndUpserts()
		throws Exception {

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_teamRoleConverter, "role-1")
		).thenReturn(
			"team-role-object-id"
		);

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_teamConverter, "team-1")
		).thenReturn(
			"team-object-id"
		);

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_accountConverter, "account-1")
		).thenReturn(
			"account-object-id"
		);

		_accountTeamRoleAssignmentSynchronizer.syncAssignTeamRole(
			"role-1", "team-1", "account-1");

		ArgumentCaptor<JiraAssetObject> jiraAssetObjectCaptor =
			ArgumentCaptor.forClass(JiraAssetObject.class);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_accountTeamRoleAssignmentConverter),
			jiraAssetObjectCaptor.capture()
		);

		JiraAssetObject jiraAssetObject = jiraAssetObjectCaptor.getValue();

		Assertions.assertEquals(
			"role-1;team-1;account-1",
			jiraAssetObject.getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_NAME));
		Assertions.assertEquals(
			"team-role-object-id",
			jiraAssetObject.getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM_ROLE));
		Assertions.assertEquals(
			"team-object-id",
			jiraAssetObject.getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_TEAM));
		Assertions.assertEquals(
			"account-object-id",
			jiraAssetObject.getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT));
		Assertions.assertEquals(
			"false",
			jiraAssetObject.getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED));
	}

	@Test
	public void testSyncAssignTeamRoleThrowsWhenReferenceIsUnresolved() {
		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_teamRoleConverter, "role-1")
		).thenThrow(
			new JiraAssetObjectException(
				"No \"Team Role\" asset object exists for external key role-1")
		);

		Assertions.assertThrows(
			JiraAssetObjectException.class,
			() -> _accountTeamRoleAssignmentSynchronizer.syncAssignTeamRole(
				"role-1", "team-1", "account-1"));

		Mockito.verify(
			_assetObjectUpsertService, Mockito.never()
		).upsert(
			Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testSyncUnassignTeamRoleMarksAssignmentDeleted()
		throws Exception {

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				Mockito.any(), Mockito.anyString())
		).thenReturn(
			"object-id"
		);

		_accountTeamRoleAssignmentSynchronizer.syncUnassignTeamRole(
			"role-1", "team-1", "account-1");

		ArgumentCaptor<JiraAssetObject> jiraAssetObjectCaptor =
			ArgumentCaptor.forClass(JiraAssetObject.class);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_accountTeamRoleAssignmentConverter),
			jiraAssetObjectCaptor.capture()
		);

		Assertions.assertEquals(
			"true",
			jiraAssetObjectCaptor.getValue(
			).getAttributeValue(
				AccountTeamRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED
			));
	}

	private AccountConverter _accountConverter;
	private AccountTeamRoleAssignmentConverter
		_accountTeamRoleAssignmentConverter;
	private AccountTeamRoleAssignmentSynchronizer
		_accountTeamRoleAssignmentSynchronizer;
	private AssetObjectUpsertService _assetObjectUpsertService;
	private AssetReferenceObjectService _assetReferenceObjectService;
	private TeamConverter _teamConverter;
	private TeamRoleConverter _teamRoleConverter;

}