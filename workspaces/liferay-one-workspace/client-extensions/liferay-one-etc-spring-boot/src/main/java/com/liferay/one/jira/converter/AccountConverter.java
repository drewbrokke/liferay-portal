/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.headless.admin.user.client.custom.field.CustomField;
import com.liferay.headless.admin.user.client.custom.field.CustomValue;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.one.jira.client.JiraAssetObject;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.service.AccountService;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maps a Liferay headless {@link Account} to the "One Liferay" JSM "Account"
 * object's attributes.
 *
 * <p>
 * A pure mapper: it works only in attribute names and the supplied name-to-id
 * map (see {@link BusinessEventConverter} for the conventions). The mapping is
 * intentionally explicit -- each JSM attribute is sourced from exactly one place
 * (a top-level field, a known custom field, or {@link AccountContactInformation})
 * and attributes with no known Liferay source are left unmapped. The account is
 * re-fetched through {@link AccountService} when it arrives without custom fields
 * or contact information (e.g. from a list endpoint).
 * </p>
 *
 * @author Drew Brokke
 */
@Component
public class AccountConverter {

	public JiraAssetObject toAssetObject(
			Account account, Map<String, String> attributeIds)
		throws Exception {

		account = _complete(account);

		JiraAssetObject jiraAssetObject = new JiraAssetObject(attributeIds);

		// Top-level account fields

		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_KEY,
			account.getExternalReferenceCode());
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_NAME, account.getName());
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_DESCRIPTION,
			account.getDescription());
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_CREATED_AT,
			_format(account.getDateCreated()));
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_UPDATED_AT,
			_format(account.getDateModified()));
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_STATUS,
			_status(account.getStatus()));

		// Custom fields (Liferay expando column -> JSM attribute)

		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_CODE,
			_customFieldData(account, _CUSTOM_FIELD_NAME_ACCOUNT_CODE));
		jiraAssetObject.set(
			AccountConstants.ATTRIBUTE_NAME_TIER,
			_customFieldData(account, _CUSTOM_FIELD_NAME_ACCOUNT_TIER));

		// Contact information

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if (accountContactInformation != null) {
			jiraAssetObject.set(
				AccountConstants.ATTRIBUTE_NAME_CONTACT_EMAIL_ADDRESS,
				_getEmailAddress(accountContactInformation));
			jiraAssetObject.set(
				AccountConstants.ATTRIBUTE_NAME_FAX_NUMBER,
				_getFaxNumber(accountContactInformation));
			jiraAssetObject.set(
				AccountConstants.ATTRIBUTE_NAME_PHONE_NUMBER,
				_getPhoneNumber(accountContactInformation));
			jiraAssetObject.set(
				AccountConstants.ATTRIBUTE_NAME_WEBSITE,
				_getWebsite(accountContactInformation));
		}

		return jiraAssetObject;
	}

	private Account _complete(Account account) throws Exception {
		if ((account.getCustomFields() != null) &&
			(account.getAccountContactInformation() != null)) {

			return account;
		}

		Account fetchedAccount = _accountService.fetchAccount(account.getId());

		if (fetchedAccount != null) {
			return fetchedAccount;
		}

		return account;
	}

	private Object _customFieldData(Account account, String name) {
		CustomField[] customFields = account.getCustomFields();

		if (customFields == null) {
			return null;
		}

		for (CustomField customField : customFields) {
			if (!name.equals(customField.getName())) {
				continue;
			}

			CustomValue customValue = customField.getCustomValue();

			if (customValue == null) {
				return null;
			}

			return customValue.getData();
		}

		return null;
	}

	private String _format(Date date) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = _simpleDateFormat();

		return simpleDateFormat.format(date);
	}

	private String _getEmailAddress(
		AccountContactInformation accountContactInformation) {

		EmailAddress[] emailAddresses =
			accountContactInformation.getEmailAddresses();

		if (ArrayUtil.isEmpty(emailAddresses)) {
			return null;
		}

		for (EmailAddress emailAddress : emailAddresses) {
			if (Boolean.TRUE.equals(emailAddress.getPrimary())) {
				return emailAddress.getEmailAddress();
			}
		}

		EmailAddress emailAddress = emailAddresses[0];

		return emailAddress.getEmailAddress();
	}

	private String _getFaxNumber(
		AccountContactInformation accountContactInformation) {

		Phone[] telephones = accountContactInformation.getTelephones();

		if (telephones == null) {
			return null;
		}

		for (Phone telephone : telephones) {
			if ("fax".equalsIgnoreCase(telephone.getPhoneType())) {
				return telephone.getPhoneNumber();
			}
		}

		return null;
	}

	private String _getPhoneNumber(
		AccountContactInformation accountContactInformation) {

		Phone[] telephones = accountContactInformation.getTelephones();

		if (ArrayUtil.isEmpty(telephones)) {
			return null;
		}

		for (Phone telephone : telephones) {
			if ("fax".equalsIgnoreCase(telephone.getPhoneType())) {
				continue;
			}

			if (Boolean.TRUE.equals(telephone.getPrimary())) {
				return telephone.getPhoneNumber();
			}
		}

		Phone telephone = telephones[0];

		return telephone.getPhoneNumber();
	}

	private String _getWebsite(
		AccountContactInformation accountContactInformation) {

		WebUrl[] webUrls = accountContactInformation.getWebUrls();

		if (ArrayUtil.isEmpty(webUrls)) {
			return null;
		}

		for (WebUrl webUrl : webUrls) {
			if (Boolean.TRUE.equals(webUrl.getPrimary())) {
				return webUrl.getUrl();
			}
		}

		WebUrl webUrl = webUrls[0];

		return webUrl.getUrl();
	}

	private SimpleDateFormat _simpleDateFormat() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		return simpleDateFormat;
	}

	private String _status(Integer status) {

		// Best-guess mapping; adjust to the real Liferay account status codes.

		if (status == null) {
			return null;
		}

		if (status == 0) {
			return "Active";
		}

		return "Closed";
	}

	private static final String _CUSTOM_FIELD_NAME_ACCOUNT_CODE = "accountCode";

	private static final String _CUSTOM_FIELD_NAME_ACCOUNT_TIER = "accountTier";

	@Autowired
	private AccountService _accountService;

}