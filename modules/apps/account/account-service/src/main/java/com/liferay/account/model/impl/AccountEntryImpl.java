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

package com.liferay.account.model.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class AccountEntryImpl extends AccountEntryBaseImpl {

	public AccountEntryImpl() {
	}

	@Override
	public Group getAccountEntryGroup() {
		return _getAccountEntryGroup();
	}

	@Override
	public long getAccountEntryGroupId() {
		Group group = _getAccountEntryGroup();

		if (group == null) {
			return GroupConstants.DEFAULT_LIVE_GROUP_ID;
		}

		return group.getGroupId();
	}

	@Override
	public List<AccountEntryOrganizationRel> getAccountEntryOrganizationRels() {
		return AccountEntryOrganizationRelLocalServiceUtil.
			getAccountEntryOrganizationRels(getAccountEntryId());
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRels() {
		return AccountEntryUserRelLocalServiceUtil.
			getAccountEntryUserRelsByAccountEntryId(getAccountEntryId());
	}

	@Override
	public Address getDefaultBillingAddress() {
		return AddressLocalServiceUtil.fetchAddress(
			getDefaultBillingAddressId());
	}

	@Override
	public Address getDefaultShippingAddress() {
		return AddressLocalServiceUtil.fetchAddress(
			getDefaultShippingAddressId());
	}

	@Override
	public AccountEntry getParentAccountEntry() {
		if (isRoot()) {
			return null;
		}

		return AccountEntryLocalServiceUtil.fetchAccountEntry(
			getParentAccountEntryId());
	}

	@Override
	public boolean isBusinessAccount() {
		return _isType(AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
	}

	@Override
	public boolean isGuestAccount() {
		return _isType(AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST);
	}

	@Override
	public boolean isPersonAccount() {
		return _isType(AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON);
	}

	@Override
	public boolean isRoot() {
		if (getParentAccountEntryId() ==
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT) {

			return true;
		}

		return false;
	}

	private Group _getAccountEntryGroup() {
		return GroupLocalServiceUtil.fetchGroup(
			getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(AccountEntry.class),
			getAccountEntryId());
	}

	private boolean _isType(String type) {
		if (getType() == type) {
			return true;
		}

		return false;
	}

}