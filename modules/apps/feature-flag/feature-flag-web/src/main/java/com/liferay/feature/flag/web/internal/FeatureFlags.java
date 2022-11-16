package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class FeatureFlags {

	public FeatureFlags(Locale locale, long companyId) {
		Map<String, FeatureFlag> map = new HashMap<>();

		for (String featureFlagKey :
				FeatureFlagsPropsUtil.getFeatureFlagsKeySet()) {

			map.put(featureFlagKey, _create(featureFlagKey, locale, companyId));
		}

		_featureFlagMap = Collections.unmodifiableMap(map);
	}

	public FeatureFlag get(String key) {
		return _featureFlagMap.get(key);
	}

	public List<FeatureFlag> getFeatureFlags() {
		return getFeatureFlags(featureFlag -> true);
	}

	public List<FeatureFlag> getFeatureFlags(Predicate<FeatureFlag> predicate) {
		Collection<FeatureFlag> values = _featureFlagMap.values();

		if (predicate == null) {
			return new ArrayList<>(values);
		}

		Stream<FeatureFlag> stream = values.stream();

		return stream.filter(
			predicate
		).sorted(
			Comparator.comparing(FeatureFlag::getKey)
		).collect(
			Collectors.toList()
		);
	}

	private FeatureFlag _create(String key, Locale locale, long companyId) {
		FeatureFlag featureFlag = new PropertyFeatureFlag(
			key, FeatureFlagsPropsUtil.enabled(key),
			FeatureFlagsPropsUtil.getStatus(key),
			FeatureFlagsPropsUtil.getTitle(key),
			FeatureFlagsPropsUtil.getDescription(key));

		featureFlag = new LanguageAwareFeatureFlag(
			featureFlag, LanguageUtil.getLanguage(), locale);

		featureFlag = new PreferenceAwareFeatureFlag(
			featureFlag, companyId);

		return featureFlag;
	}

	private final Map<String, FeatureFlag> _featureFlagMap;

}