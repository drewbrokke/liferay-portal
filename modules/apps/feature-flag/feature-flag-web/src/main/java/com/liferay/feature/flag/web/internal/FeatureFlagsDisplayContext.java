package com.liferay.feature.flag.web.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsDisplayContext {

	private SearchContainer<FeatureFlagDisplay> _searchContainer;

	private ManagementToolbarDisplayContext _managementToolbarDisplayContext;

	public ManagementToolbarDisplayContext getManagementToolbarDisplayContext() {
		return _managementToolbarDisplayContext;
	}

	public void setManagementToolbarDisplayContext(
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {
		_managementToolbarDisplayContext = managementToolbarDisplayContext;
	}

	public SearchContainer<FeatureFlagDisplay> getSearchContainer() {
		return _searchContainer;
	}

	public void setSearchContainer(
		SearchContainer<FeatureFlagDisplay> searchContainer) {
		_searchContainer = searchContainer;
	}
}
