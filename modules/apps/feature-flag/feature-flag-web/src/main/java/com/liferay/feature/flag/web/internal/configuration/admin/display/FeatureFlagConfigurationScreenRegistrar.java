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

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.feature.flag.web.internal.display.FeatureFlagsDisplayContextFactory;
import com.liferay.feature.flag.web.internal.model.FeatureFlagStatus;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

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
public class FeatureFlagConfigurationScreenRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		for (FeatureFlagStatus status : FeatureFlagStatus.values()) {
			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagConfigurationScreen(
						_featureFlagsDisplayContextFactory, _servletContext,
						status),
					new HashMapDictionary<>()));
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	@Reference
	private FeatureFlagsDisplayContextFactory
		_featureFlagsDisplayContextFactory;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

}