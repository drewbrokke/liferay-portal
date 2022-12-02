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

import com.liferay.feature.flag.web.internal.model.FeatureFlag;
import com.liferay.feature.flag.web.internal.model.FeatureFlagImpl;
import com.liferay.feature.flag.web.internal.model.LanguageAwareFeatureFlag;
import com.liferay.feature.flag.web.internal.model.PreferenceAwareFeatureFlag;
import com.liferay.feature.flag.web.internal.util.FeatureFlagsJSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public class CompanyFeatureFlags {

	public CompanyFeatureFlags(
		long companyId,
		FeatureFlagsPreferencesHelper featureFlagsPreferencesHelper,
		Language language) {

		Map<String, FeatureFlag> map = new HashMap<>();

		for (String key : _featureFlagsPropsHelper.getKeySet()) {
			FeatureFlag featureFlag = new FeatureFlagImpl(
				key, _featureFlagsPropsHelper.isEnabled(key),
				_featureFlagsPropsHelper.getStatus(key),
				_featureFlagsPropsHelper.getTitle(key),
				_featureFlagsPropsHelper.getDescription(key));

			if (isFeatureEnabled) {
				featureFlag = new LanguageAwareFeatureFlag(featureFlag, language);
				featureFlag = new PreferenceAwareFeatureFlag(
					featureFlag, companyId, featureFlagsPreferencesHelper);
			}

			map.put(featureFlag.getKey(), featureFlag);
		}

		_featureFlagMap = Collections.unmodifiableMap(map);
	}

	public List<FeatureFlag> getFeatureFlags(Predicate<FeatureFlag> predicate) {
		List<FeatureFlag> featureFlags = new ArrayList<>();

		if (predicate == null) {
			predicate = featureFlag -> true;
		}

		for (FeatureFlag featureFlag : _featureFlagMap.values()) {
			if (predicate.test(featureFlag)) {
				featureFlags.add(featureFlag);
			}
		}

		featureFlags.sort(Comparator.comparing(FeatureFlag::getKey));

		return featureFlags;
	}

	public String getFeatureFlagsJSON() {
		if (isFeatureEnabled) {
			Collection<FeatureFlag> featureFlags = _featureFlagMap.values();

			return FeatureFlagsJSONUtil.toJSON(
				featureFlags.toArray(new FeatureFlag[0]));
		}

		return PropsValues.FEATURE_FLAGS_JSON;
	}

	public boolean isEnabled(String key) {
		FeatureFlag featureFlag = _featureFlagMap.get(key);

		if (featureFlag != null) {
			return featureFlag.isEnabled();
		}

		return _featureFlagsPropsHelper.isEnabled(key);

	}

	private final Map<String, FeatureFlag> _featureFlagMap;
	private final FeatureFlagsPropsHelper _featureFlagsPropsHelper =
		new FeatureFlagsPropsHelper();

	private final boolean isFeatureEnabled = _featureFlagsPropsHelper.isEnabled("LPS-167698");

}