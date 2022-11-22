package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public interface FeatureFlag {

	public String getDescription();

	public String getKey();

	public Status getStatus();

	public default String getStatusString() {
		return String.valueOf(getStatus());
	}

	public String getTitle();

	public boolean isEnabled();

	public static enum Status {

		DEV(false), BETA(true), RELEASE(true);

		Status(boolean isUIEnabledDefaultValue) {
			_isUIEnabledDefaultValue = isUIEnabledDefaultValue;
		}

		public boolean isUIEnabledDefaultValue() {
			return _isUIEnabledDefaultValue;
		}

		private final boolean _isUIEnabledDefaultValue;

		public static Status fromString(String propertyValue) {
			for (Status status : values()) {
				if (Objects.equals(
						StringUtil.lowerCase(status.toString()),
						StringUtil.lowerCase(propertyValue))) {

					return status;
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Property value did not match a known feature flag " +
						"status, returning the default value.");
			}

			return DEV;
		}

		public boolean equals(Status status) {
			if (Objects.equals(this, status)) {
				return true;
			}

			return false;
		}

		public Predicate<FeatureFlag> getPredicate() {
			return featureFlag -> equals(featureFlag.getStatus());
		}

		private static final Log _log = LogFactoryUtil.getLog(Status.class);

		@Override
		public String toString() {
			return StringUtil.toLowerCase(super.toString());
		}
	}

}