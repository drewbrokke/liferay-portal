/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.one.jira.constants.ContactRoleConstants;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AssetSchemaService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Drew Brokke
 */
public class ContactRoleConverterTest {

	@BeforeEach
	public void setUp() {
		_contactRoleConverter = new ContactRoleConverter();

		Map<String, String> attributeIds = HashMapBuilder.put(
			ContactRoleConstants.ATTRIBUTE_NAME_TYPE,
			ContactRoleConstants.ATTRIBUTE_NAME_TYPE
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
			_contactRoleConverter, "_assetSchemaService", assetSchemaService);

		ReflectionTestUtils.setField(
			_contactRoleConverter, "_schemaName", "test-schema");
	}

	@Test
	public void testToAssetObjectTypeIsAccountCustomerWhenRoleIsNotEmployeeRole() {
		Role role = _role("Primary Business Contact");

		JiraAssetObject jiraAssetObject = _contactRoleConverter.toAssetObject(
			role);

		Assertions.assertEquals(
			"Account Customer",
			jiraAssetObject.getAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_TYPE));
	}

	@Test
	public void testToAssetObjectTypeIsAccountWorkerWhenRoleIsEmployeeRole() {
		Role role = _role("Liferay Sales");

		JiraAssetObject jiraAssetObject = _contactRoleConverter.toAssetObject(
			role);

		Assertions.assertEquals(
			"Account Worker",
			jiraAssetObject.getAttributeValue(
				ContactRoleConstants.ATTRIBUTE_NAME_TYPE));
	}

	private Role _role(String name) {
		Role role = new Role();

		role.setName(() -> name);

		return role;
	}

	private ContactRoleConverter _contactRoleConverter;

}