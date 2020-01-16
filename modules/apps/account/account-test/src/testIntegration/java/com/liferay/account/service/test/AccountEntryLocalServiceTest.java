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

package com.liferay.account.service.test;

import com.liferay.account.exception.AccountEntryDomainsException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.retriever.AccountUserRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class AccountEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_accountEntryIndexer = IndexerRegistryUtil.getIndexer(
			AccountEntry.class);
	}

	@After
	public void tearDown() throws Exception {
		for (AccountEntry accountEntry : _accountEntries) {
			accountEntry = _accountEntryLocalService.fetchAccountEntry(
				accountEntry.getAccountEntryId());

			if (accountEntry != null) {
				_accountEntryLocalService.deleteAccountEntry(accountEntry);
			}
		}
	}

	@Test
	public void testAccountEntryGroup() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		Group group = accountEntry.getAccountEntryGroup();

		Assert.assertNotNull(group);

		long classNameId = _classNameLocalService.getClassNameId(
			AccountEntry.class);

		Assert.assertEquals(classNameId, group.getClassNameId());

		Assert.assertEquals(
			accountEntry.getAccountEntryId(), group.getClassPK());

		long accountEntryGroupId = accountEntry.getAccountEntryGroupId();

		Assert.assertNotEquals(
			GroupConstants.DEFAULT_LIVE_GROUP_ID, accountEntryGroupId);

		Assert.assertEquals(group.getGroupId(), accountEntryGroupId);

		_accountEntryLocalService.deleteAccountEntry(accountEntry);

		Assert.assertNull(accountEntry.getAccountEntryGroup());
	}

	@Test
	public void testActivateAccountEntries() throws Exception {
		long[] accountEntryIds = _addAccountEntries(
			WorkflowConstants.STATUS_INACTIVE);

		for (long accountEntryId : accountEntryIds) {
			_assertStatus(accountEntryId, WorkflowConstants.STATUS_INACTIVE);
		}

		_accountEntryLocalService.activateAccountEntries(accountEntryIds);

		for (long accountEntryId : accountEntryIds) {
			_assertStatus(accountEntryId, WorkflowConstants.STATUS_APPROVED);
		}
	}

	@Test
	public void testActivateAccountEntryByModel() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(
			WorkflowConstants.STATUS_INACTIVE);

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_INACTIVE);

		_accountEntryLocalService.activateAccountEntry(accountEntry);

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testActivateAccountEntryByPrimaryKey() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(
			WorkflowConstants.STATUS_INACTIVE);

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_INACTIVE);

		_accountEntryLocalService.activateAccountEntry(
			accountEntry.getAccountEntryId());

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddAccountEntry() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		Assert.assertNotNull(
			_accountEntryLocalService.fetchAccountEntry(
				accountEntry.getAccountEntryId()));

		int resourcePermissionsCount =
			_resourcePermissionLocalService.getResourcePermissionsCount(
				TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(accountEntry.getAccountEntryId()));

		Assert.assertEquals(1, resourcePermissionsCount);
	}

	@Test
	public void testAddAccountEntryWithDomains() throws Exception {
		String[] domains = {"test1.com", "test.1.com", "test-1.com"};

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			_accountEntryLocalService, domains);

		_accountEntries.add(accountEntry);

		Assert.assertEquals(
			StringUtil.merge(ArrayUtil.distinct(domains), ","),
			accountEntry.getDomains());
	}

	@Test
	public void testAddAccountEntryWithInvalidDomains() throws Exception {
		String[] invalidDomains = {
			"invalid", ".invalid", "-invalid", "invalid-", "_invalid",
			"invalid_", "@invalid.com", "invalid#domain", "invalid&domain",
			"invalid!.com", "invalid$domain.com", "invalid%.com", "*invalid",
			"invalid*", "invalid.*.com", "invalid+domain", ">", "<"
		};

		for (String domain : invalidDomains) {
			try {
				AccountEntryTestUtil.addAccountEntry(
					_accountEntryLocalService, new String[] {domain});

				Assert.fail(
					"Created an account entry with invalid domain " + domain);
			}
			catch (AccountEntryDomainsException aede) {
			}
		}
	}

	@Test
	public void testDeactivateAccountEntries() throws Exception {
		long[] accountEntryIds = _addAccountEntries();

		for (long accountEntryId : accountEntryIds) {
			_assertStatus(accountEntryId, WorkflowConstants.STATUS_APPROVED);
		}

		_accountEntryLocalService.deactivateAccountEntries(accountEntryIds);

		for (long accountEntryId : accountEntryIds) {
			_assertStatus(accountEntryId, WorkflowConstants.STATUS_INACTIVE);
		}
	}

	@Test
	public void testDeactivateAccountEntryByModel() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_APPROVED);

		_accountEntryLocalService.deactivateAccountEntry(accountEntry);

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_INACTIVE);
	}

	@Test
	public void testDeactivateAccountEntryByPrimaryKey() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_APPROVED);

		_accountEntryLocalService.deactivateAccountEntry(
			accountEntry.getAccountEntryId());

		_assertStatus(
			accountEntry.getAccountEntryId(),
			WorkflowConstants.STATUS_INACTIVE);
	}

	@Test
	public void testDeleteAccountEntries() throws Exception {
		long[] accountEntryIds = _addAccountEntries();

		_accountEntryLocalService.deleteAccountEntries(accountEntryIds);

		for (long accountEntryId : accountEntryIds) {
			_assertDeleted(accountEntryId);
		}
	}

	@Test
	public void testDeleteAccountEntryByModel() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_accountEntryLocalService.deleteAccountEntry(accountEntry);

		_assertDeleted(accountEntry.getAccountEntryId());
	}

	@Test
	public void testDeleteAccountEntryByPrimaryKey() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_accountEntryLocalService.deleteAccountEntry(
			accountEntry.getAccountEntryId());

		_assertDeleted(accountEntry.getAccountEntryId());
	}

	@Test
	public void testIndexerDocument() throws Exception {

		/**
		 * Set up
		 */
		AccountEntry accountEntry = _addAccountEntry();

		User user = UserTestUtil.addUser();

		_users.add(user);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		User user1 = UserTestUtil.addUser();

		_users.add(user1);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user1.getUserId());

		accountEntry.setDomains("one,two,three");

		/**
		 * Create Document
		 */
		Document document = _accountEntryIndexer.getDocument(accountEntry);

		/**
		 * Assert
		 */
		Assert.assertEquals(
			accountEntry.getDescription(), document.get(Field.DESCRIPTION));
		Assert.assertEquals(accountEntry.getName(), document.get(Field.NAME));
		Assert.assertEquals(
			accountEntry.getStatus(),
			GetterUtil.getInteger(document.get(Field.STATUS), -1));
		Assert.assertArrayEquals(
			_getAccountUserIds(accountEntry),
			GetterUtil.getLongValues(document.getValues("accountUserIds")));
		Assert.assertArrayEquals(
			_getDomains(accountEntry), document.getValues("domains"));
		Assert.assertEquals(
			accountEntry.getParentAccountEntryId(),
			GetterUtil.getLong(document.get("parentAccountEntryId"), -1L));
	}

	@Test
	public void testIndexerExists() throws Exception {
		Assert.assertNotNull(_accountEntryIndexer);
	}

	private long[] _addAccountEntries() throws Exception {
		return _addAccountEntries(WorkflowConstants.STATUS_APPROVED);
	}

	private long[] _addAccountEntries(int status) throws Exception {
		int size = 5;

		long[] accountEntryIds = new long[size];

		for (int i = 0; i < size; i++) {
			AccountEntry accountEntry = _addAccountEntry(status);

			accountEntryIds[i] = accountEntry.getAccountEntryId();
		}

		return accountEntryIds;
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _addAccountEntry(WorkflowConstants.STATUS_APPROVED);
	}

	private AccountEntry _addAccountEntry(int status) throws Exception {
		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			_accountEntryLocalService, status);

		_accountEntries.add(accountEntry);

		return accountEntry;
	}

	private void _assertDeleted(long accountEntryId) throws Exception {
		Assert.assertNull(
			_accountEntryLocalService.fetchAccountEntry(accountEntryId));

		int resourcePermissionsCount =
			_resourcePermissionLocalService.getResourcePermissionsCount(
				TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(accountEntryId));

		Assert.assertEquals(0, resourcePermissionsCount);
	}

	private void _assertStatus(long accountEntryId, int expectedStatus) {
		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		Assert.assertEquals(expectedStatus, accountEntry.getStatus());
	}

	private long[] _getAccountUserIds(AccountEntry accountEntry) {
		return ListUtil.toLongArray(
			_accountUserRetriever.getAccountUsers(
				accountEntry.getAccountEntryId()),
			User.USER_ID_ACCESSOR);
	}

	private String[] _getDomains(AccountEntry accountEntry) {
		return ArrayUtil.toStringArray(
			StringUtil.split(accountEntry.getDomains(), CharPool.COMMA));
	}

	private final List<AccountEntry> _accountEntries = new ArrayList<>();
	private Indexer<AccountEntry> _accountEntryIndexer;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountUserRetriever _accountUserRetriever;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}