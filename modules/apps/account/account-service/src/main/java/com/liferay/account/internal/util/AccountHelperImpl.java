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

package com.liferay.account.internal.util;

import com.liferay.account.util.AccountHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = AccountHelper.class)
public class AccountHelperImpl implements AccountHelper {

	@Override
	public long getCurrentAccountId(long userId) {
		return _userCurrentAccount.getOrDefault(userId, 0L);
	}

	@Override
	public void setCurrentAccountId(long userId, long accountEntryId) {
		_userCurrentAccount.put(userId, accountEntryId);
	}

	private final Map<Long, Long> _userCurrentAccount =
		new ConcurrentHashMap<>();

}