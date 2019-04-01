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

package com.liferay.portal.settings.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Account;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.portlet.action.PortalSettingsConfigurationScreenContributor;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = PortalSettingsConfigurationScreenContributor.class)
public class ContactPortalSettingsConfigurationScreenContributor
	implements PortalSettingsConfigurationScreenContributor {

	@Override
	public String getCategoryKey() {
		return "portal-settings";
	}

	@Override
	public String getJspPath() {
		return "/contact.jsp";
	}

	@Override
	public String getKey() {
		return "contact";
	}

	@Override
	public String getName(Locale locale) {
		return "contact";
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/portal_settings/edit_company";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void setAttributes(
		HttpServletRequest request, HttpServletResponse response) {

		request.setAttribute("addresses.className", Account.class.getName());
		request.setAttribute(
			"emailAddresses.className", Account.class.getName());
		request.setAttribute("phones.className", Account.class.getName());
		request.setAttribute("websites.className", Account.class.getName());

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		request.setAttribute("addresses.classPK", company.getAccountId());
		request.setAttribute("emailAddresses.classPK", company.getAccountId());
		request.setAttribute("phones.classPK", company.getAccountId());
		request.setAttribute("websites.classPK", company.getAccountId());
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}