/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagListenerRegistry.class)
public class FeatureFlagListenerRegistry {

	public void notifyListeners(
		long companyId, String featureFlagKey, boolean enabled) {

		for (FeatureFlagListener featureFlagListener :
				_featureFlagListenersMap.getOrDefault(
					featureFlagKey, Collections.emptyList())) {

			featureFlagListener.onValue(companyId, featureFlagKey, enabled);
		}
	}

	protected SafeCloseable registerFeatureFlagListener(
		String featureFlagKey, FeatureFlagListener featureFlagListener) {

		_featureFlagListenersMap.compute(
			featureFlagKey,
			(key, featureFlagListeners) -> {
				if (featureFlagListeners == null) {
					featureFlagListeners = new ArrayList<>();
				}

				featureFlagListeners.add(featureFlagListener);

				return featureFlagListeners;
			});

		return () -> _featureFlagListenersMap.compute(
			featureFlagKey,
			(key, featureFlagListeners) -> {
				Objects.requireNonNull(featureFlagListeners);

				featureFlagListeners.remove(featureFlagListener);

				if (ListUtil.isEmpty(featureFlagListeners)) {
					return null;
				}

				return featureFlagListeners;
			});
	}

	private final Map<String, List<FeatureFlagListener>>
		_featureFlagListenersMap = new ConcurrentHashMap<>();

}