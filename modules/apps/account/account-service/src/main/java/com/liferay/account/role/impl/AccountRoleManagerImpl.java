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

package com.liferay.account.role.impl;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.role.AccountRoleManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = AccountRoleManager.class)
public class AccountRoleManagerImpl implements AccountRoleManager {

	@Override
	public Role createAccountRole(
			long userId, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		return _createAccountRoleImpl(userId, name, titleMap, descriptionMap);
	}

	@Override
	public List<Role> getAccountRoles(long companyId) {
		return _roleLocalService.getRoles(
			companyId, new int[] {AccountRoleConstants.TYPE_ACCOUNT});
	}

	@Activate
	protected void activate() {
		_accountRoleType = 5;
	}

	private Role _createAccountRoleImpl(
			long userId, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		return _roleLocalService.addRole(
			userId, AccountEntry.class.getName(), 0, name, titleMap,
			descriptionMap, _accountRoleType, null, null);
	}

	private int _accountRoleType = 5;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}