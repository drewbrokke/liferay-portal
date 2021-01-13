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

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.DuplicateAccountGroupRelException;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.base.AccountGroupRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountGroupRel",
	service = AopService.class
)
public class AccountGroupRelLocalServiceImpl
	extends AccountGroupRelLocalServiceBaseImpl {

	@Override
	public AccountGroupRel addAccountGroupRel(
			long accountGroupId, long accountEntryId)
		throws PortalException {

		AccountGroupRel accountGroupRel =
			accountGroupRelPersistence.fetchByAGI_AEI(
				accountGroupId, accountEntryId);

		if (accountGroupRel != null) {
			throw new DuplicateAccountGroupRelException();
		}

		_accountGroupLocalService.getAccountGroup(accountGroupId);

		if (accountEntryId != AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {
			_accountEntryLocalService.getAccountEntry(accountEntryId);
		}

		accountGroupRel = createAccountGroupRel(
			counterLocalService.increment());

		accountGroupRel.setAccountGroupId(accountGroupId);
		accountGroupRel.setAccountEntryId(accountEntryId);

		return addAccountGroupRel(accountGroupRel);
	}

	@Override
	public void addAccountGroupRels(
			long accountGroupId, long[] accountEntryIds)
		throws PortalException {

		for (long accountEntryId : accountEntryIds) {
			addAccountGroupRel(accountGroupId, accountEntryId);
		}
	}

	@Override
	public void deleteAccountGroupRels(
			long accountGroupId, long[] accountEntryIds)
		throws PortalException {

		for (long accountEntryId : accountEntryIds) {
			accountGroupRelPersistence.removeByAGI_AEI(
				accountGroupId, accountEntryId);
		}
	}

	@Override
	public AccountGroupRel fetchAccountGroupRel(
		long accountGroupId, long accountEntryId) {

		return accountGroupRelPersistence.fetchByAGI_AEI(
			accountGroupId, accountEntryId);
	}

	@Override
	public List<AccountGroupRel>
		getAccountGroupRelsByAccountEntryId(long accountEntryId) {

		return accountGroupRelPersistence.findByAccountEntryId(
			accountEntryId);
	}

	@Override
	public List<AccountGroupRel>
		getAccountGroupRelsByAccountGroupId(long accountGroupId) {

		return accountGroupRelPersistence.findByAccountGroupId(
			accountGroupId);
	}

	@Override
	public long getAccountGroupRelsCountByAccountGroupId(
		long accountGroupId) {

		return accountGroupRelPersistence.countByAccountGroupId(
			accountGroupId);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

}