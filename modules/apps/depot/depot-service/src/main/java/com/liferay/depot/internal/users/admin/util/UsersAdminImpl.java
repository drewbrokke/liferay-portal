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

package com.liferay.depot.internal.users.admin.util;

import com.liferay.depot.internal.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.RolePermission;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.users.admin.kernel.util.UsersAdmin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(property = "service.ranking:Integer=100", service = UsersAdmin.class)
public class UsersAdminImpl implements UsersAdmin {

	@Override
	public void addPortletBreadcrumbEntries(
			Organization organization, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {
	}

	@Override
	public long[] addRequiredRoles(long userId, long[] roleIds)
		throws PortalException {

		return _usersAdmin.addRequiredRoles(userId, roleIds);
	}

	@Override
	public long[] addRequiredRoles(User user, long[] roleIds)
		throws PortalException {

		return _usersAdmin.addRequiredRoles(user, roleIds);
	}

	@Override
	public List<Role> filterGroupRoles(
			PermissionChecker permissionChecker, long groupId, List<Role> roles)
		throws PortalException {

		List<Role> filteredGroupRoles = ListUtil.copy(
			_usersAdmin.filterGroupRoles(permissionChecker, groupId, roles));

		Iterator<Role> itr = filteredGroupRoles.iterator();

		while (itr.hasNext()) {
			Role role = itr.next();

			if (Objects.equals(
					role.getName(), DepotRolesConstants.DEPOT_MEMBER)) {

				itr.remove();
			}
		}

		if (permissionChecker.isCompanyAdmin() ||
			permissionChecker.isGroupOwner(groupId)) {

			return filteredGroupRoles;
		}

		itr = filteredGroupRoles.iterator();

		while (itr.hasNext()) {
			Role groupRole = itr.next();

			String roleName = groupRole.getName();

			if (Objects.equals(
					roleName, DepotRolesConstants.DEPOT_ADMINISTRATOR) ||
				Objects.equals(roleName, DepotRolesConstants.DEPOT_OWNER) ||
				!_rolePermission.contains(
					permissionChecker, groupId, groupRole.getRoleId(),
					ActionKeys.ASSIGN_MEMBERS)) {

				itr.remove();
			}
		}

		return filteredGroupRoles;
	}

	@Override
	public List<Group> filterGroups(
			PermissionChecker permissionChecker, List<Group> groups)
		throws PortalException {

		return _usersAdmin.filterGroups(permissionChecker, groups);
	}

	@Override
	public List<Organization> filterOrganizations(
			PermissionChecker permissionChecker,
			List<Organization> organizations)
		throws PortalException {

		return _usersAdmin.filterOrganizations(
			permissionChecker, organizations);
	}

	@Override
	public List<Role> filterRoles(
		PermissionChecker permissionChecker, List<Role> roles) {

		List<Role> filteredRoles = ListUtil.copy(
			_usersAdmin.filterRoles(permissionChecker, roles));

		Iterator<Role> itr = filteredRoles.iterator();

		while (itr.hasNext()) {
			Role role = itr.next();

			if (Objects.equals(
					role.getName(), DepotRolesConstants.DEPOT_MEMBER)) {

				itr.remove();
			}
		}

		return filteredRoles;
	}

	@Override
	public long[] filterUnsetGroupUserIds(
			PermissionChecker permissionChecker, long groupId, long[] userIds)
		throws PortalException {

		return _usersAdmin.filterUnsetGroupUserIds(
			permissionChecker, groupId, userIds);
	}

	@Override
	public long[] filterUnsetOrganizationUserIds(
			PermissionChecker permissionChecker, long organizationId,
			long[] userIds)
		throws PortalException {

		return _usersAdmin.filterUnsetOrganizationUserIds(
			permissionChecker, organizationId, userIds);
	}

	@Override
	public List<UserGroupRole> filterUserGroupRoles(
			PermissionChecker permissionChecker,
			List<UserGroupRole> userGroupRoles)
		throws PortalException {

		return _usersAdmin.filterUserGroupRoles(
			permissionChecker, userGroupRoles);
	}

	@Override
	public List<UserGroup> filterUserGroups(
		PermissionChecker permissionChecker, List<UserGroup> userGroups) {

		return _usersAdmin.filterUserGroups(permissionChecker, userGroups);
	}

	@Override
	public List<Address> getAddresses(ActionRequest actionRequest) {
		return _usersAdmin.getAddresses(actionRequest);
	}

	@Override
	public List<Address> getAddresses(
		ActionRequest actionRequest, List<Address> defaultAddresses) {

		return _usersAdmin.getAddresses(actionRequest, defaultAddresses);
	}

	@Override
	public List<EmailAddress> getEmailAddresses(ActionRequest actionRequest) {
		return _usersAdmin.getEmailAddresses(actionRequest);
	}

	@Override
	public List<EmailAddress> getEmailAddresses(
		ActionRequest actionRequest, List<EmailAddress> defaultEmailAddresses) {

		return _usersAdmin.getEmailAddresses(
			actionRequest, defaultEmailAddresses);
	}

	@Override
	public long[] getGroupIds(PortletRequest portletRequest)
		throws PortalException {

		return ArrayUtil.toLongArray(
			LongStream.concat(
				Arrays.stream(_getGroupIds(portletRequest)),
				Arrays.stream(_usersAdmin.getGroupIds(portletRequest))
			).mapToObj(
				Long::valueOf
			).collect(
				Collectors.toSet()
			));
	}

	@Override
	public OrderByComparator<Group> getGroupOrderByComparator(
		String orderByCol, String orderByType) {

		return _usersAdmin.getGroupOrderByComparator(orderByCol, orderByType);
	}

	@Override
	public Long[] getOrganizationIds(List<Organization> organizations) {
		return _usersAdmin.getOrganizationIds(organizations);
	}

	@Override
	public long[] getOrganizationIds(PortletRequest portletRequest)
		throws PortalException {

		return _usersAdmin.getOrganizationIds(portletRequest);
	}

	@Override
	public OrderByComparator<Organization> getOrganizationOrderByComparator(
		String orderByCol, String orderByType) {

		return _usersAdmin.getOrganizationOrderByComparator(
			orderByCol, orderByType);
	}

	@Override
	public List<Organization> getOrganizations(Hits hits)
		throws PortalException {

		return _usersAdmin.getOrganizations(hits);
	}

	@Override
	public List<OrgLabor> getOrgLabors(ActionRequest actionRequest) {
		return _usersAdmin.getOrgLabors(actionRequest);
	}

	@Override
	public List<Phone> getPhones(ActionRequest actionRequest) {
		return _usersAdmin.getPhones(actionRequest);
	}

	@Override
	public List<Phone> getPhones(
		ActionRequest actionRequest, List<Phone> defaultPhones) {

		return _usersAdmin.getPhones(actionRequest, defaultPhones);
	}

	@Override
	public long[] getRoleIds(PortletRequest portletRequest)
		throws PortalException {

		return _usersAdmin.getRoleIds(portletRequest);
	}

	@Override
	public OrderByComparator<Role> getRoleOrderByComparator(
		String orderByCol, String orderByType) {

		return _usersAdmin.getRoleOrderByComparator(orderByCol, orderByType);
	}

	@Override
	public <T> String getUserColumnText(
		Locale locale, List<? extends T> list, Accessor<T, String> accessor,
		int count) {

		return _usersAdmin.getUserColumnText(locale, list, accessor, count);
	}

	@Override
	public long[] getUserGroupIds(PortletRequest portletRequest)
		throws PortalException {

		return _usersAdmin.getUserGroupIds(portletRequest);
	}

	@Override
	public OrderByComparator<UserGroup> getUserGroupOrderByComparator(
		String orderByCol, String orderByType) {

		return _usersAdmin.getUserGroupOrderByComparator(
			orderByCol, orderByType);
	}

	@Override
	public List<UserGroupRole> getUserGroupRoles(PortletRequest portletRequest)
		throws PortalException {

		return _usersAdmin.getUserGroupRoles(portletRequest);
	}

	@Override
	public List<UserGroup> getUserGroups(Hits hits) throws PortalException {
		return _usersAdmin.getUserGroups(hits);
	}

	@Override
	public OrderByComparator<User> getUserOrderByComparator(
		String orderByCol, String orderByType) {

		return _usersAdmin.getUserOrderByComparator(orderByCol, orderByType);
	}

	@Override
	public List<User> getUsers(Hits hits) throws PortalException {
		return _usersAdmin.getUsers(hits);
	}

	@Override
	public List<Website> getWebsites(ActionRequest actionRequest) {
		return _usersAdmin.getWebsites(actionRequest);
	}

	@Override
	public List<Website> getWebsites(
		ActionRequest actionRequest, List<Website> defaultWebsites) {

		return _usersAdmin.getWebsites(actionRequest, defaultWebsites);
	}

	@Override
	public boolean hasUpdateFieldPermission(
			PermissionChecker permissionChecker, User updatingUser,
			User updatedUser, String field)
		throws PortalException {

		return _usersAdmin.hasUpdateFieldPermission(
			permissionChecker, updatingUser, updatedUser, field);
	}

	@Override
	public long[] removeRequiredRoles(long userId, long[] roleIds)
		throws PortalException {

		return _usersAdmin.removeRequiredRoles(userId, roleIds);
	}

	@Override
	public long[] removeRequiredRoles(User user, long[] roleIds)
		throws PortalException {

		return _usersAdmin.removeRequiredRoles(user, roleIds);
	}

	@Override
	public void updateAddresses(
			String className, long classPK, List<Address> addresses)
		throws PortalException {

		_usersAdmin.updateAddresses(className, classPK, addresses);
	}

	@Override
	public void updateEmailAddresses(
			String className, long classPK, List<EmailAddress> emailAddresses)
		throws PortalException {

		_usersAdmin.updateEmailAddresses(className, classPK, emailAddresses);
	}

	@Override
	public void updateOrgLabors(long classPK, List<OrgLabor> orgLabors)
		throws PortalException {

		_usersAdmin.updateOrgLabors(classPK, orgLabors);
	}

	@Override
	public void updatePhones(String className, long classPK, List<Phone> phones)
		throws PortalException {

		_usersAdmin.updatePhones(className, classPK, phones);
	}

	@Override
	public void updateWebsites(
			String className, long classPK, List<Website> websites)
		throws PortalException {

		_usersAdmin.updateWebsites(className, classPK, websites);
	}

	private long[] _getGroupIds(PortletRequest portletRequest) {
		Set<Long> groupIds = Arrays.stream(
			StringUtil.split(
				ParamUtil.getString(portletRequest, "addDepotGroupIds"), 0L)
		).mapToObj(
			Long::valueOf
		).collect(
			Collectors.toSet()
		);

		long[] deleteGroupIds = StringUtil.split(
			ParamUtil.getString(portletRequest, "deleteDepotGroupIds"), 0L);

		for (long deletePrimaryKey : deleteGroupIds) {
			groupIds.remove(deletePrimaryKey);
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	@Reference
	private Portal _portal;

	@Reference
	private RolePermission _rolePermission;

	@Reference(
		target = "(&(original.bean=true)(bean.id=com.liferay.users.admin.kernel.util.UsersAdmin))"
	)
	private UsersAdmin _usersAdmin;

}