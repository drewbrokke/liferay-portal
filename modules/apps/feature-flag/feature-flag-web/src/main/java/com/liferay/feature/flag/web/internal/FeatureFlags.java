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

		for (FeatureFlag featureFlag :
				FeatureFlagsPropsUtil.getFeatureFlagSet()) {

			featureFlag = new LanguageAwareFeatureFlag(featureFlag);
			featureFlag = new PreferenceAwareFeatureFlag(
				featureFlag, companyId);

			map.put(featureFlag.getKey(), featureFlag);
		}

		_featureFlagMap = Collections.unmodifiableMap(map);
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

	public boolean isEnabled(String key) {
		FeatureFlag featureFlag = _featureFlagMap.get(key);

		if (featureFlag == null) {
			return FeatureFlagsPropsUtil.enabled(key);
		}

		return featureFlag.isEnabled();
	}

	private final Map<String, FeatureFlag> _featureFlagMap;

}