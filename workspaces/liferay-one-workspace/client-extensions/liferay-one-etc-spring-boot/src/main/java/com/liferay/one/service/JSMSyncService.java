/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountSerDes;

import java.util.List;
import java.util.function.Function;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class JSMSyncService extends OneBaseService {

	public List<Account> getAllAccounts(String filterString) throws Exception {
		return getAllItems(
			"/o/headless-admin-user/v1.0/accounts", filterString,
			jsonObject -> AccountSerDes.toDTO(jsonObject.toString()));
	}

	public List<JSONObject> getAllItems(String path, String filterString)
		throws Exception {

		return getAllItems(path, filterString, Function.identity());
	}

}