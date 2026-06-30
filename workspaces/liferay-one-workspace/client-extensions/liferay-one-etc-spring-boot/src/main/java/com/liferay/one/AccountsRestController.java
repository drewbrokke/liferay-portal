/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.JiraAssetsService;
import com.liferay.one.permission.BusinessEventPermission;
import com.liferay.one.service.AccountService;
import com.liferay.portal.kernel.security.permission.ActionKeys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
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

		try {
			_businessEventPermission.check(
				externalReferenceCode, ActionKeys.VIEW, jwt);

			return new ResponseEntity<>(
				_accountService.getAccountObjectKey(externalReferenceCode),
				HttpStatus.OK);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get Jira object key for " + externalReferenceCode,
				exception);

			return new ResponseEntity<>(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() throws Exception {
		try {
			for (Account account : _accountService.getAllAccounts()) {
//				_syncAccount(_accountService.fetchAccount(account.getId()));
				_syncAccount(account);

				break;
			}
		} catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing the JIRA object keys", exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void _syncAccount(Account account) throws Exception {
		System.out.println("account = " + account);

		JiraAssetObject assetObject = _accountConverter.toAssetObject(account);

		System.out.println("assetObject = " + assetObject);
		System.out.println("assetObject.toAttributesJSONArray() = " + assetObject.toAttributesJSONArray());

		_accountService.syncJSMAccount(account.getExternalReferenceCode(), assetObject);
	}

	@Autowired
	private JiraAssetsService _jiraAssetsService;

	@Autowired
	private AccountConverter _accountConverter;

	private static final Log _log = LogFactory.getLog(
		AccountsRestController.class);

	@Autowired
	private AccountService _accountService;

	@Autowired
	private BusinessEventPermission _businessEventPermission;

}
