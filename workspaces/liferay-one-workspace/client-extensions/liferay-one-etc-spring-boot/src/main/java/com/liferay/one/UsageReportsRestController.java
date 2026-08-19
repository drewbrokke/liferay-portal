/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.one.service.UsageReportService;
import com.liferay.portal.kernel.util.Validator;

import java.time.YearMonth;
import java.time.ZoneOffset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Drew Brokke
 */
@RequestMapping("/usage-reports")
@RestController
public class UsageReportsRestController extends OneBaseRestController {

	@PostMapping("/generate")
	public void postUsageReportsGenerate(
			@RequestParam(required = false) String yearMonth)
		throws Exception {

		_usageReportService.generateUsageReports(_getYearMonth(yearMonth));
	}

	@Scheduled(cron = "${liferay.one.usage.report.cron}", zone = "UTC")
	protected void scheduledGenerateUsageReports() {
		try {
			_usageReportService.generateUsageReports(_getYearMonth(null));
		}
		catch (Exception exception) {
			_log.error("Unable to generate usage reports", exception);
		}
	}

	private YearMonth _getYearMonth(String yearMonth) {
		if (Validator.isNull(yearMonth)) {
			YearMonth currentYearMonth = YearMonth.now(ZoneOffset.UTC);

			return currentYearMonth.minusMonths(1);
		}

		return YearMonth.parse(yearMonth);
	}

	private static final Log _log = LogFactory.getLog(
		UsageReportsRestController.class);

	@Autowired
	private UsageReportService _usageReportService;

}