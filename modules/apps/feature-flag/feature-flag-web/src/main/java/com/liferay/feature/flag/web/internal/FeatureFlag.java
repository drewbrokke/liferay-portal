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

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Locale;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public interface FeatureFlag {

	public String getDescription(Locale locale);

	public String getKey();

	public Status getStatus();

	public String getTitle(Locale locale);

	public boolean isEnabled();

	public static enum Status {

		BETA("beta"), DEV("dev"), RELEASE("release");

		public static Status fromString(String propertyValue) {
			for (Status status : values()) {
				if (StringUtil.equalsIgnoreCase(status._value, propertyValue)) {
					return status;
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Property value did not match a known feature flag " +
						"status. Returning the default value.");
			}

			return DEV;
		}

		public Predicate<FeatureFlag> getPredicate() {
			return featureFlag -> equals(featureFlag.getStatus());
		}

		public boolean isUIEnabled() {
			return _uIEnabled;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Status(String value) {
			_value = value;

			_uIEnabled = GetterUtil.getBoolean(
				PropsUtil.get("feature.flag.ui.visible", new Filter(_value)));
		}

		private static final Log _log = LogFactoryUtil.getLog(Status.class);

		private final boolean _uIEnabled;
		private final String _value;

	}

}