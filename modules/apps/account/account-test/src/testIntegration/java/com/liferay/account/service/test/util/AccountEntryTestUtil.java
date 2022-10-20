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

package com.liferay.account.service.test.util;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class AccountEntryTestUtil {

	@SafeVarargs
	public static List<AccountEntry> addAccountEntries(
			int number,
			UnsafeConsumer<AccountEntryInfo, Exception>...
				accountEntryInfoUnsafeConsumers)
		throws Exception {

		List<AccountEntry> accountEntries = new ArrayList<>();

		for (int i = 0; i < number; i++) {
			accountEntries.add(
				addAccountEntry(accountEntryInfoUnsafeConsumers));
		}

		return accountEntries;
	}

	@SafeVarargs
	public static AccountEntry addAccountEntry(
			UnsafeConsumer<AccountEntryInfo, Exception>...
				accountEntryInfoUnsafeConsumers)
		throws Exception {

		AccountEntryInfo accountEntryInfo = new AccountEntryInfo();

		if (ArrayUtil.isNotEmpty(accountEntryInfoUnsafeConsumers)) {
			for (UnsafeConsumer<AccountEntryInfo, Exception>
					accountEntryInfoUnsafeConsumer :
						accountEntryInfoUnsafeConsumers) {

				accountEntryInfoUnsafeConsumer.accept(accountEntryInfo);
			}
		}

		return addAccountEntry(accountEntryInfo);
	}

	public static AccountEntry addAccountEntry(
			AccountEntryInfo accountEntryInfo)
		throws Exception {

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.addAccountEntry(
				accountEntryInfo.userId, accountEntryInfo.parentAccountEntryId,
				accountEntryInfo.name, accountEntryInfo.description,
				accountEntryInfo.domains, accountEntryInfo.emailAddress,
				accountEntryInfo.logoBytes, accountEntryInfo.taxIdNumber,
				accountEntryInfo.type, accountEntryInfo.status,
				accountEntryInfo.serviceContext);

		if (ArrayUtil.isNotEmpty(accountEntryInfo.accountGroupIds)) {
			for (long accountGroupId : accountEntryInfo.accountGroupIds) {
				AccountGroupRelLocalServiceUtil.addAccountGroupRel(
					accountGroupId, AccountEntry.class.getName(),
					accountEntry.getAccountEntryId());
			}
		}

		if (ArrayUtil.isNotEmpty(accountEntryInfo.organizationIds)) {
			AccountEntryOrganizationRelLocalServiceUtil.
				addAccountEntryOrganizationRels(
					accountEntry.getAccountEntryId(),
					accountEntryInfo.organizationIds);
		}

		if (ArrayUtil.isNotEmpty(accountEntryInfo.userIds)) {
			AccountEntryUserRelLocalServiceUtil.addAccountEntryUserRels(
				accountEntry.getAccountEntryId(), accountEntryInfo.userIds);
		}

		return accountEntry;
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withAccountGroups(
		AccountGroup... accountGroups) {

		return accountEntryInfo ->
			accountEntryInfo.accountGroupIds = ListUtil.toLongArray(
				Arrays.asList(accountGroups),
				AccountGroup.ACCOUNT_GROUP_ID_ACCESSOR);
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withDomains(
		String... domains) {

		return accountEntryInfo -> accountEntryInfo.domains = domains;
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withOrganizations(
		Organization... organizations) {

		return accountEntryInfo ->
			accountEntryInfo.organizationIds = ListUtil.toLongArray(
				Arrays.asList(organizations),
				Organization.ORGANIZATION_ID_ACCESSOR);
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withOwner(
		User user) {

		return accountEntryInfo -> accountEntryInfo.userId = user.getUserId();
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception>
		withStatusInactive() {

		return accountEntryInfo ->
			accountEntryInfo.status = WorkflowConstants.STATUS_INACTIVE;
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withTypePerson() {
		return accountEntryInfo ->
			accountEntryInfo.type = AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON;
	}

	public static UnsafeConsumer<AccountEntryInfo, Exception> withUsers(
		User... users) {

		return accountEntryInfo ->
			accountEntryInfo.userIds = ListUtil.toLongArray(
				Arrays.asList(users), User.USER_ID_ACCESSOR);
	}

	public static class AccountEntryInfo {

		public AccountEntryInfo() throws PortalException {
		}

		public long[] accountGroupIds = null;
		public String description = RandomTestUtil.randomString(50);
		public String[] domains = null;
		public String emailAddress = null;
		public byte[] logoBytes = null;
		public String name = RandomTestUtil.randomString(50);
		public long[] organizationIds = null;
		public long parentAccountEntryId = 0L;
		public ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();
		public int status = WorkflowConstants.STATUS_APPROVED;
		public String taxIdNumber = RandomTestUtil.randomString(50);
		public String type = AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
		public long userId = TestPropsValues.getUserId();
		public long[] userIds = null;

	}

}