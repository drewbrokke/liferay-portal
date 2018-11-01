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

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.usersadmin.search.OrganizationSearch;
import com.liferay.portlet.usersadmin.search.OrganizationSearchTerms;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class SelectOrganizationManagementToolbarDisplayContext {

	public SelectOrganizationManagementToolbarDisplayContext(
		HttpServletRequest request, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_request = request;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_currentURL = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);
	}

	public String getClearResultsURL() {
		PortletURL clearResultsURL = getPortletURL();

		clearResultsURL.setParameter("keywords", StringPool.BLANK);

		return clearResultsURL.toString();
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return new DropdownItemList() {
			{
				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getFilterNavigationDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(_request, "filter-by-navigation"));
					});

				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getOrderByDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(_request, "order-by"));
					});
			}
		};
	}

	public String getOrderByCol() {
		return _organizationSearch.getOrderByCol();
	}

	public String getOrderByType() {
		return _organizationSearch.getOrderByType();
	}

	public PortletURL getPortletURL() {
		try {
			PortletURL portletURL = PortletURLUtil.clone(
				_currentURL, _renderResponse);

			portletURL.setParameter("orderByCol", getOrderByCol());
			portletURL.setParameter("orderByType", getOrderByType());

			return portletURL;
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}

			return _renderResponse.createRenderURL();
		}
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer getSearchContainer(
			LinkedHashMap<String, Object> organizationParams)
		throws Exception {

		if (_organizationSearch != null) {
			return _organizationSearch;
		}

		OrganizationSearch organizationSearch = new OrganizationSearch(
			_renderRequest, getPortletURL());

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		long parentOrganizationId =
			OrganizationConstants.ANY_PARENT_ORGANIZATION_ID;

		OrganizationSearchTerms organizationSearchTerms =
			(OrganizationSearchTerms)organizationSearch.getSearchTerms();

		List<Organization> results;
		int total;

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		if (indexer.isIndexerEnabled() &&
			PropsValues.ORGANIZATIONS_SEARCH_WITH_INDEX) {

			organizationParams.put(
				"expandoAttributes", organizationSearchTerms.getKeywords());

			Sort sort = SortFactoryUtil.getSort(
				Organization.class, organizationSearch.getOrderByCol(),
				organizationSearch.getOrderByType());

			BaseModelSearchResult<Organization> baseModelSearchResult =
				OrganizationLocalServiceUtil.searchOrganizations(
					themeDisplay.getCompanyId(), parentOrganizationId,
					organizationSearchTerms.getKeywords(), organizationParams,
					organizationSearch.getStart(), organizationSearch.getEnd(),
					sort);

			results = baseModelSearchResult.getBaseModels();
			total = baseModelSearchResult.getLength();
		}
		else {
			total = OrganizationLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), parentOrganizationId,
				organizationSearchTerms.getKeywords(),
				organizationSearchTerms.getType(),
				organizationSearchTerms.getRegionIdObj(),
				organizationSearchTerms.getCountryIdObj(), organizationParams);

			results = OrganizationLocalServiceUtil.search(
				themeDisplay.getCompanyId(), parentOrganizationId,
				organizationSearchTerms.getKeywords(),
				organizationSearchTerms.getType(),
				organizationSearchTerms.getRegionIdObj(),
				organizationSearchTerms.getCountryIdObj(), organizationParams,
				organizationSearch.getStart(), organizationSearch.getEnd(),
				organizationSearch.getOrderByComparator());
		}

		organizationSearch.setResults(results);
		organizationSearch.setTotal(total);

		_organizationSearch = organizationSearch;

		return _organizationSearch;
	}

	public String getSortingURL() {
		PortletURL sortingURL = getPortletURL();

		sortingURL.setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL.toString();
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(true);
						dropdownItem.setHref(StringPool.BLANK);
						dropdownItem.setLabel(
							LanguageUtil.get(_request, "all"));
					});
			}
		};
	}

	private List<DropdownItem> _getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							Objects.equals(getOrderByCol(), "name"));
						dropdownItem.setHref(
							getPortletURL(), "orderByCol", "name");
						dropdownItem.setLabel(
							LanguageUtil.get(_request, "name"));
					});

				add(
					dropdownItem -> {
						dropdownItem.setActive(
							Objects.equals(getOrderByCol(), "type"));
						dropdownItem.setHref(
							getPortletURL(), "orderByCol", "type");
						dropdownItem.setLabel(
							LanguageUtil.get(_request, "type"));
					});
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectOrganizationManagementToolbarDisplayContext.class);

	private final PortletURL _currentURL;
	private OrganizationSearch _organizationSearch;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final HttpServletRequest _request;

}