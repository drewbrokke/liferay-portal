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

package com.liferay.user.associated.data.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.display.UADEntityDisplay;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.registry.UADRegistryUtil;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Drew Brokke
 */
public class UserAssociatedDataEntitiesDisplayContext {

	public UserAssociatedDataEntitiesDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public UADEntityDisplay getUADEntityDisplay() {
		String uadRegistryKey = ParamUtil.getString(
			_renderRequest, "uadRegistryKey");

		return UADRegistryUtil.getUADEntityDisplay(uadRegistryKey);
	}

	public SearchContainer<UADEntity> getUADEntitySearchContainer() {
		long selUserId = ParamUtil.getLong(_renderRequest, "selUserId");
		String uadRegistryKey = ParamUtil.getString(
			_renderRequest, "uadRegistryKey");

		PortletURL iteratorURL = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);

		SearchContainer<UADEntity> searchContainer = new SearchContainer<>(
			_renderRequest, iteratorURL, null, null);

		UADEntityAggregator uadEntityAggregator =
			UADRegistryUtil.getUADEntityAggregator(uadRegistryKey);

		searchContainer.setResults(
			uadEntityAggregator.getUADEntities(
				selUserId, searchContainer.getStart(),
				searchContainer.getEnd()));
		searchContainer.setTotal(uadEntityAggregator.count(selUserId));

		return searchContainer;
	}

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}