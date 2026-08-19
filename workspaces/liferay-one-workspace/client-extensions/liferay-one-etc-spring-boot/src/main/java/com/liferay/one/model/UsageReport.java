/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.model;

import org.json.JSONObject;

/**
 * @author Drew Brokke
 */
public class UsageReport {

	public UsageReport(JSONObject jsonObject) {
		_accountExternalReferenceCode = jsonObject.optString(
			"accountExternalReferenceCode");
		_aggregateQuantity = jsonObject.optDouble("aggregateQuantity", 0);
		_commerceOrderId = jsonObject.optLong("commerceOrderId");
		_contractExternalReferenceCode = jsonObject.optString(
			"contractExternalReferenceCode");
		_entitledQuantity = jsonObject.optDouble("entitledQuantity", 0);
		_externalReferenceCode = jsonObject.optString("externalReferenceCode");
		_overageAmount = jsonObject.optDouble("overageAmount", 0);
		_overageCurrency = jsonObject.optString("overageCurrency");
		_overageQuantity = jsonObject.optDouble("overageQuantity", 0);
		_projectId = jsonObject.optLong("r_projectToUsageReport_c_projectId");
		_reviewStatus = _getReviewStatus(jsonObject);
		_skuExternalReferenceCode = jsonObject.optString(
			"skuExternalReferenceCode");
		_usageDefinitionId = jsonObject.optLong(
			"r_usageDefinitionToUsageReport_c_usageDefinitionId");
		_usageReportId = jsonObject.getLong("id");
	}

	public String getAccountExternalReferenceCode() {
		return _accountExternalReferenceCode;
	}

	public double getAggregateQuantity() {
		return _aggregateQuantity;
	}

	public long getCommerceOrderId() {
		return _commerceOrderId;
	}

	public String getContractExternalReferenceCode() {
		return _contractExternalReferenceCode;
	}

	public double getEntitledQuantity() {
		return _entitledQuantity;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public double getOverageAmount() {
		return _overageAmount;
	}

	public String getOverageCurrency() {
		return _overageCurrency;
	}

	public double getOverageQuantity() {
		return _overageQuantity;
	}

	public long getProjectId() {
		return _projectId;
	}

	public String getReviewStatus() {
		return _reviewStatus;
	}

	public String getSkuExternalReferenceCode() {
		return _skuExternalReferenceCode;
	}

	public long getUsageDefinitionId() {
		return _usageDefinitionId;
	}

	public long getUsageReportId() {
		return _usageReportId;
	}

	private String _getReviewStatus(JSONObject jsonObject) {
		JSONObject reviewStatusJSONObject = jsonObject.optJSONObject(
			"reviewStatus");

		if (reviewStatusJSONObject != null) {
			return reviewStatusJSONObject.optString("key");
		}

		return jsonObject.optString("reviewStatus");
	}

	private final String _accountExternalReferenceCode;
	private final double _aggregateQuantity;
	private final long _commerceOrderId;
	private final String _contractExternalReferenceCode;
	private final double _entitledQuantity;
	private final String _externalReferenceCode;
	private final double _overageAmount;
	private final String _overageCurrency;
	private final double _overageQuantity;
	private final long _projectId;
	private final String _reviewStatus;
	private final String _skuExternalReferenceCode;
	private final long _usageDefinitionId;
	private final long _usageReportId;

}