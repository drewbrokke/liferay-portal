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

package com.liferay.account.security.permission.contributor;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Drew Brokke
 */
public class AccountRoleContributorThreadLocal {

	public static Boolean isEnabled() {
		Boolean enabled = _enabled.get();

		if (_log.isDebugEnabled()) {
			_log.debug("enabled " + enabled);
		}

		return enabled;
	}

	public static SafeCloseable setWithSafeCloseable(Boolean enabled) {
		return _enabled.setWithSafeCloseable(enabled);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountRoleContributorThreadLocal.class);

	private static final CentralizedThreadLocal<Boolean> _enabled =
		new CentralizedThreadLocal<>(
			AccountRoleContributorThreadLocal.class + "._enabled",
			() -> Boolean.TRUE);

}