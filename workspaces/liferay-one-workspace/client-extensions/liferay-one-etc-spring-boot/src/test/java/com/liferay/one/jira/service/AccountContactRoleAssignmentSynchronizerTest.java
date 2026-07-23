/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.constants.AccountContactRoleAssignmentConstants;
import com.liferay.one.jira.converter.AccountContactRoleAssignmentConverter;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.ContactRoleConverter;
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
public class AccountContactRoleAssignmentSynchronizerTest {

	@BeforeEach
	public void setUp() {
		_accountContactRoleAssignmentSynchronizer =
			new AccountContactRoleAssignmentSynchronizer();

		_accountContactRoleAssignmentConverter =
			new AccountContactRoleAssignmentConverter();

		Map<String, String> attributeIds = HashMapBuilder.put(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT
		).put(
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_ACCOUNT_EXTERNAL_KEY,
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_ACCOUNT_EXTERNAL_KEY
		).put(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT,
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT
		).put(
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_EXTERNAL_KEY,
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_EXTERNAL_KEY
		).put(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT_ROLE,
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT_ROLE
		).put(
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_ROLE_EXTERNAL_KEY,
			AccountContactRoleAssignmentConstants.
				ATTRIBUTE_NAME_CONTACT_ROLE_EXTERNAL_KEY
		).put(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED,
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED
		).put(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_NAME,
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_NAME
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
			_accountContactRoleAssignmentConverter, "_assetSchemaService",
			assetSchemaService);

		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentConverter, "_schemaName",
			"test-schema");

		_accountConverter = Mockito.mock(AccountConverter.class);
		_assetObjectUpsertService = Mockito.mock(
			AssetObjectUpsertService.class);
		_assetReferenceObjectService = Mockito.mock(
			AssetReferenceObjectService.class);
		_contactConverter = Mockito.mock(ContactConverter.class);
		_contactRoleConverter = Mockito.mock(ContactRoleConverter.class);

		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer,
			"_accountContactRoleAssignmentConverter",
			_accountContactRoleAssignmentConverter);
		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer, "_accountConverter",
			_accountConverter);
		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer,
			"_assetObjectUpsertService", _assetObjectUpsertService);
		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer,
			"_assetReferenceObjectService", _assetReferenceObjectService);
		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer, "_contactConverter",
			_contactConverter);
		ReflectionTestUtils.setField(
			_accountContactRoleAssignmentSynchronizer, "_contactRoleConverter",
			_contactRoleConverter);
	}

	@Test
	public void testSyncAssignContactRoleResolvesReferencesAndUpserts()
		throws Exception {

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_contactRoleConverter, "role-1")
		).thenReturn(
			"contact-role-object-id"
		);

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_contactConverter, "contact-1")
		).thenReturn(
			"contact-object-id"
		);

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_accountConverter, "account-1")
		).thenReturn(
			"account-object-id"
		);

		_accountContactRoleAssignmentSynchronizer.syncAssignContactRole(
			"role-1", "contact-1", "account-1");

		ArgumentCaptor<JiraAssetObject> jiraAssetObjectCaptor =
			ArgumentCaptor.forClass(JiraAssetObject.class);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_accountContactRoleAssignmentConverter),
			jiraAssetObjectCaptor.capture()
		);

		JiraAssetObject jiraAssetObject = jiraAssetObjectCaptor.getValue();

		Assertions.assertEquals(
			"role-1;contact-1;account-1",
			jiraAssetObject.getAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_NAME));
		Assertions.assertEquals(
			"contact-role-object-id",
			jiraAssetObject.getAttributeValue(
				AccountContactRoleAssignmentConstants.
					ATTRIBUTE_NAME_CONTACT_ROLE));
		Assertions.assertEquals(
			"contact-object-id",
			jiraAssetObject.getAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT));
		Assertions.assertEquals(
			"account-object-id",
			jiraAssetObject.getAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT));
		Assertions.assertEquals(
			"false",
			jiraAssetObject.getAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED));
	}

	@Test
	public void testSyncAssignContactRoleThrowsWhenReferenceIsUnresolved() {
		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				_contactRoleConverter, "role-1")
		).thenThrow(
			new JiraAssetObjectException(
				"No \"Contact Role\" asset object exists for external key " +
					"role-1")
		);

		Assertions.assertThrows(
			JiraAssetObjectException.class,
			() ->
				_accountContactRoleAssignmentSynchronizer.syncAssignContactRole(
					"role-1", "contact-1", "account-1"));

		Mockito.verify(
			_assetObjectUpsertService, Mockito.never()
		).upsert(
			Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testSyncUnassignContactRoleMarksAssignmentDeleted()
		throws Exception {

		Mockito.when(
			_assetReferenceObjectService.getReferenceObjectId(
				Mockito.any(), Mockito.anyString())
		).thenReturn(
			"object-id"
		);

		_accountContactRoleAssignmentSynchronizer.syncUnassignContactRole(
			"role-1", "contact-1", "account-1");

		ArgumentCaptor<JiraAssetObject> jiraAssetObjectCaptor =
			ArgumentCaptor.forClass(JiraAssetObject.class);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_accountContactRoleAssignmentConverter),
			jiraAssetObjectCaptor.capture()
		);

		Assertions.assertEquals(
			"true",
			jiraAssetObjectCaptor.getValue(
			).getAttributeValue(
				AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_DELETED
			));
	}

	private AccountContactRoleAssignmentConverter
		_accountContactRoleAssignmentConverter;
	private AccountContactRoleAssignmentSynchronizer
		_accountContactRoleAssignmentSynchronizer;
	private AccountConverter _accountConverter;
	private AssetObjectUpsertService _assetObjectUpsertService;
	private AssetReferenceObjectService _assetReferenceObjectService;
	private ContactConverter _contactConverter;
	private ContactRoleConverter _contactRoleConverter;

}