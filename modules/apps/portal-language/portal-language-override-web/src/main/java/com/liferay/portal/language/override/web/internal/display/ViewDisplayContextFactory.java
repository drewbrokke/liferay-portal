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
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.regex.PatternFactory;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.language.override.web.internal.dto.PLOItemDTO;
import com.liferay.portal.language.override.web.internal.dto.PLOItems;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
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
				null, "no-language-entries-were-found");

		searchContainer.setId("accountEntries");

		String orderByCol = ParamUtil.getString(
			liferayPortletRequest, "orderByCol", "name");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			liferayPortletRequest, "orderByType", "asc");

		searchContainer.setOrderByType(orderByType);

//		searchContainer.setRowChecker(
//			new EmptyOnClickRowChecker(liferayPortletResponse));

		String keywords = ParamUtil.getString(
			liferayPortletRequest, "keywords");

		String navigation = ParamUtil.getString(
			liferayPortletRequest, "navigation", "active");

		String type = ParamUtil.getString(liferayPortletRequest, "type");

		PLOItems ploItems = _getPLOItems(
			renderRequest, renderResponse, keywords,
			searchContainer.getStart(), searchContainer.getEnd());

		searchContainer.setResults(ploItems.getPLOItemList());
		searchContainer.setTotal(ploItems.getTotal());

		return searchContainer;
	}

	private PLOItems _getPLOItems(RenderRequest renderRequest, RenderResponse renderResponse, String keywords, int start, int end) {
		List<PLOItemDTO> ploItemDTOs = new ArrayList<>();

		Locale locale = _portal.getLocale(renderRequest);

		String orderByCol = ParamUtil.getString(
			renderRequest, "orderByCol", "key");

		String orderByType = ParamUtil.getString(
			renderRequest, "orderByType", "asc");

		long companyId = _portal.getCompanyId(renderRequest);
//		List<PLOEntry> ploEntries =
//			_ploEntryLocalService.getPLOEntries(
//				companyId);
//
//		Stream<PLOEntry> ploEntryStream = ploEntries.stream();

		java.util.function.Predicate<String> stringMatchPredicate = s -> true;

		if (keywords != null) {
			Pattern pattern = Pattern.compile(
				".* " + keywords + ".*",
				Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

			stringMatchPredicate = pattern.asPredicate();
		}

//		Map<String, List<PLOEntry>> ploEntryMap = ploEntryStream.collect(
//			Collectors.groupingBy(PLOEntry::getKey));

		ResourceBundle resourceBundle =
			LanguageResources.getResourceBundle(locale);

		for (String key : resourceBundle.keySet()) {
			String type = "system";

//			if (ploEntryMap.containsKey(key)) {
//				type = "override";
//
//				ploEntryMap.remove(key);
//			}

			if (stringMatchPredicate.test(key) ||
				stringMatchPredicate.test(resourceBundle.getString(key))) {

				ploItemDTOs.add(new PLOItemDTO(key, type, resourceBundle.getString(key)));
			}
		}


		return new PLOItems(
			ploItemDTOs.subList(start, Math.min(end, ploItemDTOs.size() - 1)),
			ploItemDTOs.size());
	}

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Reference
	private Portal _portal;

}
