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

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"mvc.command.name=/account_admin/edit_account_entry"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");

		try {
			AccountEntry accountEntry = _updateAccountEntry(
				accountEntryId, actionRequest);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (accountEntryId == 0) {
				redirect = _http.setParameter(
					redirect, actionResponse.getNamespace() + "accountEntryId",
					accountEntry.getAccountEntryId());
			}

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/account_entries_admin/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private byte[] _getLogoBytes(ActionRequest actionRequest) throws Exception {
		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId == 0) {
			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		return FileUtil.getBytes(fileEntry.getContentStream());
	}

	private int _getStatus(ActionRequest actionRequest) {
		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		if (active) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return WorkflowConstants.STATUS_INACTIVE;
	}

	private AccountEntry _updateAccountEntry(
			long accountEntryId, ActionRequest actionRequest)
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		if (accountEntry == null) {
			accountEntry = _accountEntryLocalService.createAccountEntry(
				accountEntryId);
		}

		accountEntry.setDescription(
			ParamUtil.getString(actionRequest, "description"));
		accountEntry.setEmailAddress(
			ParamUtil.getString(actionRequest, "emailAddress"));
		accountEntry.setName(ParamUtil.getString(actionRequest, "name"));
		accountEntry.setTaxIdNumber(
			ParamUtil.getString(actionRequest, "taxIdNumber"));
		accountEntry.setStatus(_getStatus(actionRequest));

		if (Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				accountEntry.getType())) {

			accountEntry.setDomains(
				ParamUtil.getString(actionRequest, "domains"));
		}

		byte[] logoBytes = _getLogoBytes(actionRequest);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AccountEntry.class.getName(), actionRequest);

		if (accountEntry.isNew()) {
			accountEntry.setType(
				ParamUtil.getString(
					actionRequest, "type",
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS));

			return _accountEntryLocalService.addAccountEntry(
				_portal.getUserId(actionRequest), accountEntry, logoBytes,
				serviceContext);
		}

		if (Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				accountEntry.getType())) {

			long personAccountEntryUserId = ParamUtil.getLong(
				actionRequest, "personAccountEntryUserId");

			_accountEntryUserRelLocalService.setPersonTypeAccountEntryUser(
				accountEntryId, personAccountEntryUserId);
		}

		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");

		return _accountEntryLocalService.updateAccountEntry(
			accountEntry, deleteLogo, logoBytes, serviceContext);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

}