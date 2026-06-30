/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.service.AssetSchemaService;
import com.liferay.one.jira.util.AQLUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Amos Fong
 */
public abstract class AssetObjectConverter {

	public String getBaseAQL() {
		return AQLUtil.getBaseAQL(getObjectSchemaName(), getObjectTypeName());
	}

	public String getObjectTypeId() {
		return _assetSchemaService.getObjectTypeId(
			getObjectSchemaName(), getObjectTypeName());
	}

	protected Map<String, String> getAttributeIds() {
		return _assetSchemaService.getAttributeIds(
			getObjectSchemaName(), getObjectTypeName());
	}

	protected abstract String getObjectSchemaName();

	protected abstract String getObjectTypeName();

	@Autowired
	private AssetSchemaService _assetSchemaService;

}