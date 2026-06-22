/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.one.service.JSMSyncService;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Drew Brokke
 */
@RequestMapping("/jsm-sync")
@RestController
public class JSMSyncRestController extends BaseRestController {

	public JSMSyncRestController(JSMSyncService jsmSyncService) {
		_jsmSyncService = jsmSyncService;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception exception) {
		_log.error(exception);

		return new ResponseEntity<>(
			exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/account")
	public void syncAccount(@RequestBody String payload) throws Exception {
		JSONObject jsonObject = new JSONObject(payload);

		JSONObject modelDTOAccountJSONObject = jsonObject.getJSONObject(
			"modelDTOAccountJSONObject");

		_normalizeDates(modelDTOAccountJSONObject);

		_syncAccount(Account.toDTO(modelDTOAccountJSONObject.toString()));
	}

	@PostMapping("/accounts")
	public void syncAccounts() throws Exception {
		for (Account account : _jsmSyncService.getAllAccounts(null)) {
			_syncAccount(account);
		}
	}

	@GetMapping("/test-path")
	public String testPath(
			@RequestParam String filter, @RequestParam String path)
		throws Exception {

		System.out.println("TESTING PATH: " + path);

		StringBuilder stringBuilder = new StringBuilder();

		for (JSONObject jsonObject :
				_jsmSyncService.getAllItems(path, filter)) {

			System.out.println(jsonObject.toString());
			stringBuilder.append(jsonObject);
		}

		return stringBuilder.toString();
	}

	private void _normalizeDates(JSONObject jsonObject) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		if (jsonObject.has("dateCreated")) {
			jsonObject.put(
				"dateCreated",
				simpleDateFormat.format(
					new Date(jsonObject.getLong("dateCreated"))));
		}

		if (jsonObject.has("dateModified")) {
			jsonObject.put(
				"dateModified",
				simpleDateFormat.format(
					new Date(jsonObject.getLong("dateModified"))));
		}
	}

	private void _syncAccount(Account account) {
		System.out.println("Syncing account: " + account.getName());
	}

	private static final Log _log = LogFactory.getLog(
		JSMSyncRestController.class);

	private final JSMSyncService _jsmSyncService;

}