/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.one.constants.ClassNameConstants;
import com.liferay.one.constants.UsageReportConstants;
import com.liferay.one.model.Project;
import com.liferay.one.model.UsageDefinition;
import com.liferay.one.model.UsageRecord;
import com.liferay.one.model.UsageReport;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Drew Brokke
 * @author Ryan Schuhler
 */
@Component
public class UsageReportService extends OneBaseService {

	public UsageReport fetchUsageReport(String externalReferenceCode)
		throws Exception {

		String response = null;

		try {
			response = get(
				getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/usagereports/by-external-reference-code" +
						"/{externalReferenceCode}"
				).buildAndExpand(
					externalReferenceCode
				).toUri());
		}
		catch (WebClientResponseException webClientResponseException) {
			int statusCode = webClientResponseException.getStatusCode(
			).value();

			if (statusCode != HttpStatus.NOT_FOUND.value()) {
				throw webClientResponseException;
			}
		}

		if (Validator.isNull(response)) {
			return null;
		}

		return new UsageReport(new JSONObject(response));
	}

	public void generateUsageReports(YearMonth yearMonth) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Generating usage reports for " + yearMonth);
		}

		List<UsageRecord> usageRecords = _dataWarehouseService.getUsageRecords(
			yearMonth);

		Map<String, UsageDefinition> usageDefinitions = new HashMap<>();

		int overageCount = 0;

		for (UsageRecord usageRecord : usageRecords) {
			try {
				JSONObject usageReportJSONObject = _createUsageReportJSONObject(
					usageDefinitions, usageRecord, yearMonth);

				if (usageReportJSONObject == null) {
					continue;
				}

				_addUsageReport(usageReportJSONObject);

				overageCount++;
			}
			catch (Exception exception) {
				_log.error(
					"Unable to generate usage report for project " +
						usageRecord.getProjectExternalReferenceCode(),
					exception);
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Generated ", overageCount, " usage report(s) for ",
					yearMonth));
		}
	}

	private void _addUsageReport(JSONObject usageReportJSONObject)
		throws Exception {

		String externalReferenceCode = usageReportJSONObject.getString(
			"externalReferenceCode");

		put(
			getAuthorization(), usageReportJSONObject.toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/usagereports/by-external-reference-code" +
					"/{externalReferenceCode}"
			).buildAndExpand(
				externalReferenceCode
			).toUri());

		if (_log.isInfoEnabled()) {
			_log.info(
				"Generated usage report " + externalReferenceCode +
					" ready for review");
		}
	}

	private JSONObject _createUsageReportJSONObject(
			Map<String, UsageDefinition> usageDefinitions,
			UsageRecord usageRecord, YearMonth yearMonth)
		throws Exception {

		double overageQuantity =
			usageRecord.getUsedQuantity() - usageRecord.getEntitledQuantity();

		if (overageQuantity <= 0) {
			return null;
		}

		UsageDefinition usageDefinition = _getUsageDefinition(
			usageDefinitions,
			usageRecord.getUsageDefinitionExternalReferenceCode());

		if (usageDefinition == null) {
			_log.error(
				"Unable to find usage definition " +
					usageRecord.getUsageDefinitionExternalReferenceCode());

			return null;
		}

		Project project = _projectService.fetchProject(
			usageRecord.getProjectExternalReferenceCode());

		if (project == null) {
			_log.error(
				"Unable to find project " +
					usageRecord.getProjectExternalReferenceCode());

			return null;
		}

		String externalReferenceCode = _getExternalReferenceCode(
			project.getExternalReferenceCode(), yearMonth);

		UsageReport usageReport = fetchUsageReport(externalReferenceCode);

		if (usageReport != null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Skipping usage report " + externalReferenceCode +
						" because it already exists");
			}

			return null;
		}

		if (usageDefinition.getOverageRate() <= 0) {
			_log.error(
				StringBundler.concat(
					"Unable to generate usage report ", externalReferenceCode,
					" because usage definition ",
					usageDefinition.getExternalReferenceCode(),
					" has no overage rate"));

			return null;
		}

		double overageAmount =
			overageQuantity * usageDefinition.getOverageRate();

		Instant generatedAtInstant = Instant.now(
		).truncatedTo(
			ChronoUnit.SECONDS
		);

		return new JSONObject(
		).put(
			"accountExternalReferenceCode",
			usageRecord.getAccountExternalReferenceCode()
		).put(
			"aggregateQuantity", usageRecord.getUsedQuantity()
		).put(
			"contractExternalReferenceCode",
			usageRecord.getContractExternalReferenceCode()
		).put(
			"dateFrom", yearMonth.atDay(1) + "T00:00:00Z"
		).put(
			"dateTo", yearMonth.atEndOfMonth() + "T23:59:59Z"
		).put(
			"entitledQuantity", usageRecord.getEntitledQuantity()
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"generatedAt", generatedAtInstant.toString()
		).put(
			"generatorClassName", UsageReportService.class.getName()
		).put(
			"overageAmount", overageAmount
		).put(
			"overageCurrency", usageDefinition.getOverageCurrency()
		).put(
			"overageQuantity", overageQuantity
		).put(
			"r_projectToUsageReport_c_projectId", project.getProjectId()
		).put(
			"r_usageDefinitionToUsageReport_c_usageDefinitionId",
			usageDefinition.getUsageDefinitionId()
		).put(
			"reviewStatus", UsageReportConstants.REVIEW_STATUS_READY_FOR_REVIEW
		).put(
			"skuExternalReferenceCode",
			usageRecord.getSkuExternalReferenceCode()
		).put(
			"targetClassName", ClassNameConstants.PROJECT
		).put(
			"targetPK", project.getProjectId()
		).put(
			"targetType", UsageReportConstants.TARGET_TYPE_PROJECT
		);
	}

	private String _getExternalReferenceCode(
		String projectExternalReferenceCode, YearMonth yearMonth) {

		// C_USAGE_REPORT_<PROJECT_ERC>_<YYYY_MM>, one report per project per
		// month, e.g. C_USAGE_REPORT_PRJCT_026_2026_07

		String projectKey = StringUtil.toUpperCase(
			StringUtil.replace(projectExternalReferenceCode, '-', '_'));

		return StringBundler.concat(
			"C_USAGE_REPORT_", projectKey, "_",
			yearMonth.format(_ERC_YEAR_MONTH_DATE_TIME_FORMATTER));
	}

	private UsageDefinition _getUsageDefinition(
			Map<String, UsageDefinition> usageDefinitions,
			String externalReferenceCode)
		throws Exception {

		UsageDefinition usageDefinition = usageDefinitions.get(
			externalReferenceCode);

		if (usageDefinition == null) {
			usageDefinition = _usageDefinitionService.fetchUsageDefinition(
				externalReferenceCode);

			usageDefinitions.put(externalReferenceCode, usageDefinition);
		}

		return usageDefinition;
	}

	private static final DateTimeFormatter _ERC_YEAR_MONTH_DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy_MM");

	private static final Log _log = LogFactory.getLog(UsageReportService.class);

	@Autowired
	private DataWarehouseService _dataWarehouseService;

	@Autowired
	private ProjectService _projectService;

	@Autowired
	private UsageDefinitionService _usageDefinitionService;

}