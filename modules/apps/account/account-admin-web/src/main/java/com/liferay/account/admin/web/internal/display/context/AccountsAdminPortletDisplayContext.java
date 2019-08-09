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

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class AccountsAdminPortletDisplayContext {

	public AccountsAdminPortletDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public SearchContainer getAccountsSearchContainer() {
		if (_accountsSearchContainer != null) {
			return _accountsSearchContainer;
		}

		SearchContainer accountsSearchContainer = new SearchContainer(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			null, "no-accounts-were-found");

		accountsSearchContainer.setId("accounts");
		accountsSearchContainer.setOrderByCol(getOrderByCol());
		accountsSearchContainer.setOrderByType(getOrderByType());

		accountsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_liferayPortletResponse));

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_liferayPortletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<AccountEntry> accountEntries =
			AccountEntryLocalServiceUtil.getAccountEntries(
				themeDisplay.getCompanyId(), getStatus(),
				accountsSearchContainer.getStart(),
				accountsSearchContainer.getEnd(),
				accountsSearchContainer.getOrderByComparator());

		accountsSearchContainer.setResults(accountEntries);
		accountsSearchContainer.setTotal(accountEntries.size());

		_accountsSearchContainer = accountsSearchContainer;

		return _accountsSearchContainer;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_liferayPortletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_liferayPortletRequest, "orderByCol", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_liferayPortletRequest, "orderByType", "asc");

		return _orderByType;
	}

	public int getStatus() {
		return ParamUtil.getInteger(
			_liferayPortletRequest, "status",
			WorkflowConstants.STATUS_APPROVED);
	}

	private SearchContainer _accountsSearchContainer;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;

}