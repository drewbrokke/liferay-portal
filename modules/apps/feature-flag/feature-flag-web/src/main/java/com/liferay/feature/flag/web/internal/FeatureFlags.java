package com.liferay.feature.flag.web.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class FeatureFlags {

	public FeatureFlags(long companyId) {
		Map<String, FeatureFlag> map = new HashMap<>();

		for (FeatureFlag featureFlag : FeatureFlagsPropsUtil.getFeatureFlagSet()) {
			featureFlag = new LanguageAwareFeatureFlag(featureFlag);
			featureFlag = new PreferenceAwareFeatureFlag(
				featureFlag, companyId);

			map.put(featureFlag.getKey(), featureFlag);
		}

		_featureFlagMap = Collections.unmodifiableMap(map);
	}

	public boolean isEnabled(String key) {
		FeatureFlag featureFlag = _featureFlagMap.get(key);

		if (featureFlag == null) {
			return FeatureFlagsPropsUtil.enabled(key);
		}

		return featureFlag.isEnabled();
	}

	public FeatureFlag get(String key) {
		return _featureFlagMap.get(key);
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

	private final Map<String, FeatureFlag> _featureFlagMap;

}