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

package com.liferay.data.engine.rest.internal.field.type.v1_0;

import com.liferay.data.engine.rest.dto.v1_0.DataDefinitionField;
import com.liferay.data.engine.rest.internal.field.type.v1_0.util.CustomPropertyUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.soy.data.SoyDataFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Gabriel Albuquerque
 */
public class FieldSetFieldType extends BaseFieldType {

	public FieldSetFieldType(
		DataDefinitionField dataDefinitionField,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		SoyDataFactory soyDataFactory) {

		super(
			dataDefinitionField, httpServletRequest, httpServletResponse,
			soyDataFactory);
	}

	protected static List<Object> getNestedFields(
		Map<String, List<Object>> nestedFieldsMap, String[] nestedFieldNames) {

		Set<Map.Entry<String, List<Object>>> entrySet =
			nestedFieldsMap.entrySet();

		Stream<Map.Entry<String, List<Object>>> stream = entrySet.stream();

		return stream.filter(
			entry -> ArrayUtil.contains(nestedFieldNames, entry.getKey())
		).map(
			entry -> entry.getValue()
		).collect(
			Collectors.toList()
		);
	}

	@Override
	protected void addContext(Map<String, Object> context) {
		Map<String, List<Object>> map =
			(Map<String, List<Object>>)CustomPropertyUtil.getMap(
				dataDefinitionField.getCustomProperties(), "nestedFields");

		if (!map.isEmpty()) {
			List<Object> nestedFields = getNestedFields(
				map,
				getNestedFieldNames(
					CustomPropertyUtil.getString(
						dataDefinitionField.getCustomProperties(),
						"nestedFieldNames"),
					map.keySet()));

			context.put(
				"columnSize",
				getColumnSize(
					nestedFields.size(),
					CustomPropertyUtil.getString(
						dataDefinitionField.getCustomProperties(),
						"orientation", "horizontal")));
			context.put("nestedFields", nestedFields);
		}

		if (context.containsKey("label")) {
			context.put("showLabel", true);
		}
	}

	protected int getColumnSize(int nestedFieldsSize, String orientation) {
		if (Objects.equals(orientation, "vertical")) {
			return _FULL_COLUMN_SIZE;
		}

		if (nestedFieldsSize == 0) {
			return 0;
		}

		return _FULL_COLUMN_SIZE / nestedFieldsSize;
	}

	protected String[] getNestedFieldNames(
		String nestedFieldNames, Set<String> defaultNestedFieldNames) {

		if (Validator.isNotNull(nestedFieldNames)) {
			return StringUtil.split(nestedFieldNames);
		}

		return defaultNestedFieldNames.toArray(
			new String[defaultNestedFieldNames.size()]);
	}

	private static final int _FULL_COLUMN_SIZE = 12;

}