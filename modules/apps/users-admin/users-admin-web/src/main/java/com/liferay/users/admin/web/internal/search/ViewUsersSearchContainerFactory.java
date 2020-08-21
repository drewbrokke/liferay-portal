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

package com.liferay.users.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.usersadmin.search.UserSearch;
import com.liferay.portlet.usersadmin.search.UserSearchTerms;
import com.liferay.users.admin.constants.UsersAdminManagementToolbarKeys;
import com.liferay.users.admin.web.internal.display.context.UsersManagementToolbarFilterContributorTracker;

import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class ViewUsersSearchContainerFactory {

	public SearchContainer<User> create(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		PortletURL portletURL = (PortletURL)httpServletRequest.getAttribute(
			"view.jsp-portletURL");

		String navigation = ParamUtil.getString(
			httpServletRequest, "navigation");

		portletURL.setParameter("navigation", navigation);

		UserSearch userSearch = new UserSearch(
			renderRequest, "cur2", portletURL);

		RowChecker rowChecker = new EmptyOnClickRowChecker(renderResponse);

		rowChecker.setRowIds("rowIdsUser");

		userSearch.setRowChecker(rowChecker);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		if (navigation.equals("active")) {
			searchTerms.setStatus(WorkflowConstants.STATUS_APPROVED);
		}
		else if (navigation.equals("inactive")) {
			searchTerms.setStatus(WorkflowConstants.STATUS_INACTIVE);
		}

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		_usersManagementToolbarFilterContributorTracker.visit(
			UsersAdminManagementToolbarKeys.VIEW_USERS,
			usersManagementToolbarFilterContributor -> {
				String filter = ParamUtil.getString(
					renderRequest,
					usersManagementToolbarFilterContributor.getFilterName());

				params.putAll(
					usersManagementToolbarFilterContributor.
						getSearchParameterMap(filter));
			});

		int total = _userLocalService.searchCount(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), params);

		userSearch.setTotal(total);

		List<User> results = _userLocalService.search(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), params, userSearch.getStart(),
			userSearch.getEnd(), userSearch.getOrderByComparator());

		userSearch.setResults(results);

		return userSearch;
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UsersManagementToolbarFilterContributorTracker
		_usersManagementToolbarFilterContributorTracker;

}