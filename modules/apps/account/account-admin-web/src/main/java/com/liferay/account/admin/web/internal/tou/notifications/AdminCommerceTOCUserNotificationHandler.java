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

package com.liferay.account.admin.web.internal.tou.notifications;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
	service = UserNotificationHandler.class
)
public class AdminCommerceTOCUserNotificationHandler
	extends BaseUserNotificationHandler {

	public AdminCommerceTOCUserNotificationHandler() {
		setPortletId(AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN);
	}

	@Override
	protected String getBody(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) throws Exception {

		return StringUtil.replace(
			getBodyTemplate(),
			new String[] {
				"[$BODY$]", "[$TITLE$]"
			},
			new String[] {
				"This is the body",
				"This is the title"
			}
		);
	}

	@Override
	protected String getLink(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) throws Exception {

		return super.getLink(userNotificationEvent, serviceContext);
	}

	@Override
	public boolean isDeliver(
		long userId, long classNameId, int notificationType, int deliveryType,
		ServiceContext serviceContext) throws PortalException {

		if (!_adminCommerceTOCManager.isConfirmed(userId)) {
			return true;
		}

		return false;
	}

	@Reference
	private AdminCommerceTOCManager _adminCommerceTOCManager;

}