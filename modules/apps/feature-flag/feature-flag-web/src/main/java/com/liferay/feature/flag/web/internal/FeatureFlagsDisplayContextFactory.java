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

package com.liferay.feature.flag.web.internal;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagsDisplayContextFactory.class)
public class FeatureFlagsDisplayContextFactory {

	public FeatureFlagsDisplayContext create(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FeatureFlag.Status status) {

		FeatureFlagsDisplayContext featureFlagsDisplayContext =
			new FeatureFlagsDisplayContext();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		featureFlagsDisplayContext.setDisplayStyle(
			ParamUtil.getString(portletRequest, "displayStyle", "descriptive"));

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(portletRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(portletResponse);

		SearchContainer<FeatureFlagDisplay> searchContainer =
			new SearchContainer<>(
				portletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest, liferayPortletResponse),
				null, "no-feature-flags-found");

		searchContainer.setId("accountEntryAccountGroupsSearchContainer");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-type", "asc"));

		FeatureFlags featureFlags = _featureFlagsProvider.getFeatureFlags(
			_portal.getCompanyId(httpServletRequest));

		Predicate<FeatureFlag> predicate = FeatureFlag.Status.getPredicate(
			status);

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		Locale locale = _portal.getLocale(httpServletRequest);

		if (Validator.isNotNull(keywords)) {
			predicate = predicate.and(
				featureFlag ->
					_contains(
						locale, featureFlag.getDescription(locale), keywords) ||
					_contains(locale, featureFlag.getKey(), keywords) ||
					_contains(locale, featureFlag.getTitle(locale), keywords));
		}

		for (FeatureFlagsManagementToolbarDisplayContext.Filter filter :
				FeatureFlagsManagementToolbarDisplayContext.FILTERS) {

			predicate = predicate.and(filter.getPredicate(httpServletRequest));
		}

		List<FeatureFlagDisplay> featureFlagDisplays = TransformUtil.transform(
			featureFlags.getFeatureFlags(predicate),
			featureFlag -> new FeatureFlagDisplay(featureFlag, locale));

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(),
			featureFlagDisplays.size());

		searchContainer.setResultsAndTotal(
			() -> featureFlagDisplays.subList(startAndEnd[0], startAndEnd[1]),
			featureFlagDisplays.size());

		featureFlagsDisplayContext.setSearchContainer(searchContainer);

		featureFlagsDisplayContext.setManagementToolbarDisplayContext(
			new FeatureFlagsManagementToolbarDisplayContext(
				httpServletRequest, liferayPortletRequest,
				liferayPortletResponse, searchContainer));

		return featureFlagsDisplayContext;
	}

	private boolean _contains(Locale locale, String s1, String s2) {
		String normalized = _normalize(locale, s1);

		return normalized.contains(_normalize(locale, s2));
	}

	private String _normalize(Locale locale, String string) {
		return StringUtil.toLowerCase(string, locale);
	}

	@Reference
	private FeatureFlagsProvider _featureFlagsProvider;

	@Reference
	private Portal _portal;

}