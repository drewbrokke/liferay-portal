/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.converter;

import com.liferay.one.jira.constants.BusinessEventConstants;
import com.liferay.one.jira.constants.ProductVersionConstants;
import com.liferay.one.jira.model.ProductVersion;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class ProductVersionConverter extends BaseAssetObjectConverter {

	@Override
	public String getExternalKeyAttributeName() {
		return ProductVersionConstants.ATTRIBUTE_NAME_NAME;
	}

	@Override
	public String getObjectTypeName() {
		return ProductVersionConstants.OBJECT_TYPE_NAME;
	}

	public ProductVersion toProductVersion(JSONObject jsonObject) {
		return new ProductVersion(jsonObject);
	}

	@Override
	protected String getObjectSchemaName() {
		return BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS;
	}

}