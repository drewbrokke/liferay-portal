/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.synchronizer;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountContactInformation;
import com.liferay.one.jira.converter.ExternalLinkConverter;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.model.Project;
import com.liferay.one.model.ProjectMembership;
import com.liferay.one.model.Property;
import com.liferay.one.service.EntitlementService;
import com.liferay.one.service.ProjectMembershipService;
import com.liferay.one.service.ProjectService;
import com.liferay.one.service.PropertyService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class UserAccountSyncModel {

	public UserAccountSyncModel(
		EntitlementService entitlementService,
		ExternalLinkConverter externalLinkConverter,
		ProjectMembershipService projectMembershipService,
		ProjectService projectService, PropertyService propertyService,
		UserAccount userAccount) {

		_entitlementService = entitlementService;
		_externalLinkConverter = externalLinkConverter;
		_projectMembershipService = projectMembershipService;
		_projectService = projectService;
		_propertyService = propertyService;
		_userAccount = userAccount;
	}

	public List<AccountBrief> getAccountBriefs() {
		if (_accountBriefs == null) {
			_accountBriefs = ListUtil.fromArray(
				_userAccount.getAccountBriefs());
		}

		return _accountBriefs;
	}

	public List<EntitlementDefinition> getEntitlementDefinitions()
		throws Exception {

		if (_entitlementDefinitions != null) {
			return _entitlementDefinitions;
		}

		_entitlementDefinitions = new ArrayList<>();

		for (AccountBrief accountBrief : getAccountBriefs()) {
			_entitlementDefinitions.addAll(
				_entitlementService.getActiveEntitlementDefinitions(
					accountBrief.getId()));
		}

		return _entitlementDefinitions;
	}

	public List<Property> getExternalLinkProperties() throws Exception {
		if (_externalLinkProperties != null) {
			return _externalLinkProperties;
		}

		_externalLinkProperties = new ArrayList<>();

		for (Property property : _getUserAccountProperties()) {
			if (_externalLinkConverter.isExternalLinkProperty(property)) {
				_externalLinkProperties.add(property);
			}
		}

		return _externalLinkProperties;
	}

	public String getExternalReferenceCode() {
		return _userAccount.getExternalReferenceCode();
	}

	public List<OrganizationBrief> getOrganizationBriefs() {
		if (_organizationBriefs == null) {
			_organizationBriefs = ListUtil.fromArray(
				_userAccount.getOrganizationBriefs());
		}

		return _organizationBriefs;
	}

	public List<Project> getProjects() throws Exception {
		ArrayList<Project> projects = new ArrayList<>();

		List<ProjectMembership> projectMemberships =
			_projectMembershipService.getProjectMemberships(
				_userAccount.getId());

		for (ProjectMembership projectMembership : projectMemberships) {
			projects.add(
				_projectService.getProject(
					projectMembership.getProjectExternalReferenceCode()));
		}

		return projects;
	}

	public List<RoleBrief> getRoleBriefs() {
		if (_roleBriefs != null) {
			return _roleBriefs;
		}

		_roleBriefs = new ArrayList<>();

		for (AccountBrief accountBrief : getAccountBriefs()) {
			RoleBrief[] accountRoleBriefs = accountBrief.getRoleBriefs();

			if (accountRoleBriefs != null) {
				Collections.addAll(_roleBriefs, accountRoleBriefs);
			}
		}

		for (OrganizationBrief organizationBrief : getOrganizationBriefs()) {
			RoleBrief[] organizationRoleBriefs =
				organizationBrief.getRoleBriefs();

			if (organizationRoleBriefs != null) {
				Collections.addAll(_roleBriefs, organizationRoleBriefs);
			}
		}

		return _roleBriefs;
	}

	public List<Phone> getTelephones() {
		if (_telephones != null) {
			return _telephones;
		}

		UserAccountContactInformation userAccountContactInformation =
			_userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			_telephones = Collections.emptyList();
		}
		else {
			_telephones = ListUtil.fromArray(
				userAccountContactInformation.getTelephones());
		}

		return _telephones;
	}

	public UserAccount getUserAccount() {
		return _userAccount;
	}

	private List<Property> _getUserAccountProperties() throws Exception {
		if (_userAccountProperties == null) {
			_userAccountProperties = _propertyService.getUserAccountProperties(
				_userAccount.getId());
		}

		return _userAccountProperties;
	}

	private List<AccountBrief> _accountBriefs;
	private List<EntitlementDefinition> _entitlementDefinitions;
	private final EntitlementService _entitlementService;
	private final ExternalLinkConverter _externalLinkConverter;
	private List<Property> _externalLinkProperties;
	private List<OrganizationBrief> _organizationBriefs;
	private final ProjectMembershipService _projectMembershipService;
	private final ProjectService _projectService;
	private final PropertyService _propertyService;
	private List<RoleBrief> _roleBriefs;
	private List<Phone> _telephones;
	private final UserAccount _userAccount;
	private List<Property> _userAccountProperties;

}