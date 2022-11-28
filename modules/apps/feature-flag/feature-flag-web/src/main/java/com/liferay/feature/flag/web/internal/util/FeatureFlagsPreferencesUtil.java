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

package com.liferay.feature.flag.web.internal.util;

import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portlet.PortalPreferencesWrapper;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsPreferencesUtil {

	public static Boolean isEnabled(long companyId, String key) {
		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		String value = portalPreferences.getValue(_NAMESPACE, key);

		if (value == null) {
			return null;
		}

		return GetterUtil.getBoolean(value);
	}

	public static void setEnabled(long companyId, String key, boolean enabled) {
		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		portalPreferences.setValue(_NAMESPACE, key, String.valueOf(enabled));

		PortalPreferencesLocalServiceUtil.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, portalPreferences);
	}

	private static PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper preferences =
			(PortalPreferencesWrapper)
				PortalPreferencesLocalServiceUtil.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return preferences.getPortalPreferencesImpl();
	}

	private static final String _NAMESPACE = FeatureFlagConstants.FEATURE_FLAG;

}