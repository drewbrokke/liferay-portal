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

package com.liferay.account.retriever.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.retriever.AccountUserRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.test.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.comparator.UserEmailAddressComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class AccountUserRetrieverWhenSearchingAssignableUsersTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldReturnNoUsersIfAccountEntryHasNoDomain()
		throws Exception {

		_users.add(UserTestUtil.addUser());
		_users.add(_addDomainUser());

		_accountEntry = AccountEntryTestUtil.addAccountEntry(
			_accountEntryLocalService);

		BaseModelSearchResult<User> baseModelSearchResult =
			_accountUserRetriever.searchAssignableUsers(
				_accountEntry.getAccountEntryId(), true, null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, "email-address", false);

		Assert.assertEquals(0, baseModelSearchResult.getLength());
		Assert.assertTrue(
			ListUtil.isEmpty(baseModelSearchResult.getBaseModels()));
	}

	@Test
	public void testShouldReturnUsersWithAnyEmailDomain() throws Exception {
		_users.add(UserTestUtil.addUser());
		_users.add(_addDomainUser());

		_accountEntry = AccountEntryTestUtil.addAccountEntry(
			_accountEntryLocalService, new String[] {_TEST_EMAIL_DOMAIN});

		BaseModelSearchResult<User> users =
			_accountUserRetriever.searchAssignableUsers(
				_accountEntry.getAccountEntryId(), false, null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, "screen-name", false);

		BaseModelSearchResult<User> expectedUsers =
			_userLocalService.searchUsers(
				TestPropsValues.getCompanyId(), null,
				WorkflowConstants.STATUS_APPROVED, new LinkedHashMap<>(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new Sort("screenName", false));

		Assert.assertEquals(expectedUsers.getLength(), users.getLength());
		Assert.assertEquals(
			expectedUsers.getBaseModels(), users.getBaseModels());
	}

	@Test
	public void testShouldReturnUsersWithMatchingEmailDomain()
		throws Exception {

		_users.add(UserTestUtil.addUser());

		List<User> domainUsers = new ArrayList<>();

		domainUsers.add(_addDomainUser());
		domainUsers.add(_addDomainUser());

		_users.addAll(domainUsers);

		_accountEntry = AccountEntryTestUtil.addAccountEntry(
			_accountEntryLocalService, new String[] {_TEST_EMAIL_DOMAIN});

		BaseModelSearchResult<User> baseModelSearchResult =
			_accountUserRetriever.searchAssignableUsers(
				_accountEntry.getAccountEntryId(), true, null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, "email-address", false);

		Assert.assertEquals(
			domainUsers.size(), baseModelSearchResult.getLength());
		Assert.assertEquals(
			ListUtil.sort(domainUsers, new UserEmailAddressComparator(true)),
			baseModelSearchResult.getBaseModels());
	}

	private User _addDomainUser() throws Exception {
		String emailAddress =
			RandomTestUtil.randomString() + StringPool.AT + _TEST_EMAIL_DOMAIN;

		return UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			StringPool.BLANK, emailAddress, RandomTestUtil.randomString(),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());
	}

	private static final String _TEST_EMAIL_DOMAIN = "test.com";

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountUserRetriever _accountUserRetriever;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}