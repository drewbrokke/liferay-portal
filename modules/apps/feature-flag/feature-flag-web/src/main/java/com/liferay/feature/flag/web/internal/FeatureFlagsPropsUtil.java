package com.liferay.feature.flag.web.internal;

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
		return GetterUtil.getBoolean(
			_get(key, StringPool.BLANK, null));
	}

	public static String getDescription(String key) {
		return _get(key, "description", StringPool.BLANK);
	}

	public static Set<String> getFeatureFlagsKeySet() {
		return _featureFlagKeysSet;
	}

	public static FeatureFlag.Status getStatus(String key) {
		return FeatureFlag.Status.fromString(_get(key, "status", StringPool.BLANK));
	}

	public static String getTitle(String key) {
		return _get(key, "title", key);
	}

	private static String _get(String key, String suffix, String defaultValue) {
		if (!_featureFlagKeysSet.contains(key)) {
			throw new IllegalArgumentException(
				String.format("%s is not a known feature flag key.", key));
		}

		if (Validator.isNotNull(suffix)) {
			key = key + "." + suffix;
		}

		String[] stringValues = StringUtil.split(_properties.getProperty(key));

		if (ArrayUtil.isEmpty(stringValues)) {
			return defaultValue;
		}

		return stringValues[stringValues.length - 1];
	}

	private static final Set<String> _featureFlagKeysSet;
	private static final Pattern _pattern = Pattern.compile("^([A-Z\\-0-9]+)$");
	private static final Properties _properties = PropsUtil.getProperties(
		"feature.flag.", true);

	static {
		Set<String> set = new HashSet<>();

		for (String stringPropertyName : _properties.stringPropertyNames()) {
			Matcher matcher = _pattern.matcher(stringPropertyName);

			if (matcher.find()) {
				set.add(stringPropertyName);
			}
		}

		_featureFlagKeysSet = Collections.unmodifiableSet(set);
	}

}