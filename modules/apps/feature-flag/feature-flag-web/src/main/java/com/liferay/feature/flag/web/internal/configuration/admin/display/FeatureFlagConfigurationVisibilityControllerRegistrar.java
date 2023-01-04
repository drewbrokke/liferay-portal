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

package com.liferay.feature.flag.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.feature.flag.web.internal.company.feature.flags.CompanyFeatureFlagsProvider;
import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.feature.flag.web.internal.model.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class FeatureFlagConfigurationVisibilityControllerRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		List<FeatureFlag> featureFlags =
			_companyFeatureFlagsProvider.withCompanyFeatureFlags(
				CompanyConstants.SYSTEM,
				companyFeatureFlags -> companyFeatureFlags.getFeatureFlags(
					null));

		for (FeatureFlag featureFlag : featureFlags) {
			ServiceRegistration<ConfigurationVisibilityController>
				serviceRegistration = bundleContext.registerService(
					ConfigurationVisibilityController.class,
					new FeatureFlagConfigurationVisibilityController(
						_featureFlagManager, _groupLocalService,
						featureFlag.getKey()),
					HashMapDictionaryBuilder.put(
						"visibility.controller.key",
						FeatureFlagConstants.getKey(featureFlag.getKey())
					).build());

			_serviceRegistrations.add(serviceRegistration);
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Reference
	private CompanyFeatureFlagsProvider _companyFeatureFlagsProvider;

	@Reference
	private FeatureFlagManager _featureFlagManager;

	@Reference
	private GroupLocalService _groupLocalService;

	private final List<ServiceRegistration<ConfigurationVisibilityController>>
		_serviceRegistrations = new ArrayList<>();

}