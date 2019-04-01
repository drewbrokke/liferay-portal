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

import com.liferay.portal.kernel.terms.of.use.TermsOfUseContentProvider;
import com.liferay.portal.settings.portlet.action.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.web.internal.constants.PortalSettingsWebKeys;

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
public class TermsOfUsePortalSettingsConfigurationScreenContributor
	implements PortalSettingsConfigurationScreenContributor {

	@Override
	public String getCategoryKey() {
		return "portal-settings";
	}

	@Override
	public String getJspPath() {
		return "/terms_of_use.jsp";
	}

	@Override
	public String getKey() {
		return "terms-of-use";
	}

	@Override
	public String getName(Locale locale) {
		return "terms-of-use";
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

		request.setAttribute(
			PortalSettingsWebKeys.TERMS_OF_USE_CONTENT_PROVIDER,
			_termsOfUseContentProvider);
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

	@Reference
	private TermsOfUseContentProvider _termsOfUseContentProvider;

}