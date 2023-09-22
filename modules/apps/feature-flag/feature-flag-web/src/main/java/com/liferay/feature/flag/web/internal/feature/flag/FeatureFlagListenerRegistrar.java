/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Drew Brokke
 *
 * FeatureFlagManagerUtil.isEnabled("TEST-123");
 * FeatureFlagManagerUtil.isEnabled("TEST-456");
 * FeatureFlagManagerUtil.isEnabled("TEST-789");
 * FeatureFlagManagerUtil.isEnabled("TEST-000");
 */
@Component(service = {})
public class FeatureFlagListenerRegistrar
	implements ServiceTrackerCustomizer
		<FeatureFlagListener, List<SafeCloseable>> {

	@Override
	public List<SafeCloseable> addingService(
		ServiceReference<FeatureFlagListener> serviceReference) {

		FeatureFlagListener featureFlagListener = _bundleContext.getService(
			serviceReference);

		List<String> featureFlagKeys = new ArrayList<>();

		_propertyServiceReferenceMapper.map(
			serviceReference,
			key -> {
				if (key != null) {
					featureFlagKeys.add(key);
				}
			});

		if (featureFlagKeys.isEmpty()) {
			Class<? extends FeatureFlagListener> clazz =
				featureFlagListener.getClass();

			_log.error(
				StringBundler.concat(
					"No featureFlagKey property found on the feature flag ",
					"listener ", clazz.getName(), ". Skipping registration."));

			return null;
		}

		List<SafeCloseable> safeCloseables = new ArrayList<>();

		Predicate<FeatureFlag> featureFlagPredicate =
			featureFlag -> featureFlagKeys.contains(featureFlag.getKey());

		for (String featureFlagKey : featureFlagKeys) {
			safeCloseables.add(
				_featureFlagListenerRegistry.registerFeatureFlagListener(
					featureFlagKey, featureFlagListener));
		}

		Predicate<FeatureFlag> finalFeatureFlagPredicate = featureFlagPredicate;

		_companyLocalService.forEachCompanyId(
			companyId -> {
				for (FeatureFlag featureFlag :
						_featureFlagManager.getFeatureFlags(
							companyId, finalFeatureFlagPredicate)) {

					featureFlagListener.onValue(
						companyId, featureFlag.getKey(),
						featureFlag.isEnabled());
				}
			});

		for (FeatureFlag featureFlag :
				_featureFlagManager.getFeatureFlags(
					CompanyConstants.SYSTEM, finalFeatureFlagPredicate)) {

			featureFlagListener.onValue(
				CompanyConstants.SYSTEM, featureFlag.getKey(),
				featureFlag.isEnabled());
		}

		return safeCloseables;
	}

	@Override
	public void modifiedService(
		ServiceReference<FeatureFlagListener> serviceReference,
		List<SafeCloseable> safeCloseables) {
	}

	@Override
	public void removedService(
		ServiceReference<FeatureFlagListener> serviceReference,
		List<SafeCloseable> safeCloseables) {

		for (SafeCloseable safeCloseable : safeCloseables) {
			safeCloseable.close();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext, FeatureFlagListener.class, this);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagListenerRegistrar.class);

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FeatureFlagListenerRegistry _featureFlagListenerRegistry;

	@Reference
	private FeatureFlagManager _featureFlagManager;

	private final PropertyServiceReferenceMapper<String, FeatureFlagListener>
		_propertyServiceReferenceMapper = new PropertyServiceReferenceMapper<>(
			"featureFlagKey");
	private ServiceTracker<FeatureFlagListener, List<SafeCloseable>>
		_serviceTracker;

}