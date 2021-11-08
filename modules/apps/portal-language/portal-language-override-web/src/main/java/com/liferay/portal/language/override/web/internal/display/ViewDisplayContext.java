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
import com.liferay.portal.language.override.web.internal.dto.PLOItemDTO;
import com.liferay.portal.kernel.dao.search.SearchContainer;

/**
 * @author Drew Brokke
 */
public class ViewDisplayContext {

	private ManagementToolbarDisplayContext _managementToolbarDisplayContext;

	public ManagementToolbarDisplayContext getManagementToolbarDisplayContext() {
		return _managementToolbarDisplayContext;
	}

	public void setManagementToolbarDisplayContext(
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {

		_managementToolbarDisplayContext = managementToolbarDisplayContext;
	}

	public SearchContainer<PLOItemDTO> getSearchContainer() {
		return _searchContainer;
	}

	public void setSearchContainer(
		SearchContainer<PLOItemDTO> searchContainer) {
		_searchContainer = searchContainer;
	}

	private SearchContainer<PLOItemDTO> _searchContainer;

}
