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

package com.liferay.account.internal.instance.lifecycle;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class AddDefaultAccountRolesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		User defaultUser = company.getDefaultUser();

		if (!_exists(AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER)) {
			AccountRole accountRole = _addAccountRole(
				defaultUser.getUserId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER);

			_addResourcePermissions(
				accountRole.getRoleId(), _accountMemberResourceActionsMap);
		}

		if (!_exists(
				AccountRoleConstants.
					REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR)) {

			AccountRole accountRole = _addAccountRole(
				defaultUser.getUserId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

			_addResourcePermissions(
				accountRole.getRoleId(), _accountMemberResourceActionsMap);
			_addResourcePermissions(
				accountRole.getRoleId(),
				_accountAdministratorResourceActionsMap);
		}

		if (!_exists(AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER)) {
			_addRole(
				defaultUser.getUserId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);
		}
	}

	private AccountRole _addAccountRole(long userId, String roleName)
		throws PortalException {

		AccountRole accountRole = _accountRoleLocalService.createAccountRole(
			_counterLocalService.increment());

		Role role = _roleLocalService.addRole(
			userId, AccountRole.class.getName(), accountRole.getAccountRoleId(),
			roleName,
			HashMapBuilder.put(
				LocaleThreadLocal.getDefaultLocale(), roleName
			).build(),
			null, RoleConstants.TYPE_ACCOUNT, null, null);

		accountRole.setCompanyId(role.getCompanyId());
		accountRole.setAccountEntryId(
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT);
		accountRole.setRoleId(role.getRoleId());

		return _accountRoleLocalService.addAccountRole(accountRole);
	}

	private void _addResourcePermissions(
			long roleId, Map<String, String[]> resourceActionsMap)
		throws PortalException {

		for (Map.Entry<String, String[]> entry :
				resourceActionsMap.entrySet()) {

			for (String resourceAction : entry.getValue()) {
				String resourceName = entry.getKey();

				_resourcePermissionLocalService.addResourcePermission(
					CompanyThreadLocal.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", roleId,
					resourceAction);
			}
		}
	}

	private void _addRole(long userId, String roleName) throws PortalException {
		_roleLocalService.addRole(
			userId, null, 0, roleName,
			HashMapBuilder.put(
				LocaleThreadLocal.getDefaultLocale(), roleName
			).build(),
			_getDescriptionMap(roleName), RoleConstants.TYPE_REGULAR, null,
			null);
	}

	private boolean _exists(String roleName) {
		Role role = _roleLocalService.fetchRole(
			CompanyThreadLocal.getCompanyId(), roleName);

		if (role != null) {
			Map<Locale, String> descriptionMap = _getDescriptionMap(roleName);

			if (!Objects.equals(role.getDescriptionMap(), descriptionMap)) {
				role.setDescriptionMap(descriptionMap);

				_roleLocalService.updateRole(role);
			}

			return true;
		}

		return false;
	}

	private String _getDescription(Locale locale, String roleName) {
		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		if (Objects.equals(
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
				roleName)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"account-administrators-are-super-users-of-their-account");
		}
		else if (Objects.equals(
					AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
					roleName)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"account-managers-who-belong-to-an-organization-can-" +
					"administer-all-accounts-associated-to-that-organization");
		}
		else if (Objects.equals(
					AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER,
					roleName)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"all-users-who-belong-to-an-account-have-this-role-within-" +
					"that-account");
		}

		return StringPool.BLANK;
	}

	private Map<Locale, String> _getDescriptionMap(String roleName) {
		Set<Locale> availableLocales = _language.getAvailableLocales();

		Stream<Locale> stream = availableLocales.stream();

		return stream.map(
			locale -> new AbstractMap.SimpleEntry<>(
				locale, _getDescription(locale, roleName))
		).collect(
			Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
		);
	}

	private static final Map<String, String[]>
		_accountAdministratorResourceActionsMap = HashMapBuilder.put(
			AccountConstants.RESOURCE_NAME,
			new String[] {AccountActionKeys.ADD_ACCOUNT_ENTRY}
		).put(
			AccountEntry.class.getName(),
			new String[] {ActionKeys.UPDATE, ActionKeys.MANAGE_USERS}
		).build();
	private static final Map<String, String[]>
		_accountMemberResourceActionsMap = HashMapBuilder.put(
			AccountEntry.class.getName(), new String[] {ActionKeys.VIEW}
		).build();

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private Language _language;

	@Reference(target = "(bundle.symbolic.name=com.liferay.account.service)")
	private ResourceBundleLoader _resourceBundleLoader;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}