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

import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.PhoneNumberException;
import com.liferay.portal.kernel.exception.PhoneNumberExtensionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.kernel.util.UsersAdmin;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"javax.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_organization_contact_information"
	},
	service = MVCActionCommand.class
)
public class UpdateOrganizationContactInformationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			updateContactInformation(actionRequest);
		}
		catch (Exception e) {
			if (e instanceof NoSuchOrganizationException ||
				e instanceof PrincipalException) {

				SessionErrors.add(actionRequest, e.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (e instanceof EmailAddressException ||
					 e instanceof NoSuchListTypeException ||
					 e instanceof PhoneNumberException ||
					 e instanceof PhoneNumberExtensionException ||
					 e instanceof WebsiteURLException) {

				SessionErrors.add(actionRequest, e.getClass(), e);

				actionResponse.setRenderParameter(
					"mvcPath", "/edit_organization.jsp");
			}
			else {
				throw e;
			}
		}
	}

	protected void updateContactInformation(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long organizationId = ParamUtil.getLong(
			actionRequest, "organizationId");

		Organization organization = _organizationService.getOrganization(
			organizationId);

		OrganizationPermissionUtil.check(
			themeDisplay.getPermissionChecker(), organization,
			ActionKeys.UPDATE);

		String listType = ParamUtil.getString(actionRequest, "listType");

		if (listType.equals(ListTypeConstants.PHONE)) {
			_updatePhones(actionRequest);
		}
		else {
			List<EmailAddress> emailAddresses =
				UsersAdminUtil.getEmailAddresses(actionRequest);

			if (emailAddresses != null) {
				_usersAdmin.updateEmailAddresses(
					Organization.class.getName(), organizationId,
					emailAddresses);
			}

			List<Phone> phones = UsersAdminUtil.getPhones(actionRequest);

			if (phones != null) {
				_usersAdmin.updatePhones(
					Organization.class.getName(), organizationId, phones);
			}

			List<Website> websites = UsersAdminUtil.getWebsites(actionRequest);

			if (websites != null) {
				_usersAdmin.updateWebsites(
					Organization.class.getName(), organizationId, websites);
			}
		}
	}

	private void _setPrimaryPhone(Long organizationId) throws PortalException {
		List<Phone> phones = _phoneService.getPhones(
			Organization.class.getName(), organizationId);

		if (phones.isEmpty()) {
			return;
		}

		for (Phone phone : phones) {
			if (phone.isPrimary()) {
				return;
			}
		}

		Phone primaryPhone = phones.get(0);

		_phoneService.updatePhone(
			primaryPhone.getPhoneId(), primaryPhone.getNumber(),
			primaryPhone.getExtension(), primaryPhone.getTypeId(), true);
	}

	private void _setPrimaryPhone(Long organizationId, Long phoneId)
		throws PortalException {

		List<Phone> phones = _phoneService.getPhones(
			Organization.class.getName(), organizationId);

		if (phones.isEmpty()) {
			return;
		}

		for (Phone phone : phones) {
			if ((phone.getPhoneId() == phoneId) && !phone.isPrimary()) {
				_phoneService.updatePhone(
					phone.getPhoneId(), phone.getNumber(), phone.getExtension(),
					phone.getTypeId(), true);
			}
			else if ((phone.getPhoneId() != phoneId) && phone.isPrimary()) {
				_phoneService.updatePhone(
					phone.getPhoneId(), phone.getNumber(), phone.getExtension(),
					phone.getTypeId(), false);
			}
		}
	}

	private void _updatePhones(ActionRequest actionRequest)
		throws PortalException {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		long phoneId = ParamUtil.getLong(actionRequest, "entryId");
		long organizationId = ParamUtil.getLong(
			actionRequest, "organizationId");

		if (cmd.equals(Constants.DELETE)) {
			_phoneService.deletePhone(phoneId);

			_setPrimaryPhone(organizationId);
		}
		else if (cmd.equals(UsersAdminWebKeys.CMD_MAKE_PRIMARY)) {
			_setPrimaryPhone(organizationId, phoneId);
		}
	}

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private Portal _portal;

	@Reference
	private UsersAdmin _usersAdmin;

	@Reference
	private WebsiteService _websiteService;

}