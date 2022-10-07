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
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class AccountEntryTestUtil {

	public static AccountEntry addAccountEntry(
			AccountEntryLocalService accountEntryLocalService)
		throws Exception {

		return addAccountEntry(
			accountEntryLocalService, WorkflowConstants.STATUS_APPROVED);
	}

	public static AccountEntry addAccountEntry(
			AccountEntryLocalService accountEntryLocalService, boolean active)
		throws Exception {

		return accountEntryLocalService.addAccountEntry(
			TestPropsValues.getUserId(), 0L, RandomTestUtil.randomString(50),
			RandomTestUtil.randomString(50), null, null, null,
			RandomTestUtil.randomString(50),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, active,
			ServiceContextTestUtil.getServiceContext());
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static AccountEntry addAccountEntry(
			AccountEntryLocalService accountEntryLocalService, int status)
		throws Exception {

		return addAccountEntry(
			accountEntryLocalService,
			Objects.equals(WorkflowConstants.STATUS_APPROVED, status));
	}

	public static AccountEntry addAccountEntry(
			AccountEntryLocalService accountEntryLocalService, String name,
			String description)
		throws Exception {

		return accountEntryLocalService.addAccountEntry(
			TestPropsValues.getUserId(), 0L, name, description, null, null,
			null, RandomTestUtil.randomString(50),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, true,
			ServiceContextTestUtil.getServiceContext());
	}

	public static AccountEntry addAccountEntry(
			AccountEntryLocalService accountEntryLocalService, String[] domains)
		throws Exception {

		return accountEntryLocalService.addAccountEntry(
			TestPropsValues.getUserId(), 0L, RandomTestUtil.randomString(50),
			RandomTestUtil.randomString(50), domains, null, null,
			RandomTestUtil.randomString(50),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, true,
			ServiceContextTestUtil.getServiceContext());
	}

	public static AccountEntry addPersonAccountEntry(
			AccountEntryLocalService accountEntryLocalService)
		throws Exception {

		return accountEntryLocalService.addAccountEntry(
			TestPropsValues.getUserId(), 0L, RandomTestUtil.randomString(50),
			RandomTestUtil.randomString(50), null, null, null,
			RandomTestUtil.randomString(50),
			AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON, true,
			ServiceContextTestUtil.getServiceContext());
	}

}