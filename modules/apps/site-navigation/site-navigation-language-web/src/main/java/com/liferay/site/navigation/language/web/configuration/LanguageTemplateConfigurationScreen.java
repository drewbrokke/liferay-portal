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

package com.liferay.site.navigation.language.web.configuration;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	configurationPid = "com.liferay.site.navigation.language.web.configuration.SiteNavigationLanguageWebTemplateConfiguration",
	immediate = true, service = ConfigurationScreen.class
)
public class LanguageTemplateConfigurationScreen
	implements ConfigurationScreen {

	@Activate
	public void activate(Map<String, Object> properties) {
		_siteNavigationLanguageWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SiteNavigationLanguageWebTemplateConfiguration.class,
				properties);
	}

	@Override
	public String getCategoryKey() {
		return "localization";
	}

	@Override
	public String getKey() {
		return "site-navigation-language-web-template-configuration-name";
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getBundle(
				locale, LanguageTemplateConfigurationScreen.class),
			getKey());
	}

	@Override
	public String getScope() {
		return "system";
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		LanguageTemplateConfigurationDisplayContext
			languageTemplateConfigurationDisplayContext =
				new LanguageTemplateConfigurationDisplayContext();

		languageTemplateConfigurationDisplayContext.setCurrentTemplateName(
			_siteNavigationLanguageWebTemplateConfiguration.ddmTemplateKey());

		long groupId = 0;

		Group group = _groupLocalService.fetchCompanyGroup(
			CompanyThreadLocal.getCompanyId());

		if (group != null) {
			groupId = group.getGroupId();
		}

		List<DDMTemplate> ddmTemplates = _ddmTemplateLocalService.getTemplates(
			groupId, _portal.getClassNameId(LanguageEntry.class));

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			languageTemplateConfigurationDisplayContext.addTemplateValue(
				ddmTemplate.getTemplateKey(), ddmTemplate.getName(locale));
		}

		languageTemplateConfigurationDisplayContext.setRedirect(
			_portal.getCurrentURL(request));

		languageTemplateConfigurationDisplayContext.setTitle(getName(locale));

		languageTemplateConfigurationDisplayContext.setFieldLabel(
			"Language Selection Style");

		request.setAttribute(
			LanguageTemplateConfigurationDisplayContext.class.getName(),
			languageTemplateConfigurationDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, request, response,
			"/configuration/site_navigation_language_web_template.jsp");
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.navigation.language.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

	private volatile SiteNavigationLanguageWebTemplateConfiguration
		_siteNavigationLanguageWebTemplateConfiguration;

}