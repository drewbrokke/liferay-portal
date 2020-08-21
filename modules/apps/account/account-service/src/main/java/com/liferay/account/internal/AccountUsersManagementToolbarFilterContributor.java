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

package com.liferay.account.internal;

import com.liferay.account.constants.AccountConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UsersAdminManagementToolbarKeys;
import com.liferay.users.admin.management.toolbar.UsersManagementToolbarFilterContributor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = UsersManagementToolbarFilterContributor.class)
public class AccountUsersManagementToolbarFilterContributor
	implements UsersManagementToolbarFilterContributor {

	@Override
	public String getFilterLabel(Locale locale, String filterValue) {
		return LanguageUtil.get(locale, filterValue);
	}

	@Override
	public String getFilterName() {
		return "domains";
	}

	@Override
	public String[] getFilterValues() {
		return new String[] {"all", "company-users", "account-users"};
	}

	@Override
	public String getManagementToolbarId() {
		return UsersAdminManagementToolbarKeys.VIEW_USERS;
	}

	@Override
	public Map<String, Object> getSearchParameterMap(String filterValue) {
		if (filterValue == null) {
			return Collections.emptyMap();
		}

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		if (filterValue.equals("company-users")) {
			params.put("accountEntryIds", new Long[0]);
		}
		else if (filterValue.equals("account-users")) {
			params.put(
				"accountEntryIds",
				new Long[] {AccountConstants.ACCOUNT_ENTRY_ID_ANY});
		}

		return params;
	}

	@Override
	public boolean isShowLabelItem(String filterValue) {
		if (Validator.isNull(filterValue) ||
			filterValue.equals("company-users")) {

			return false;
		}

		return true;
	}

}