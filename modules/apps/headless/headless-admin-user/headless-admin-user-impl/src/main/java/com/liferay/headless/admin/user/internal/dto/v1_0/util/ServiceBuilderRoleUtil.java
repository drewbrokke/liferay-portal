/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author István Sajtos
 */
public class ServiceBuilderRoleUtil {

	public static List<Role> getAllRoles(User user) throws Exception {
		List<Role> roles = RoleServiceUtil.getUserRoles(user.getUserId());

		List<Group> roleGroups = ListUtil.filter(
			_getAllGroups(user),
			group -> RoleLocalServiceUtil.hasGroupRoles(group.getGroupId()));

		for (Group group : roleGroups) {
			List<Role> groupRoles = RoleLocalServiceUtil.getGroupRoles(
				group.getGroupId());

			for (Role role : groupRoles) {
				if (!roles.contains(role)) {
					roles.add(role);
				}
			}
		}

		return roles;
	}

	private static List<Group> _getAllGroups(User user) throws Exception {
		List<Group> allGroups = new ArrayList<>();

		allGroups.addAll(_getGroups(user));
		allGroups.addAll(_getInheritedSiteGroups(user));
		allGroups.addAll(
			GroupLocalServiceUtil.getOrganizationsGroups(
				_getOrganizations(user)));
		allGroups.addAll(
			GroupLocalServiceUtil.getUserGroupsGroups(_getUserGroups(user)));

		return allGroups;
	}

	private static List<Group> _getGroups(User user) throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return user.getGroups();
		}

		return UsersAdminUtil.filterGroups(permissionChecker, user.getGroups());
	}

	private static List<Group> _getInheritedSiteGroups(User user)
		throws Exception {

		SortedSet<Group> inheritedSiteGroupsSet = new TreeSet<>();

		inheritedSiteGroupsSet.addAll(
			GroupLocalServiceUtil.getUserGroupsRelatedGroups(
				_getUserGroups(user)));
		inheritedSiteGroupsSet.addAll(_getOrganizationRelatedGroups(user));

		return ListUtil.fromCollection(inheritedSiteGroupsSet);
	}

	private static List<Group> _getOrganizationRelatedGroups(User user)
		throws Exception {

		List<Organization> organizations = _getOrganizations(user);

		if (organizations.isEmpty()) {
			return Collections.emptyList();
		}

		return GroupLocalServiceUtil.getOrganizationsRelatedGroups(
			organizations);
	}

	private static List<Organization> _getOrganizations(User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin() ||
			permissionChecker.hasPermission(
				null, Organization.class.getName(),
				Organization.class.getName(), ActionKeys.VIEW)) {

			return user.getOrganizations();
		}

		return UsersAdminUtil.filterOrganizations(
			permissionChecker, user.getOrganizations());
	}

	private static List<UserGroup> _getUserGroups(User user) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return user.getUserGroups();
		}

		return UsersAdminUtil.filterUserGroups(
			permissionChecker, user.getUserGroups());
	}

}