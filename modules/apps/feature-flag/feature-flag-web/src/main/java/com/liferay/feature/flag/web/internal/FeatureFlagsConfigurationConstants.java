package com.liferay.feature.flag.web.internal;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsConfigurationConstants {

	public static final String CONFIGURATION_CATEGORY_KEY = "feature-flags";

	public static String getEntryKey(FeatureFlag.Status status) {
		return _CONFIGURATION_ENTRY_KEY_PREFIX + status.toString();
	}

	private static final String _CONFIGURATION_ENTRY_KEY_PREFIX = "feature-flags-";
}
