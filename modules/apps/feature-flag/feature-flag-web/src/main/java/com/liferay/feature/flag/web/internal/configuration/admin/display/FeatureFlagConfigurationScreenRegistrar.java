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
import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.feature.flag.web.internal.display.FeatureFlagsDisplayContextFactory;
import com.liferay.feature.flag.web.internal.model.FeatureFlag;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		for (FeatureFlag.Status status : FeatureFlag.Status.values()) {
			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagConfigurationScreen(status),
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

	private class FeatureFlagConfigurationScreen
		implements ConfigurationScreen {

		public FeatureFlagConfigurationScreen(FeatureFlag.Status status) {
			_status = status;
		}

		@Override
		public String getCategoryKey() {
			return FeatureFlagConstants.FEATURE_FLAG;
		}

		@Override
		public String getKey() {
			return FeatureFlagConstants.getKey(_status.toString());
		}

		@Override
		public String getName(Locale locale) {
			return _status.getTitle(locale);
		}

		@Override
		public String getScope() {
			return "company";
		}

		@Override
		public boolean isVisible() {
			return _status.isUIEnabled();
		}

		@Override
		public void render(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
			throws IOException {

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				_featureFlagsDisplayContextFactory.create(
					httpServletRequest, httpServletResponse, _status));

			try {
				RequestDispatcher requestDispatcher =
					_servletContext.getRequestDispatcher("/feature_flags.jsp");

				requestDispatcher.include(
					httpServletRequest, httpServletResponse);
			}
			catch (Exception exception) {
				throw new IOException(
					"Unable to render feature_flags.jsp", exception);
			}
		}

		private final FeatureFlag.Status _status;

	}

}