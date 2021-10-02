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

package com.liferay.account.admin.web.internal.tou.notifications;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = AdminCommerceTOCManager.class)
public class AdminCommerceTOCManager {

	public boolean isConfirmed(long userId) {
		if (!_isAdminUser(userId)) {
			return true;
		}

		if (_read(userId)) {
			return true;
		}

		return false;
	}

	public void confirm(long userId) {
		_write(userId);
	}

	private boolean _read(long userId) {
		PortalPreferences portalPreferences = _getPortalPreferences(userId);

		return GetterUtil.getBoolean(
			portalPreferences.getValue(
				_NAMESPACE,
				_ADMIN_COMMERCE_TOC_ACKNOWLEDGED
			)
		);
	}

	private static final String _NAMESPACE = AccountEntry.class.getName();

	private static final String _ADMIN_COMMERCE_TOC_ACKNOWLEDGED = "_ADMIN_COMMERCE_TOC_ACKNOWLEDGED";

	private void _write(long userId) {
		PortalPreferences portalPreferences = _getPortalPreferences(userId);

		portalPreferences.setValue(
			_NAMESPACE, _ADMIN_COMMERCE_TOC_ACKNOWLEDGED, String.valueOf(true));

		_portalPreferencesLocalService.updatePreferences(
			userId, PortletKeys.PREFS_OWNER_TYPE_USER, portalPreferences);
	}

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	private PortalPreferences _getPortalPreferences(long userId) {
		return _portletPreferencesFactory.getPortalPreferences(userId, true);
	}

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	private boolean _isAdminUser(long userId) {
		try {
			User user = _userLocalService.getUser(userId);

			Role role = RoleLocalServiceUtil.getRole(
				user.getCompanyId(), RoleConstants.ADMINISTRATOR);

			long[] roleIds =
				UserLocalServiceUtil.getRolePrimaryKeys(userId);

			if (ArrayUtil.contains(roleIds, role.getRoleId())) {
				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AdminCommerceTOCManager.class);

	@Reference
	private UserLocalService _userLocalService;
}
