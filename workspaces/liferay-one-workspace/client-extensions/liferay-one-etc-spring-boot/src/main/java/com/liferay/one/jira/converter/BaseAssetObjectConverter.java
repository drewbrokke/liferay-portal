/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AssetSchemaService;
import com.liferay.one.jira.util.AQLUtil;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Amos Fong
 * @author Drew Brokke
 */
public abstract class BaseAssetObjectConverter {

	public String getAQLWithBuilder(Consumer<AQLUtil.Builder> consumer) {
		AQLUtil.Builder builder = AQLUtil.builder(getBaseAQL());

		if (consumer != null) {
			consumer.accept(builder);
		}

		return builder.build();
	}

	/**
	 * The name of the attribute that acts as an external key for this object
	 * type. Defaults to "External Key".
	 */
	public String getExternalKeyAttributeName() {
		return _ATTRIBUTE_NAME_EXTERNAL_KEY;
	}

	public String getObjectTypeId() {
		return _assetSchemaService.getObjectTypeId(
			getObjectSchemaName(), getObjectTypeName());
	}

	public JiraAssetObject toJiraAssetObject(JSONObject jsonObject) {
		return new JiraAssetObject(jsonObject, getAttributeIds());
	}

	protected String formatDate(Date date) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		return simpleDateFormat.format(date);
	}

	protected Map<String, String> getAttributeIds() {
		return _assetSchemaService.getAttributeIds(
			getObjectSchemaName(), getObjectTypeName());
	}

	protected String getBaseAQL() {
		return AQLUtil.getBaseAQL(getObjectSchemaName(), getObjectTypeName());
	}

	protected abstract String getObjectSchemaName();

	protected abstract String getObjectTypeName();

	private static final String _ATTRIBUTE_NAME_EXTERNAL_KEY = "External Key";

	@Autowired
	private AssetSchemaService _assetSchemaService;

}