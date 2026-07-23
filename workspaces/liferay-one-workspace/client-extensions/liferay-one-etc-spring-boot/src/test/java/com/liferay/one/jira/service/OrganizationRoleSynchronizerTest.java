/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.one.jira.converter.TeamRoleConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.service.RoleService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Drew Brokke
 */
public class OrganizationRoleSynchronizerTest {

	@BeforeEach
	public void setUp() {
		_organizationRoleSynchronizer = new OrganizationRoleSynchronizer();

		_assetObjectUpsertService = Mockito.mock(
			AssetObjectUpsertService.class);
		_roleService = Mockito.mock(RoleService.class);
		_teamRoleConverter = Mockito.mock(TeamRoleConverter.class);

		ReflectionTestUtils.setField(
			_organizationRoleSynchronizer, "_assetObjectUpsertService",
			_assetObjectUpsertService);
		ReflectionTestUtils.setField(
			_organizationRoleSynchronizer, "_roleService", _roleService);
		ReflectionTestUtils.setField(
			_organizationRoleSynchronizer, "_teamRoleConverter",
			_teamRoleConverter);
	}

	@Test
	public void testSyncOrganizationRolesContinuesAfterUpsertFailure()
		throws Exception {

		Role role1 = _role("role-1");
		Role role2 = _role("role-2");

		JiraAssetObject jiraAssetObject1 = Mockito.mock(JiraAssetObject.class);
		JiraAssetObject jiraAssetObject2 = Mockito.mock(JiraAssetObject.class);

		Mockito.when(
			_roleService.getOrganizationRoles()
		).thenReturn(
			Arrays.asList(role1, role2)
		);

		Mockito.when(
			_teamRoleConverter.toAssetObject(role1)
		).thenReturn(
			jiraAssetObject1
		);

		Mockito.when(
			_teamRoleConverter.toAssetObject(role2)
		).thenReturn(
			jiraAssetObject2
		);

		Mockito.doThrow(
			new RuntimeException("upsert failed")
		).when(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_teamRoleConverter), Mockito.eq(jiraAssetObject1),
			Mockito.any()
		);

		_organizationRoleSynchronizer.syncOrganizationRoles();

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_teamRoleConverter), Mockito.eq(jiraAssetObject1),
			Mockito.any()
		);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_teamRoleConverter), Mockito.eq(jiraAssetObject2),
			Mockito.any()
		);
	}

	@Test
	public void testSyncOrganizationRolesUpsertsEachRole() throws Exception {
		Role role = _role("role-1");

		JiraAssetObject jiraAssetObject = Mockito.mock(JiraAssetObject.class);

		Mockito.when(
			_roleService.getOrganizationRoles()
		).thenReturn(
			Collections.singletonList(role)
		);

		Mockito.when(
			_teamRoleConverter.toAssetObject(role)
		).thenReturn(
			jiraAssetObject
		);

		_organizationRoleSynchronizer.syncOrganizationRoles();

		ArgumentCaptor<JiraAssetObject> jiraAssetObjectCaptor =
			ArgumentCaptor.forClass(JiraAssetObject.class);

		Mockito.verify(
			_assetObjectUpsertService
		).upsert(
			Mockito.eq(_teamRoleConverter), jiraAssetObjectCaptor.capture(),
			Mockito.any()
		);

		Assertions.assertEquals(
			jiraAssetObject, jiraAssetObjectCaptor.getValue());
	}

	private Role _role(String externalReferenceCode) {
		Role role = new Role();

		role.setExternalReferenceCode(() -> externalReferenceCode);
		role.setName(() -> externalReferenceCode);

		return role;
	}

	private AssetObjectUpsertService _assetObjectUpsertService;
	private OrganizationRoleSynchronizer _organizationRoleSynchronizer;
	private RoleService _roleService;
	private TeamRoleConverter _teamRoleConverter;

}