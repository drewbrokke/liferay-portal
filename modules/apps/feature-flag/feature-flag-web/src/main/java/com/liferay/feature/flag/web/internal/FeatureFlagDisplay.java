package com.liferay.feature.flag.web.internal;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class FeatureFlagDisplay extends FeatureFlagWrapper {

	public FeatureFlagDisplay(FeatureFlag featureFlag) {
		super(featureFlag);
	}
	
	public String getBadgeDisplayStyle() {
		String statusString = getStatusString();

		if (Objects.equals(statusString, "beta")) {
			return "warning";
		}

		if (Objects.equals(statusString, "release")) {
			return "primary";
		}

		return "danger";
	}

}
