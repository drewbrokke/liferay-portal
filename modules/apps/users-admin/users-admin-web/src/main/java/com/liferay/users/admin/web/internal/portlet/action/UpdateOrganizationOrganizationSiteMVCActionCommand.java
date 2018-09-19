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

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.OrgLaborServiceUtil;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sites.kernel.util.Sites;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"javax.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_organization_organization_site"
	},
	service = MVCActionCommand.class
)
public class UpdateOrganizationOrganizationSiteMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			updateOrganizationSite(actionRequest);
		}
		catch (Exception e) {
			String mvcPath = "/organization_site.jsp";

			if (e instanceof NoSuchOrganizationException ||
				e instanceof PrincipalException) {

				SessionErrors.add(actionRequest, e.getClass());

				mvcPath = "/error.jsp";
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	protected void updateOrganizationSite(ActionRequest actionRequest)
		throws Exception {

			boolean site = ParamUtil.getBoolean(actionRequest, "site");

			long publicLayoutSetPrototypeId = ParamUtil.getLong(
				actionRequest, "publicLayoutSetPrototypeId");
			long privateLayoutSetPrototypeId = ParamUtil.getLong(
				actionRequest, "privateLayoutSetPrototypeId");
			boolean publicLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
				actionRequest, "publicLayoutSetPrototypeLinkEnabled",
				publicLayoutSetPrototypeId > 0);
			boolean privateLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
				actionRequest, "privateLayoutSetPrototypeLinkEnabled",
				privateLayoutSetPrototypeId > 0);

			long organizationId = ParamUtil.getLong(
				actionRequest, "organizationId");

			Organization organization = _organizationService.getOrganization(
				organizationId);

			Group organizationGroup = organization.getGroup();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			List<EmailAddress> emailAddresses =
				_emailAddressService.getEmailAddresses(
					Organization.class.getName(), organizationId);
			List<Phone> phones = _phoneService.getPhones(
				Organization.class.getName(), organizationId);
			List<Website> websites = _websiteService.getWebsites(
				Organization.class.getName(), organizationId);

			boolean deleteLogo = false;

			byte[] logoBytes = null;

			if (organization.getLogoId() > 0) {
				Image image = ImageLocalServiceUtil.getImage(
					organization.getLogoId());

				logoBytes = image.getTextObj();
			}

			organization = _organizationService.updateOrganization(
				organizationId, organization.getParentOrganizationId(),
				organization.getName(), organization.getType(),
				organization.getRegionId(), organization.getCountryId(),
				organization.getStatusId(), organization.getComments(),
				deleteLogo, logoBytes, site, organization.getAddresses(),
				emailAddresses,
				OrgLaborServiceUtil.getOrgLabors(organizationId), phones,
				websites, null);

			if (GroupPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), organizationGroup,
					ActionKeys.UPDATE)) {

				_sites.updateLayoutSetPrototypesLinks(
					organizationGroup, publicLayoutSetPrototypeId,
					privateLayoutSetPrototypeId,
					publicLayoutSetPrototypeLinkEnabled,
					privateLayoutSetPrototypeLinkEnabled);
			}
	}

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private Sites _sites;

	@Reference
	private WebsiteService _websiteService;

}