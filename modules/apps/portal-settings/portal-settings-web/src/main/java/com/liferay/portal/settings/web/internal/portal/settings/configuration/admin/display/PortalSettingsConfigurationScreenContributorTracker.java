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

package com.liferay.portal.settings.web.internal.portal.settings.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class PortalSettingsConfigurationScreenContributorTracker
	implements ServiceTrackerCustomizer
		<PortalSettingsConfigurationScreenContributor,
		 ServiceRegistration<ConfigurationScreen>> {

	@Override
	public ServiceRegistration<ConfigurationScreen> addingService(
		ServiceReference<PortalSettingsConfigurationScreenContributor>
			serviceReference) {

		Bundle bundle = serviceReference.getBundle();

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ConfigurationScreen.class,
			new PortalSettingsConfigurationScreen(
				bundleContext.getService(serviceReference), _servletContext),
			new HashMapDictionary<>());
	}

	@Override
	public void modifiedService(
		ServiceReference<PortalSettingsConfigurationScreenContributor>
			serviceReference,
		ServiceRegistration<ConfigurationScreen> serviceRegistration) {
	}

	@Override
	public void removedService(
		ServiceReference<PortalSettingsConfigurationScreenContributor>
			serviceReference,
		ServiceRegistration<ConfigurationScreen> serviceRegistration) {

		serviceRegistration.unregister();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, PortalSettingsConfigurationScreenContributor.class,
			this);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private ServiceTracker
		<PortalSettingsConfigurationScreenContributor,
		 ServiceRegistration<ConfigurationScreen>> _serviceTracker;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)"
	)
	private ServletContext _servletContext;

}