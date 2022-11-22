package com.liferay.feature.flag.web.internal;

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
			(PortalPreferencesWrapper)PortalPreferencesLocalServiceUtil.getPreferences(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return preferences.getPortalPreferencesImpl();
	}

	private static final String _NAMESPACE = "feature.flags";

}
