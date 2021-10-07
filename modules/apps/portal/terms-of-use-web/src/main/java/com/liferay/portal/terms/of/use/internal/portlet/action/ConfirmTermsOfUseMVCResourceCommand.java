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

package com.liferay.portal.terms.of.use.internal.portlet.action;

import com.liferay.portal.terms.of.use.internal.constants.PortalTermsOfUsePortletKeys;
import com.liferay.portal.terms.of.use.internal.terms.of.use.confirmation.manager.PortalTermsOfUseConfirmationManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Portal;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	enabled = false, immediate = true,
	property = {
		"javax.portlet.name=" + PortalTermsOfUsePortletKeys.PORTAL_TERMS_OF_USE,
		"mvc.command.name=/portal_terms_of_use/confirm"
	},
	service = MVCResourceCommand.class
)
public class ConfirmPortalTermsOfUseMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		_portalTermsOfUseConfirmationManager.confirm(
			_portal.getUserId(resourceRequest));
	}

	@Reference
	private PortalTermsOfUseConfirmationManager
		_portalTermsOfUseConfirmationManager;

	@Reference
	private Portal _portal;

}