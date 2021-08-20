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

package com.liferay.bookmarks.web.internal.el;

import com.liferay.portal.kernel.service.permission.GroupPermission;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Neil Griffin
 */
@Component(immediate = true, service = CurrentUserFactory.class)
public class CurrentUserFactory {

	public CurrentUser getCurrentUser(
		boolean emailEntryAddedEnabled, boolean emailEntryUpdatedEnabled,
		ThemeDisplay themeDisplay) {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return new CurrentUser(
			themeDisplay.getCompanyId(), emailEntryAddedEnabled,
			emailEntryUpdatedEnabled, _groupPermission,
			portletDisplay.getPortletName(),
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
			themeDisplay.isSignedIn(), _stagingGroupHelper,
			_subscriptionLocalService, themeDisplay.getUserId());
	}

	@Reference
	private GroupPermission _groupPermission;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}