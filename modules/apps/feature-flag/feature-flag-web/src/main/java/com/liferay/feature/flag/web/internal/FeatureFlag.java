package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public interface FeatureFlag {

	public String getDescription();

	public String getKey();

	public Status getStatus();

	public String getTitle();

	public boolean isEnabled();

	public static enum Status {

		DEV, BETA, RELEASE;

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

		private static final Log _log = LogFactoryUtil.getLog(Status.class);

	}

}