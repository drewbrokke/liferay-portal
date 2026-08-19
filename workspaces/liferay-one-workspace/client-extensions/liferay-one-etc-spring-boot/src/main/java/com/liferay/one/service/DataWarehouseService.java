/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.one.model.UsageRecord;

import java.time.YearMonth;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class DataWarehouseService {

	public List<UsageRecord> getUsageRecords(YearMonth yearMonth) {

		// Mocked until the data warehouse billing usage API is available
		// (LPD-92371)

		return Collections.singletonList(
			new UsageRecord(
				new JSONObject(
				).put(
					"accountExternalReferenceCode", "ACCNT-026"
				).put(
					"contractExternalReferenceCode", "C_CONTRACT_AI_HUB"
				).put(
					"entitledQuantity", 50000000
				).put(
					"projectExternalReferenceCode", "PRJCT-026"
				).put(
					"skuExternalReferenceCode", "PRDCT-AI-HUB"
				).put(
					"usageDefinitionExternalReferenceCode", "ai-tokens-monthly"
				).put(
					"usedQuantity", 62500000
				)));
	}

}