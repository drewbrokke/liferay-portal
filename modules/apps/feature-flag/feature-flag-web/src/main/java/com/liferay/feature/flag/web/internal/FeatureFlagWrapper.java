package com.liferay.feature.flag.web.internal;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class FeatureFlagWrapper implements FeatureFlag {

	public FeatureFlagWrapper(FeatureFlag featureFlag) {
		_featureFlag = featureFlag;
	}

	@Override
	public String getDescription(Locale locale) {
		return _featureFlag.getDescription(locale);
	}

	@Override
	public String getKey() {
		return _featureFlag.getKey();
	}

	@Override
	public Status getStatus() {
		return _featureFlag.getStatus();
	}

	@Override
	public String getTitle(Locale locale) {
		return _featureFlag.getTitle(locale);
	}

	@Override
	public boolean isEnabled() {
		return _featureFlag.isEnabled();
	}

	private final FeatureFlag _featureFlag;

}