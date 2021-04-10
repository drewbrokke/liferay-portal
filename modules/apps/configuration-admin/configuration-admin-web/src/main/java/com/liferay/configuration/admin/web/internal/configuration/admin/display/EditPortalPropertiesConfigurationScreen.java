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

package com.liferay.configuration.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = {ConfigurationCategory.class, ConfigurationScreen.class})
public class EditPortalPropertiesConfigurationScreen
	implements ConfigurationCategory, ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "portal-properties";
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}

	@Override
	public String getKey() {
		return "portal-properties";
	}

	@Override
	public String getName(Locale locale) {
		return "portal-properties";
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			EditPortalPropertiesDisplayContext
				editPortalPropertiesDisplayContext =
					new EditPortalPropertiesDisplayContext();

			editPortalPropertiesDisplayContext.setConfigurablePortalProperties(
				_configurablePortalPropertyProvider.
					getConfigurablePortalProperties());

			httpServletRequest.setAttribute(
				ConfigurationAdminWebKeys.
					EDIT_PORTAL_PROPERTIES_DISPLAY_CONTEXT,
				editPortalPropertiesDisplayContext);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_portal_properties.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			_log.error(servletException, servletException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPortalPropertiesConfigurationScreen.class);

	@Reference
	private ConfigurablePortalPropertyProvider
		_configurablePortalPropertyProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.configuration.admin.web)"
	)
	private ServletContext _servletContext;

}