/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.one.model.SiteLanguage;

import java.util.List;

import org.json.JSONObject;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Drew Brokke
 */
@Component
public class SiteLanguageService extends OneBaseService {

	@Cacheable("siteLanguages")
	public List<SiteLanguage> getSiteLanguages() throws Exception {
		return getAllItems(
			"/o/headless-delivery/v1.0/sites/" + _getSiteId() + "/languages",
			null, SiteLanguage::new);
	}

	private long _getSiteId() throws Exception {
		String response = get(
			getAuthorization(),
			UriComponentsBuilder.fromPath(
				"/o/headless-site/v1.0/sites/by-external-reference-code/" +
					_SITE_EXTERNAL_REFERENCE_CODE
			).build(
			).toUri());

		JSONObject jsonObject = new JSONObject(response);

		return jsonObject.getLong("id");
	}

	private static final String _SITE_EXTERNAL_REFERENCE_CODE = "LIFERAY_ONE";

}