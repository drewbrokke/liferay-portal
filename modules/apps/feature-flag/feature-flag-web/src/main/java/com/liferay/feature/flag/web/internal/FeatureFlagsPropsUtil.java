package com.liferay.feature.flag.web.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
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

	public static boolean isUIEnabled(FeatureFlag.Status status) {
		return GetterUtil.getBoolean(
			PropsUtil.get(
				_prefix + "ui.visible", new Filter(status.toString())),
			status.isUIEnabledDefaultValue());
	}

	public static boolean enabled(String key) {
		return GetterUtil.getBoolean(
			_get(key, StringPool.BLANK, null));
	}

	public static String getDescription(String key) {
		return _get(key, "description", StringPool.BLANK);
	}

	public static FeatureFlag.Status getStatus(String key) {
		return FeatureFlag.Status.fromString(_get(key, "status", StringPool.BLANK));
	}

	public static String getTitle(String key) {
		return _get(key, "title", key);
	}

	private static String _get(String key, String suffix, String defaultValue) {
		if (Validator.isNotNull(suffix)) {
			key = key + "." + suffix;
		}

		String[] stringValues = StringUtil.split(_properties.getProperty(key));

		if (ArrayUtil.isEmpty(stringValues)) {
			return defaultValue;
		}

		return stringValues[stringValues.length - 1];
	}

	public static Set<FeatureFlag> getFeatureFlagSet() {
		return _featureFlagSet;
	}

	public static FeatureFlag create(String key) {
		return new PropertyFeatureFlag(
			key, enabled(key), getStatus(key), getTitle(key),
			getDescription(key));
	}

	private static final Set<FeatureFlag> _featureFlagSet;
	private static final Pattern _pattern = Pattern.compile("^([A-Z\\-0-9]+)$");

	private static final String _prefix = "feature.flag.";
	private static final Properties _properties = PropsUtil.getProperties(
		_prefix, true);

	static {
		Set<FeatureFlag> featureFlagSet = new HashSet<>();

		for (String stringPropertyName : _properties.stringPropertyNames()) {
			Matcher matcher = _pattern.matcher(stringPropertyName);

			if (matcher.find()) {
				featureFlagSet.add(create(stringPropertyName));
			}
		}

		_featureFlagSet = Collections.unmodifiableSet(featureFlagSet);
	}

}