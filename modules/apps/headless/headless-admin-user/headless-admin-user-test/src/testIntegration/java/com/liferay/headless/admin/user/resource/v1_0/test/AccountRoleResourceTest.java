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

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountRoleSerDes;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountRoleResourceTest extends BaseAccountRoleResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountResource = AccountResource.builder(
		).authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		_userAccountResource = UserAccountResource.builder(
		).authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		_account = _accountResource.postAccount(_randomAccount());

		_sharedAccountRoles = TransformUtil.transform(
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT}),
			serviceBuilderAccountRole -> {
				Role role = serviceBuilderAccountRole.getRole();

				return new AccountRole() {
					{
						accountId =
							serviceBuilderAccountRole.getAccountEntryId();
						description = role.getDescription();
						displayName = role.getTitle();
						id = serviceBuilderAccountRole.getAccountRoleId();
						name = serviceBuilderAccountRole.getRoleName();
						roleId = serviceBuilderAccountRole.getRoleId();
					}
				};
			});
	}

	@After
	@Override
	public void tearDown() throws Exception {
	}

	@Override
	@Test
	public void testDeleteAccountRoleUserAssociation() throws Exception {
		AccountRole accountRole = _addAccountRole(_account);
		UserAccount accountUser = _addAccountUser(_account);

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);

		_accountRoleLocalService.associateUser(
			_account.getId(), accountRole.getId(), accountUser.getId());

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, true);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.deleteAccountRoleUserAssociationHttpResponse(
				_account.getId(), accountRole.getId(), accountUser.getId()));

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);
	}

	@Override
	@Test
	public void testDeleteAccountRoleUserAssociationByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountRole(_account);
		UserAccount accountUser = _addAccountUser(_account);

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);

		_accountRoleLocalService.associateUser(
			_account.getId(), accountRole.getId(), accountUser.getId());

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, true);

		accountRoleResource.
			deleteAccountRoleUserAssociationByExternalReferenceCode(
				_account.getExternalReferenceCode(), accountRole.getId(),
				accountUser.getExternalReferenceCode());

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAccountRolesByExternalReferenceCodePage()
		throws Exception {

		Page<AccountRole> page =
			accountRoleResource.getAccountRolesByExternalReferenceCodePage(
				testGetAccountRolesByExternalReferenceCodePage_getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		String externalReferenceCode =
			testGetAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		String irrelevantExternalReferenceCode =
			testGetAccountRolesByExternalReferenceCodePage_getIrrelevantExternalReferenceCode();

		if (irrelevantExternalReferenceCode != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountRole());

			page =
				accountRoleResource.getAccountRolesByExternalReferenceCodePage(
					irrelevantExternalReferenceCode, null, Pagination.of(1, 2),
					null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantAccountRole),
				(List<AccountRole>)page.getItems());
			assertValid(page);
		}

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole())),
			_sharedAccountRoles);

		page = accountRoleResource.getAccountRolesByExternalReferenceCodePage(
			externalReferenceCode, null,
			Pagination.of(1, expectedAccountRoles.size()), null);

		Assert.assertEquals(expectedAccountRoles.size(), page.getTotalCount());

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page.getItems());
		assertValid(page);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAccountRolesByExternalReferenceCodePageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole())),
			_sharedAccountRoles);

		Page<AccountRole> page1 =
			accountRoleResource.getAccountRolesByExternalReferenceCodePage(
				externalReferenceCode, null, Pagination.of(1, 2), null);

		List<AccountRole> accountRoles1 = (List<AccountRole>)page1.getItems();

		Assert.assertEquals(accountRoles1.toString(), 2, accountRoles1.size());

		Page<AccountRole> page2 =
			accountRoleResource.getAccountRolesByExternalReferenceCodePage(
				externalReferenceCode, null,
				Pagination.of(2, expectedAccountRoles.size() - 1), null);

		Assert.assertEquals(expectedAccountRoles.size(), page2.getTotalCount());

		List<AccountRole> accountRoles2 = (List<AccountRole>)page2.getItems();

		Assert.assertEquals(accountRoles2.toString(), 1, accountRoles2.size());

		Page<AccountRole> page3 =
			accountRoleResource.getAccountRolesByExternalReferenceCodePage(
				externalReferenceCode, null,
				Pagination.of(1, expectedAccountRoles.size()), null);

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page3.getItems());
	}

	@Override
	@Test
	public void testGetAccountRolesPage() throws Exception {
		Page<AccountRole> page = accountRoleResource.getAccountRolesPage(
			testGetAccountRolesPage_getAccountId(),
			RandomTestUtil.randomString(), Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		Long accountId = testGetAccountRolesPage_getAccountId();

		Long irrelevantAccountId =
			testGetAccountRolesPage_getIrrelevantAccountId();

		if (irrelevantAccountId != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountRolesPage_addAccountRole(
					irrelevantAccountId, randomIrrelevantAccountRole());

			page = accountRoleResource.getAccountRolesPage(
				irrelevantAccountId, null, Pagination.of(1, 2), null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Collections.singletonList(irrelevantAccountRole),
				(List<AccountRole>)page.getItems());
			assertValid(page);
		}

		AccountRole accountRole1 = testGetAccountRolesPage_addAccountRole(
			accountId, randomAccountRole());

		AccountRole accountRole2 = testGetAccountRolesPage_addAccountRole(
			accountId, randomAccountRole());

		page = accountRoleResource.getAccountRolesPage(
			accountId, null, Pagination.of(1, 2), null);

		Assert.assertEquals(_addSharedAccountRoles(2), page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(accountRole1, accountRole2),
			(List<AccountRole>)page.getItems());
		assertValid(page);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAccountRolesPageWithPagination() throws Exception {
		Long accountId = testGetAccountRolesPage_getAccountId();

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountRolesPage_addAccountRole(
					accountId, randomAccountRole()),
				testGetAccountRolesPage_addAccountRole(
					accountId, randomAccountRole()),
				testGetAccountRolesPage_addAccountRole(
					accountId, randomAccountRole())),
			_sharedAccountRoles);

		Page<AccountRole> page1 = accountRoleResource.getAccountRolesPage(
			accountId, null, Pagination.of(1, 2), null);

		List<AccountRole> accountRoles1 = (List<AccountRole>)page1.getItems();

		Assert.assertEquals(accountRoles1.toString(), 2, accountRoles1.size());

		Page<AccountRole> page2 = accountRoleResource.getAccountRolesPage(
			accountId, null, Pagination.of(2, expectedAccountRoles.size() - 1),
			null);

		Assert.assertEquals(expectedAccountRoles.size(), page2.getTotalCount());

		List<AccountRole> accountRoles2 = (List<AccountRole>)page2.getItems();

		Assert.assertEquals(accountRoles2.toString(), 1, accountRoles2.size());

		Page<AccountRole> page3 = accountRoleResource.getAccountRolesPage(
			accountId, null, Pagination.of(1, expectedAccountRoles.size()),
			null);

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page3.getItems());
	}

	@Override
	@SuppressWarnings("unchecked")
	@Test
	public void testGraphQLGetAccountRolesPage() throws Exception {
		Long accountId = testGetAccountRolesPage_getAccountId();

		GraphQLField graphQLField = new GraphQLField(
			"accountRoles",
			HashMapBuilder.<String, Object>put(
				"accountId", accountId
			).put(
				"page", 1
			).put(
				"pageSize", _sharedAccountRoles.size() + 2
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject accountRolesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accountRoles");

		Assert.assertEquals(
			_sharedAccountRoles.size(),
			accountRolesJSONObject.get("totalCount"));

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGraphQLAccountRole_addAccountRole(),
				testGraphQLAccountRole_addAccountRole()),
			_sharedAccountRoles);

		accountRolesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accountRoles");

		Assert.assertEquals(
			expectedAccountRoles.size(),
			accountRolesJSONObject.get("totalCount"));

		assertEqualsIgnoringOrder(
			expectedAccountRoles,
			Arrays.asList(
				AccountRoleSerDes.toDTOs(
					accountRolesJSONObject.getString("items"))));
	}

	@Override
	@Test
	public void testPostAccountRoleUserAssociation() throws Exception {
		AccountRole accountRole = _addAccountRole(_account);
		UserAccount accountUser = _addAccountUser(_account);

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.postAccountRoleUserAssociationHttpResponse(
				_account.getId(), accountRole.getId(), accountUser.getId()));

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.postAccountRoleUserAssociationHttpResponse(
				_account.getId(), 0L, accountUser.getId()));
	}

	@Override
	@Test
	public void testPostAccountRoleUserAssociationByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountRole(_account);
		UserAccount accountUser = _addAccountUser(_account);

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountRoleUserAssociationByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(), accountRole.getId(),
					accountUser.getExternalReferenceCode()));

		_assertAccountRoleUserAssociation(
			_account, accountRole, accountUser, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountRoleUserAssociationByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(), 0L,
					accountUser.getExternalReferenceCode()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected AccountRole testDeleteAccountRoleUserAssociation_addAccountRole()
		throws Exception {

		return _addAccountRole(_account);
	}

	@Override
	protected AccountRole
			testDeleteAccountRoleUserAssociationByExternalReferenceCode_addAccountRole()
		throws Exception {

		return _addAccountRole(_account);
	}

	@Override
	protected AccountRole
			testGetAccountRolesByExternalReferenceCodePage_addAccountRole(
				String externalReferenceCode, AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountRoleByExternalReferenceCode(
			externalReferenceCode, accountRole);
	}

	@Override
	protected String
			testGetAccountRolesByExternalReferenceCodePage_getExternalReferenceCode()
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected AccountRole testGetAccountRolesPage_addAccountRole(
			Long accountId, AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountRole(accountId, accountRole);
	}

	@Override
	protected Long testGetAccountRolesPage_getAccountId() {
		return _account.getId();
	}

	@Override
	protected AccountRole testGraphQLAccountRole_addAccountRole()
		throws Exception {

		return accountRoleResource.postAccountRole(
			_account.getId(), randomAccountRole());
	}

	@Override
	protected AccountRole testPostAccountRole_addAccountRole(
			AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountRole(
			_account.getId(), accountRole);
	}

	@Override
	protected AccountRole
			testPostAccountRoleByExternalReferenceCode_addAccountRole(
				AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountRoleByExternalReferenceCode(
			_account.getExternalReferenceCode(), accountRole);
	}

	@Override
	protected AccountRole testPostAccountRoleUserAssociation_addAccountRole()
		throws Exception {

		return _addAccountRole(_account);
	}

	@Override
	protected AccountRole
			testPostAccountRoleUserAssociationByExternalReferenceCode_addAccountRole()
		throws Exception {

		return _addAccountRole(_account);
	}

	private AccountRole _addAccountRole(Account account) throws Exception {
		return accountRoleResource.postAccountRole(
			account.getId(), randomAccountRole());
	}

	private UserAccount _addAccountUser(Account account) throws Exception {
		return _userAccountResource.postAccountUser(
			account.getId(), _randomAccountUser());
	}

	private int _addSharedAccountRoles(int count) {
		return count + _sharedAccountRoles.size();
	}

	private void _assertAccountRoleUserAssociation(
			Account account, AccountRole accountRole, UserAccount accountUser,
			boolean hasAssociation)
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			account.getId());

		UserGroupRole userGroupRole =
			_userGroupRoleLocalService.fetchUserGroupRole(
				accountUser.getId(), accountEntry.getAccountEntryGroupId(),
				accountRole.getRoleId());

		if (hasAssociation) {
			Assert.assertNotNull(userGroupRole);
		}
		else {
			Assert.assertNull(userGroupRole);
		}
	}

	private Account _randomAccount() {
		return new Account() {
			{
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentAccountId = RandomTestUtil.randomLong();
				status = RandomTestUtil.randomInt();
			}
		};
	}

	private UserAccount _randomAccountUser() throws Exception {
		UserAccount userAccount = new UserAccount() {
			{
				additionalName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				alternateName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				birthDate = RandomTestUtil.nextDate();
				dashboardURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				emailAddress =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				familyName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				givenName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificPrefix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificSuffix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				image = StringUtil.toLowerCase(RandomTestUtil.randomString());
				jobTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				profileURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};

		userAccount.setBirthDate(
			() -> {
				Calendar calendar = CalendarFactoryUtil.getCalendar();

				calendar.setTime(RandomTestUtil.nextDate());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				return calendar.getTime();
			});
		userAccount.setUserAccountContactInformation(
			_randomUserAccountContactInformation());

		return userAccount;
	}

	private EmailAddress _randomEmailAddress() throws Exception {
		return new EmailAddress() {
			{
				setEmailAddress(RandomTestUtil.randomString() + "@liferay.com");
				setPrimary(true);
				setType("email-address");
			}
		};
	}

	private Phone _randomPhone() throws Exception {
		return new Phone() {
			{
				setExtension(String.valueOf(RandomTestUtil.randomInt()));
				setPhoneNumber(String.valueOf(RandomTestUtil.randomInt()));
				setPhoneType("personal");
				setPrimary(true);
			}
		};
	}

	private PostalAddress _randomPostalAddress() throws Exception {
		return new PostalAddress() {
			{
				setAddressCountry("united-states");
				setAddressLocality("Diamond Bar");
				setAddressRegion("California");
				setAddressType("personal");
				setPostalCode("91765");
				setPrimary(true);
				setStreetAddressLine1(RandomTestUtil.randomString());
				setStreetAddressLine2(RandomTestUtil.randomString());
				setStreetAddressLine3(RandomTestUtil.randomString());
			}
		};
	}

	private UserAccountContactInformation _randomUserAccountContactInformation()
		throws Exception {

		return new UserAccountContactInformation() {
			{
				setEmailAddresses(new EmailAddress[] {_randomEmailAddress()});
				setFacebook(RandomTestUtil.randomString());
				setJabber(RandomTestUtil.randomString());
				setPostalAddresses(
					new PostalAddress[] {_randomPostalAddress()});
				setSkype(RandomTestUtil.randomString());
				setSms(RandomTestUtil.randomString() + "@liferay.com");
				setTelephones(new Phone[] {_randomPhone()});
				setTwitter(RandomTestUtil.randomString());
				setWebUrls(new WebUrl[] {_randomWebUrl()});
			}
		};
	}

	private WebUrl _randomWebUrl() throws Exception {
		return new WebUrl() {
			{
				setPrimary(true);
				setUrl("https://" + RandomTestUtil.randomString() + ".com");
				setUrlType("personal");
			}
		};
	}

	private Account _account;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private AccountResource _accountResource;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	private List<AccountRole> _sharedAccountRoles;
	private UserAccountResource _userAccountResource;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}