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

package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public interface FeatureFlag {

	public String getDescription(Locale locale);

	public String getKey();

	public Status getStatus();

	public default String getStatusString() {
		return String.valueOf(getStatus());
	}

	public String getTitle(Locale locale);

	public boolean isEnabled();

	public static enum Status {

		BETA(true), DEV(false), RELEASE(true);

		public static Status fromString(String propertyValue) {
			for (Status status : values()) {
				if (status.equals(propertyValue)) {
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

		public static Predicate<FeatureFlag> getPredicate(Status status) {
			return featureFlag -> status.equals(featureFlag.getStatus());
		}

		public boolean equals(Status status) {
			if (Objects.equals(this, status)) {
				return true;
			}

			return false;
		}

		public boolean equals(String status) {
			if (Objects.equals(toString(), StringUtil.toLowerCase(status))) {
				return true;
			}

			return false;
		}

		public boolean isUIEnabledDefaultValue() {
			return _isUIEnabledDefaultValue;
		}

		@Override
		public String toString() {
			return StringUtil.toLowerCase(super.toString());
		}

		private Status(boolean isUIEnabledDefaultValue) {
			_isUIEnabledDefaultValue = isUIEnabledDefaultValue;
		}

		private static final Log _log = LogFactoryUtil.getLog(Status.class);

		private final boolean _isUIEnabledDefaultValue;

	}

}