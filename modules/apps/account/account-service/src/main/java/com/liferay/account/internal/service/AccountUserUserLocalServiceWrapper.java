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

package com.liferay.account.internal.service;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.model.AccountEntryUserRelModel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = ServiceWrapper.class)
public class AccountUserUserLocalServiceWrapper
	extends UserLocalServiceWrapper {

	public AccountUserUserLocalServiceWrapper() {
		super(null);
	}

	public AccountUserUserLocalServiceWrapper(
		UserLocalService userLocalService) {

		super(userLocalService);
	}

	@Override
	public long[] getGroupPrimaryKeys(long userId) {
		List<AccountEntryUserRel> accountEntryUserRelsByAccountUserId =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(userId);

		return LongStream.concat(
			Arrays.stream(super.getGroupPrimaryKeys(userId)),
			accountEntryUserRelsByAccountUserId.stream(
			).map(
				AccountEntryUserRelModel::getAccountEntryId
			).filter(
				accountEntryId -> accountEntryId > 0
			).map(
				_accountEntryLocalService::fetchAccountEntry
			).mapToLong(
				AccountEntry::getAccountEntryGroupId
			)
		).toArray();
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

}