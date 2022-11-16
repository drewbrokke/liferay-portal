package com.liferay.feature.flag.web.internal;


import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Drew Brokke
 */
public class PreferenceAwareFeatureFlag extends FeatureFlagWrapper {

	public PreferenceAwareFeatureFlag(FeatureFlag featureFlag, long companyId) {
		super(featureFlag);

		_companyId = companyId;
	}

	private final long _companyId;

	@Override
	public boolean isEnabled() {
		return GetterUtil.getBoolean(
			FeatureFlagsPreferencesUtil.isEnabled(_companyId, getKey()),
			super.isEnabled());
	}
}
