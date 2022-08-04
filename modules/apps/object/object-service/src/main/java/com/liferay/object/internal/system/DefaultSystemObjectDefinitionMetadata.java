/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.internal.system;

import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.system.SystemObjectDefinitionMetadata;
import com.liferay.object.system.SystemObjectDefinitionMetadataContributor;
import com.liferay.object.util.LocalizedMapUtil;
import com.liferay.object.util.ObjectFieldUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class DefaultSystemObjectDefinitionMetadata
	implements SystemObjectDefinitionMetadata {

	public DefaultSystemObjectDefinitionMetadata(
		SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor) {
		this.systemObjectDefinitionMetadataContributor = systemObjectDefinitionMetadataContributor;
	}

	protected final SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor;

	@Override
	public Map<Locale, String> getLabelMap() {
		return systemObjectDefinitionMetadataContributor.getLabelMap();
	}

	@Override
	public Class<?> getModelClass() {
		return systemObjectDefinitionMetadataContributor.getModelClass();
	}

	@Override
	public String getModelClassName() {
		Class<?> modelClass = getModelClass();

		return modelClass.getName();
	}

	@Override
	public String getName() {
		Table table = getTable();

		String tableName = table.getName();

		if (tableName.endsWith("_")) {
			return tableName.substring(0, tableName.length() - 1);
		}

		return tableName;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return systemObjectDefinitionMetadataContributor.getObjectFields();
	}

	@Override
	public Map<Locale, String> getPluralLabelMap() {
		return systemObjectDefinitionMetadataContributor.getPluralLabelMap();
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return systemObjectDefinitionMetadataContributor.getPrimaryKeyColumn();
	}

	@Override
	public String getRESTContextPath() {
		return systemObjectDefinitionMetadataContributor.getRESTContextPath();
	}

	@Override
	public String getRESTDTOIdPropertyName() {
		return "id";
	}

	@Override
	public String getScope() {
		return systemObjectDefinitionMetadataContributor.getScope();
	}

	@Override
	public Table getTable() {
		return systemObjectDefinitionMetadataContributor.getTable();
	}

	@Override
	public int getVersion() {
		return systemObjectDefinitionMetadataContributor.getVersion();
	}

	protected Map<Locale, String> createLabelMap(String labelKey) {
		return LocalizedMapUtil.getLocalizedMap(_translate(labelKey));
	}

	protected ObjectField createObjectField(
		String businessType, String dbType, String labelKey, String name,
		boolean required, boolean system) {

		return createObjectField(
			businessType, null, dbType, labelKey, name, required, system);
	}

	protected ObjectField createObjectField(
		String businessType, String dbColumnName, String dbType,
		String labelKey, String name, boolean required, boolean system) {

		return ObjectFieldUtil.createObjectField(
			0, businessType, dbColumnName, dbType, false, false, null,
			_translate(labelKey), name, required, system);
	}

	private String _translate(String labelKey) {
		return LanguageUtil.get(LocaleUtil.getDefault(), labelKey);
	}

}