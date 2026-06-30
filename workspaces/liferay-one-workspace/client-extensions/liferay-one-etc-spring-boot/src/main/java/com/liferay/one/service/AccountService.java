/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.exception.AccountNotFoundException;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.JiraAssetsService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 */
@Component
public class AccountService extends OneBaseService {

	private final int _PAGE_SIZE = 500;

	public void addAccountUserAccount(
			long accountId, long userId, Long accountRoleId)
		throws Exception {

		if (accountRoleId != null) {
			post(
				getAuthorization(), "",
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/accounts/{accountId}" +
						"/account-roles/{accountRoleId}/user-accounts/{userId}"
				).buildAndExpand(
					accountId, accountRoleId, userId
				).toUri());

			return;
		}

		UserAccount userAccount = _userAccountService.getUserAccount(userId);

		post(
			getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				"/o/headless-admin-user/v1.0/accounts/{accountId}" +
					"/user-accounts/by-email-address/{emailAddress}"
			).buildAndExpand(
				accountId, userAccount.getEmailAddress()
			).toUri());
	}

	public Account fetchAccount(long accountId) throws Exception {
		AccountResource accountResource = getAccountResource(getAuthorization());

		try {
			return accountResource.getAccount(accountId);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			if ((problem != null) && isNotFound(problem.getStatus())) {
				return null;
			}

			throw problemException;
		}
	}

	private AccountResource getAccountResource(String authorization) {
		return AccountResource.builder(
		).endpoint(
			lxcDXPMainDomain, lxcDXPServerProtocol
		).header(
			HttpHeaders.AUTHORIZATION, authorization
		).build();
	}

	public Account getAccount(String externalReferenceCode, Jwt jwt)
		throws Exception {

		AccountResource accountResource = getAccountResource("Bearer " + jwt.getTokenValue());

		return accountResource.getAccountByExternalReferenceCode(
			externalReferenceCode);
	}

	public List<Account> getAllAccounts() throws Exception {
		AccountResource accountResource = getAccountResource(getAuthorization());

		int page = 1;

		List<Account> accounts = new ArrayList<>();

		while (true) {
			Page<Account> accountsPage = accountResource.getAccountsPage(
				null, null, Pagination.of(page, _PAGE_SIZE), null);

			accounts.addAll(accountsPage.getItems());

			if (page >= accountsPage.getLastPage()) {
				break;
			}

			page++;
		}

		return accounts;
	}

	public void syncJSMAccount(String externalKey, JiraAssetObject payload) {
		JiraAssetObject jiraAssetObject = fetchAccountJiraAssetObjectByExternalKey(externalKey);

		if (jiraAssetObject != null) {
			System.out.println("Account found for key " + externalKey);
			_jiraAssetsService.updateObject(jiraAssetObject.getObjectId(), payload);
		}
		else {
			System.out.println("Account not found for key " + externalKey);
			_jiraAssetsService.createObject(_accountConverter.getObjectTypeId(), payload);
		}
	}

	public String getAccountObjectKey(String externalKey) throws Exception {
		JiraAssetObject jiraAssetObject = getAccountJiraAssetObjectByExternalKey(externalKey);

		return jiraAssetObject.getObjectKey();
	}

	private JiraAssetObject fetchAccountJiraAssetObjectByExternalKey(String externalKey) {
		List<JiraAssetObject> objects = _jiraAssetsService.searchObjects(
			_accountConverter.getAQLWithBuilder(
				aqlBuilder -> aqlBuilder.andEquals(
					externalKey, "External Key")),
			_accountConverter::toJiraAssetObject);

		if (objects.isEmpty()) {
			return null;
		}

		return objects.get(0);
	}

	private JiraAssetObject getAccountJiraAssetObjectByExternalKey(String externalKey) throws Exception {
		JiraAssetObject jiraAssetObject = fetchAccountJiraAssetObjectByExternalKey(externalKey);

		if (jiraAssetObject == null) {
			throw new AccountNotFoundException();
		}

		return jiraAssetObject;
	}

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private JiraAssetsService _jiraAssetsService;

	@Autowired
	private UserAccountService _userAccountService;

}
