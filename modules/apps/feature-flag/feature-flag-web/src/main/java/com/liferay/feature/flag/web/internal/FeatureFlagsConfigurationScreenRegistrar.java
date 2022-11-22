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

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.language.Language;
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
public class FeatureFlagsConfigurationScreenRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		for (FeatureFlag.Status status : FeatureFlag.Status.values()) {
			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagsConfigurationScreen(status),
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

	@Reference
	private Language _language;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

	private class FeatureFlagsConfigurationScreen
		implements ConfigurationScreen {

		public FeatureFlagsConfigurationScreen(FeatureFlag.Status status) {
			_status = status;
		}

		@Override
		public String getCategoryKey() {
			return FeatureFlagsConfigurationConstants.
				CONFIGURATION_CATEGORY_KEY;
		}

		@Override
		public String getKey() {
			return FeatureFlagsConfigurationConstants.getEntryKey(_status);
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, _status.toString());
		}

		@Override
		public String getScope() {
			return "company";
		}

		@Override
		public boolean isVisible() {
			return FeatureFlagsPropsUtil.isUIEnabled(_status);
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