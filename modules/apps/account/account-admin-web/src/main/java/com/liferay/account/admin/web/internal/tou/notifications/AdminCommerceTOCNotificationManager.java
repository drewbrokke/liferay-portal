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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.notifications.NotificationEventFactoryUtil;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = AdminCommerceTOCNotificationManager.class)
public class AdminCommerceTOCNotificationManager {

	public void sendNotification(long userId)
		throws PortalException {

		if (UserNotificationManagerUtil.isDeliver(
				userId, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN, 0L,
				0, UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			NotificationEvent notificationEvent =
				NotificationEventFactoryUtil.createNotificationEvent(
					System.currentTimeMillis(),
					AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
					JSONFactoryUtil.createJSONObject());

			notificationEvent.setDeliveryType(
				UserNotificationDeliveryConstants.TYPE_WEBSITE);

			_userNotificationEventLocalService.addUserNotificationEvent(
				userId, true, true, notificationEvent);
		}
	}

	@Reference
	private UserNotificationEventLocalService _userNotificationEventLocalService;
}
