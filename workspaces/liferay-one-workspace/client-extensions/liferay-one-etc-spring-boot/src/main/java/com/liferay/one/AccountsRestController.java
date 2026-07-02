/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AccountAssetService;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.permission.BusinessEventPermission;
import com.liferay.one.service.AccountService;
import com.liferay.one.service.UserAccountService;
import com.liferay.portal.kernel.security.permission.ActionKeys;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Jenny Chen
 */
@RequestMapping("/accounts")
@RestController
public class AccountsRestController extends OneBaseRestController {

	@GetMapping("/{externalReferenceCode}/jira/object-key")
	public ResponseEntity<String> getJiraObjectKey(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_businessEventPermission.check(
			externalReferenceCode, ActionKeys.VIEW, jwt);

		return new ResponseEntity<>(
			_accountAssetService.getAccountObjectKey(externalReferenceCode),
			HttpStatus.OK);
	}

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() throws Exception {
		try {
			List<Account> accounts = _accountService.getAllAccounts();

			if (_log.isInfoEnabled()) {
				_log.info("Syncing " + accounts.size() + " accounts to JSM");
			}

			for (Account account : accounts) {
				_syncAccount(account);
			}

			if (_log.isInfoEnabled()) {
				_log.info("Synced " + accounts.size() + " accounts to JSM");
			}
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing the JIRA object keys",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void _syncAccount(Account account) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing account " + account.getExternalReferenceCode() +
					" to JSM");
		}

		JiraAssetObject assetObject = _accountConverter.toAssetObject(account);

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_CUSTOMER_CONTACTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_contactConverter,
				_userAccountService.getAccountUserAccounts(account.getId()),
				UserAccount::getExternalReferenceCode,
				_contactConverter::toAssetObject));

		_accountAssetService.upsertJSMAccount(
			account.getExternalReferenceCode(), assetObject);
	}

	private static final Log _log = LogFactory.getLog(
		AccountsRestController.class);

	@Autowired
	private AccountAssetService _accountAssetService;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private BusinessEventPermission _businessEventPermission;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private UserAccountService _userAccountService;

}