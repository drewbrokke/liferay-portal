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

package com.liferay.site.navigation.language.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.site.navigation.language.web.configuration.SiteNavigationLanguageWebTemplateConfiguration;

import java.util.Dictionary;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/site_navigation_language/update_language_template_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateLanguageTemplateConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String ddmTemplateKey = ParamUtil.getString(
			actionRequest, "ddmTemplateKey");

		Configuration configuration = _configurationAdmin.getConfiguration(
			SiteNavigationLanguageWebTemplateConfiguration.class.getName(),
			StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			properties = new HashMapDictionary<>();
		}

		properties.put("ddmTemplateKey", ddmTemplateKey);

		configuration.update(properties);
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}