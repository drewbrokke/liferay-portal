package com.liferay.feature.flag.web.internal;

/**
 * @author Drew Brokke
 */
public class FeatureFlagWrapper implements FeatureFlag {

	public FeatureFlagWrapper(FeatureFlag featureFlag) {
		_featureFlag = featureFlag;
	}

	@Override
	public String getDescription() {
		return _featureFlag.getDescription();
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
	public String getTitle() {
		return _featureFlag.getTitle();
	}

	@Override
	public boolean isEnabled() {
		return _featureFlag.isEnabled();
	}

	private final FeatureFlag _featureFlag;

}