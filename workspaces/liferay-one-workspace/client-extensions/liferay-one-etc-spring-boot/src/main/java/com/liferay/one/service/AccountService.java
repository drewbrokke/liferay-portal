/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.one.jira.client.JiraAssetObject;
import com.liferay.one.jira.client.JiraAssetsClient;
import com.liferay.one.jira.converter.AccountConverter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * @author Amos Fong
 */
@Component
public class AccountService extends OneBaseService {

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
		AccountResource accountResource = AccountResource.builder(
		).endpoint(
			lxcDXPMainDomain, lxcDXPServerProtocol
		).header(
			HttpHeaders.AUTHORIZATION, getAuthorization()
		).build();

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

	public Account getAccount(String externalReferenceCode, Jwt jwt)
		throws Exception {

		AccountResource accountResource = AccountResource.builder(
		).endpoint(
			lxcDXPMainDomain, lxcDXPServerProtocol
		).header(
			HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()
		).build();

		return accountResource.getAccountByExternalReferenceCode(
			externalReferenceCode);
	}

	public String getAccountObjectKey(String externalKey) {
		List<JiraAssetObject> objects = _jiraAssetsClient.searchObjects(
			_accountConverter.getAQLWithBuilder(
				aqlBuilder -> aqlBuilder.andEquals(
					externalKey, "External Key")),
			_accountConverter::toJiraAssetObject);

		if (objects.isEmpty()) {
			return null;
		}

		JiraAssetObject jiraAssetObject = objects.getFirst();

		return jiraAssetObject.getObjectKey();
	}

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private JiraAssetsClient _jiraAssetsClient;

	@Autowired
	private UserAccountService _userAccountService;

}