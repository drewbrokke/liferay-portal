/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.one.jira.model.SupportIssue;
import com.liferay.one.jira.service.JiraIssueService;
import com.liferay.one.permission.BusinessEventPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ActionKeys;

import java.util.List;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jenny Chen
 */
@RequestMapping("/jira")
@RestController
public class JiraRestController extends OneBaseRestController {

	@GetMapping("/accounts/{externalReferenceCode}/tickets")
	public ResponseEntity<String> getAccountsTickets(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@RequestParam(defaultValue = StringPool.BLANK, required = false)
				String[] ticketIds)
		throws Exception {

		_businessEventPermission.check(
			externalReferenceCode, ActionKeys.VIEW, jwt);

		return _getResponseEntity(
			_jiraIssueService.getSupportIssues(
				externalReferenceCode, ticketIds),
			SupportIssue::toJSONObject);
	}

	private <T> ResponseEntity<String> _getResponseEntity(
		List<T> items, Function<T, JSONObject> transformFunction) {

		JSONObject responseJSONObject = new JSONObject();

		JSONArray itemsJSONArray = new JSONArray();

		for (T item : items) {
			itemsJSONArray.put(transformFunction.apply(item));
		}

		responseJSONObject.put("items", itemsJSONArray);

		return new ResponseEntity<>(
			responseJSONObject.toString(), HttpStatus.OK);
	}

	@Autowired
	private BusinessEventPermission _businessEventPermission;

	@Autowired
	private JiraIssueService _jiraIssueService;

}