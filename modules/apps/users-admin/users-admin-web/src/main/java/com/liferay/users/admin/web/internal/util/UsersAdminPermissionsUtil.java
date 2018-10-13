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

package com.liferay.users.admin.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Samuel Trong Tran
 */
public class UsersAdminPermissionsUtil {

	public UsersAdminPermissionsUtil(HttpServletRequest request) {
		_request = request;

		_themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		_permissionChecker = _themeDisplay.getPermissionChecker();
	}

	public boolean showAddOrganizationAction(long organizationId)
		throws PortalException {

		if (PortalPermissionUtil.contains(
				_permissionChecker, ActionKeys.ADD_ORGANIZATION) &&
			((organizationId == 0) ||
			 OrganizationPermissionUtil.contains(
				 _permissionChecker, organizationId,
				 ActionKeys.MANAGE_SUBORGANIZATIONS))) {

			return true;
		}

		return false;
	}

	public boolean showAddOrganizationUserAction(long organizationId)
		throws PortalException {

		if (PortalPermissionUtil.contains(
				_permissionChecker, ActionKeys.ADD_USER) &&
			((organizationId == 0) ||
			 (OrganizationPermissionUtil.contains(
					_permissionChecker, organizationId,
					ActionKeys.ASSIGN_MEMBERS) &&
				 OrganizationPermissionUtil.contains(
					_permissionChecker, organizationId,
					ActionKeys.MANAGE_USERS)))) {

			return true;
		}

		return false;
	}

	public boolean showAssignMembersAction(long organizationId)
		throws PortalException {

		if ((organizationId != 0) &&
			OrganizationPermissionUtil.contains(
				_permissionChecker, organizationId,
				ActionKeys.ASSIGN_MEMBERS)) {

			return true;
		}

		return false;
	}

	public boolean showRemoveOrganizationAction(
			long organizationId, long parentOrganizationId)
		throws PortalException {

		if ((organizationId != 0) && (parentOrganizationId != 0) &&
			OrganizationPermissionUtil.contains(
				_permissionChecker, organizationId, ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	public boolean showRemoveUserAction(long organizationId, long userId)
		throws PortalException {

		if ((organizationId != 0) &&
			OrganizationPermissionUtil.contains(
				_permissionChecker, organizationId,
				ActionKeys.ASSIGN_MEMBERS) &&
			!OrganizationMembershipPolicyUtil.isMembershipProtected(
				_permissionChecker, userId, organizationId) &&
			!OrganizationMembershipPolicyUtil.isMembershipRequired(
				userId, organizationId)) {

			return true;
		}

		return false;
	}

	private final PermissionChecker _permissionChecker;
	private final HttpServletRequest _request;
	private final ThemeDisplay _themeDisplay;

}