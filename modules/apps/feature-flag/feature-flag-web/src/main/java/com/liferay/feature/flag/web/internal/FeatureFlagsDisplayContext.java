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

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsDisplayContext {

	public ManagementToolbarDisplayContext
		getManagementToolbarDisplayContext() {

		return _managementToolbarDisplayContext;
	}

	public SearchContainer<FeatureFlagDisplay> getSearchContainer() {
		return _searchContainer;
	}

	public void setManagementToolbarDisplayContext(
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {

		_managementToolbarDisplayContext = managementToolbarDisplayContext;
	}

	public void setSearchContainer(
		SearchContainer<FeatureFlagDisplay> searchContainer) {

		_searchContainer = searchContainer;
	}

	private ManagementToolbarDisplayContext _managementToolbarDisplayContext;
	private SearchContainer<FeatureFlagDisplay> _searchContainer;

	private String _displayStyle;

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}
}