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

package com.liferay.portal.language.override.web.internal.display;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.util.OrderByComparatorAdapter;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringParser;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.language.override.web.internal.dto.PLOItemDTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
@Component(service = ViewDisplayContextFactory.class)
public class ViewDisplayContextFactory {

	public ViewDisplayContext create(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ViewDisplayContext viewDisplayContext = new ViewDisplayContext();

		SearchContainer<PLOItemDTO> searchContainer =
			_createSearchContainer(renderRequest, renderResponse);

		viewDisplayContext.setManagementToolbarDisplayContext(
			_createManagementToolbarDisplayContext(
				renderRequest, renderResponse, searchContainer));
		viewDisplayContext.setSearchContainer(searchContainer);

		return viewDisplayContext;
	}

	private ManagementToolbarDisplayContext _createManagementToolbarDisplayContext(RenderRequest renderRequest, RenderResponse renderResponse, SearchContainer searchContainer) {
		return new ViewManagementToolbarDisplayContext(
			_portal.getHttpServletRequest(renderRequest),
			_portal.getLiferayPortletRequest(renderRequest),
			_portal.getLiferayPortletResponse(renderResponse), searchContainer);
	}

	private SearchContainer<PLOItemDTO> _createSearchContainer(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(renderResponse);

		SearchContainer<PLOItemDTO> searchContainer =
			new SearchContainer<>(
				liferayPortletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest,
					liferayPortletResponse),
				Arrays.asList("key", "value"), "no-language-entries-were-found");

		searchContainer.setId("portalLanguageOverrideEntries");

		String orderByCol = ParamUtil.getString(
			liferayPortletRequest, "orderByCol", "name");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			liferayPortletRequest, "orderByType", "asc");

		searchContainer.setOrderByType(orderByType);

		_setResults(renderRequest, searchContainer);

		return searchContainer;
	}

	private void _setResults(
		RenderRequest renderRequest,
		SearchContainer<PLOItemDTO> searchContainer) {

		List<PLOItemDTO> ploItemDTOs = new ArrayList<>();

		List<PLOEntry> ploEntries =
			_ploEntryLocalService.getPLOEntries(
				_portal.getCompanyId(renderRequest));

		Stream<PLOEntry> ploEntryStream = ploEntries.stream();

		Map<String, List<PLOEntry>> ploEntryMap = ploEntryStream.collect(
			Collectors.groupingBy(PLOEntry::getKey));

		java.util.function.Predicate<String> stringMatchPredicate = s -> true;

		if (searchContainer.isSearch()) {
			String keywords = ParamUtil.getString(renderRequest, "keywords");

			Pattern pattern = Pattern.compile(
				".*\\b" + StringParser.escapeRegex(keywords) + ".*",
				Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

			stringMatchPredicate = pattern.asPredicate();
		}

		ResourceBundle resourceBundle =
			LanguageResources.getResourceBundle(
				_portal.getLocale(renderRequest));

		String filter = ParamUtil.getString(
			renderRequest, "navigation", "all");

		if (filter.equals("override")) {
			for (String key : ploEntryMap.keySet()) {
				if (stringMatchPredicate.test(key) ||
					stringMatchPredicate.test(resourceBundle.getString(key))) {

					ploItemDTOs.add(new PLOItemDTO(key, "override", resourceBundle.getString(key)));
				}
			}
		}
		else {
			Enumeration<String> keys = resourceBundle.getKeys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();

				String type = "system";

				if (ploEntryMap.containsKey(key)) {
					type = "override";

					ploEntryMap.remove(key);
				}

				if (stringMatchPredicate.test(key) ||
					stringMatchPredicate.test(resourceBundle.getString(key))) {

					ploItemDTOs.add(new PLOItemDTO(key, type, resourceBundle.getString(key)));
				}
			}
		}



		searchContainer.setTotal(ploItemDTOs.size());

		// Sorting
		Comparator<PLOItemDTO> comparator =
			Comparator.comparing(PLOItemDTO::getKey);

		if (Objects.equals(searchContainer.getOrderByType(), "desc")) {
			comparator = comparator.reversed();
		}

		ploItemDTOs.sort(comparator);

		// Pagination
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getTotal());

		searchContainer.setResults(ploItemDTOs.subList(startAndEnd[0], startAndEnd[1]));
	}

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Reference
	private Portal _portal;

}
