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

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/configuration_admin/edit_portal_properties"
	},
	service = MVCActionCommand.class
)
public class EditPortalPropertiesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			PortletPreferences portletPreferences =
				PrefsPropsUtil.getPreferences(
					_portal.getCompanyId(actionRequest));

			for (ConfigurablePortalProperty configurablePortalProperty :
					_configurablePortalPropertyProvider.
						getConfigurablePortalProperties()) {

				String value = ParamUtil.getString(
					actionRequest,
					"settings--" + configurablePortalProperty.getName() + "--");

				portletPreferences.setValue(
					configurablePortalProperty.getName(), value);
			}

			portletPreferences.store();

			sendRedirect(
				actionRequest, actionResponse,
				ParamUtil.getString(actionRequest, "redirect"));
		}
		catch (IOException ioException) {
			_log.error(ioException, ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPortalPropertiesMVCActionCommand.class);

	@Reference
	private ConfigurablePortalPropertyProvider
		_configurablePortalPropertyProvider;

	@Reference
	private Portal _portal;

}