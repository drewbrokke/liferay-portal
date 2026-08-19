/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.model;

import org.json.JSONObject;

/**
 * @author Drew Brokke
 */
public class UsageRecord {

	public UsageRecord(JSONObject jsonObject) {
		_accountExternalReferenceCode = jsonObject.optString(
			"accountExternalReferenceCode");
		_contractExternalReferenceCode = jsonObject.optString(
			"contractExternalReferenceCode");
		_entitledQuantity = jsonObject.getDouble("entitledQuantity");
		_projectExternalReferenceCode = jsonObject.optString(
			"projectExternalReferenceCode");
		_skuExternalReferenceCode = jsonObject.optString(
			"skuExternalReferenceCode");
		_usageDefinitionExternalReferenceCode = jsonObject.optString(
			"usageDefinitionExternalReferenceCode");
		_usedQuantity = jsonObject.getDouble("usedQuantity");
	}

	public String getAccountExternalReferenceCode() {
		return _accountExternalReferenceCode;
	}

	public String getContractExternalReferenceCode() {
		return _contractExternalReferenceCode;
	}

	public double getEntitledQuantity() {
		return _entitledQuantity;
	}

	public String getProjectExternalReferenceCode() {
		return _projectExternalReferenceCode;
	}

	public String getSkuExternalReferenceCode() {
		return _skuExternalReferenceCode;
	}

	public String getUsageDefinitionExternalReferenceCode() {
		return _usageDefinitionExternalReferenceCode;
	}

	public double getUsedQuantity() {
		return _usedQuantity;
	}

	private final String _accountExternalReferenceCode;
	private final String _contractExternalReferenceCode;
	private final double _entitledQuantity;
	private final String _projectExternalReferenceCode;
	private final String _skuExternalReferenceCode;
	private final String _usageDefinitionExternalReferenceCode;
	private final double _usedQuantity;

}