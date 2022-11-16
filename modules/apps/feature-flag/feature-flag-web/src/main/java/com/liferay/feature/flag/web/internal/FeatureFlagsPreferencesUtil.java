package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsPreferencesUtil {

	public static Boolean isEnabled(long companyId, String key) {
//		PortalPreferencesLocalServiceUtil.getPreferences(
//			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return null;
	}

	public static void setEnabled(long companyId, String key, boolean enabled) {

	}

	private static final String _NAMESPACE = "feature.flags";

}
