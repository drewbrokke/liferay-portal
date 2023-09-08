/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.feature.flag.web.internal.manager.FeatureFlagPreferencesManager;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagsBagProvider.class)
public class FeatureFlagsBagProvider {

	public FeatureFlagsBag getOrCreateFeatureFlagsBag(long companyId) {
		return _featureFlagsBagMap.computeIfAbsent(
			companyId,
			key -> _featureFlagFactory.create(
				key, _featureFlagPreferencesManager, _language));
	}

	public void setEnabled(long companyId, String key, boolean enabled) {
		_featureFlagPreferencesManager.setEnabled(companyId, key, enabled);

		_setEnabled(companyId, key, enabled);

		if (!_clusterExecutor.isEnabled()) {
			return;
		}

		MethodHandler methodHandler = new MethodHandler(
			_setEnabledMethodKey, companyId, key, enabled);

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			methodHandler, true);

		clusterRequest.setFireAndForget(true);

		_clusterExecutor.execute(clusterRequest);
	}

	public <T> T withFeatureFlagsBag(
		long companyId, Function<FeatureFlagsBag, T> function) {

		return function.apply(getOrCreateFeatureFlagsBag(companyId));
	}

	private static void _setEnabled(
		long companyId, String key, boolean enabled) {

		FeatureFlagsBag featureFlagsBag = _featureFlagsBagMap.get(companyId);

		if (featureFlagsBag == null) {
			return;
		}

		featureFlagsBag.setEnabled(key, enabled);
	}

	private static final Map<Long, FeatureFlagsBag> _featureFlagsBagMap =
		new ConcurrentHashMap<>();
	private static final MethodKey _setEnabledMethodKey = new MethodKey(
		FeatureFlagsBagProvider.class, "_setEnabled", long.class, String.class,
		boolean.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

	private final FeatureFlagFactory _featureFlagFactory =
		new FeatureFlagFactory();

	@Reference
	private FeatureFlagPreferencesManager _featureFlagPreferencesManager;

	@Reference
	private Language _language;

}