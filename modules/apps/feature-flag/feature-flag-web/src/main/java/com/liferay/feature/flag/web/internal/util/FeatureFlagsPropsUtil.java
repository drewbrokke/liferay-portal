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

package com.liferay.feature.flag.web.internal.util;

import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.feature.flag.web.internal.model.FeatureFlag;
import com.liferay.feature.flag.web.internal.model.PropertyFeatureFlag;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsPropsUtil {

	public static boolean enabled(String key) {
		return GetterUtil.getBoolean(_get(key, StringPool.BLANK, null));
	}

	public static String getDescription(String key) {
		return _get(key, "description", StringPool.BLANK);
	}

	public static Set<FeatureFlag> getFeatureFlagSet() {
		return _featureFlagSet;
	}

	public static FeatureFlag.Status getStatus(String key) {
		return FeatureFlag.Status.fromString(
			_get(key, "status", StringPool.BLANK));
	}

	public static String getTitle(String key) {
		return _get(key, "title", key);
	}

	private static String _get(String key, String suffix, String defaultValue) {
		if (Validator.isNotNull(suffix)) {
			key = StringBundler.concat(key, StringPool.PERIOD, suffix);
		}

		String[] stringValues = StringUtil.split(_properties.getProperty(key));

		if (ArrayUtil.isEmpty(stringValues)) {
			return defaultValue;
		}

		return stringValues[stringValues.length - 1];
	}

	private static final Set<FeatureFlag> _featureFlagSet;
	private static final Pattern _pattern = Pattern.compile("^([A-Z\\-0-9]+)$");
	private static final Properties _properties = PropsUtil.getProperties(
		FeatureFlagConstants.FEATURE_FLAG + StringPool.PERIOD, true);

	static {
		Set<FeatureFlag> featureFlagSet = new HashSet<>();

		for (String stringPropertyName : _properties.stringPropertyNames()) {
			Matcher matcher = _pattern.matcher(stringPropertyName);

			if (matcher.find()) {
				featureFlagSet.add(
					new PropertyFeatureFlag(
						stringPropertyName, enabled(stringPropertyName),
						getStatus(stringPropertyName),
						getTitle(stringPropertyName),
						getDescription(stringPropertyName)));
			}
		}

		_featureFlagSet = Collections.unmodifiableSet(featureFlagSet);
	}

}