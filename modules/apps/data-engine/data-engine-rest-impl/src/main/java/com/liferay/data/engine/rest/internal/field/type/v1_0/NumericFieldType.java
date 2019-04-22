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
import com.liferay.data.engine.rest.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.internal.field.type.v1_0.util.CustomPropertyUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.template.soy.data.SoyDataFactory;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gabriel Albuquerque
 */
public class NumericFieldType extends BaseFieldType {

	public NumericFieldType(
		DataDefinitionField dataDefinitionField,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		SoyDataFactory soyDataFactory) {

		super(
			dataDefinitionField, httpServletRequest, httpServletResponse,
			soyDataFactory);
	}

	@Override
	public DataDefinitionField deserialize(JSONObject jsonObject)
		throws Exception {

		DataDefinitionField dataDefinitionField = super.deserialize(jsonObject);

		dataDefinitionField.setCustomProperties(
			CustomPropertyUtil.add(
				dataDefinitionField.getCustomProperties(), "dataType",
				jsonObject.getString("dataType")));
		dataDefinitionField.setCustomProperties(
			CustomPropertyUtil.add(
				dataDefinitionField.getCustomProperties(), "placeholder",
				LocalizedValueUtil.toLocalizedValues(
					jsonObject.getJSONObject("placeholder"))));
		dataDefinitionField.setCustomProperties(
			CustomPropertyUtil.add(
				dataDefinitionField.getCustomProperties(), "predefinedValue",
				LocalizedValueUtil.toLocalizedValues(
					jsonObject.getJSONObject("predefinedValue"))));
		dataDefinitionField.setCustomProperties(
			CustomPropertyUtil.add(
				dataDefinitionField.getCustomProperties(), "tooltip",
				LocalizedValueUtil.toLocalizedValues(
					jsonObject.getJSONObject("tooltip"))));

		return dataDefinitionField;
	}

	@Override
	public JSONObject toJSONObject() throws Exception {
		JSONObject jsonObject = super.toJSONObject();

		jsonObject.put(
			"dataType",
			CustomPropertyUtil.getString(
				dataDefinitionField.getCustomProperties(), "dataType")
		).put(
			"placeholder",
			CustomPropertyUtil.getLocalizedValue(
				dataDefinitionField.getCustomProperties(), "placeholder")
		).put(
			"predefinedValue",
			CustomPropertyUtil.getLocalizedValue(
				dataDefinitionField.getCustomProperties(), "predefinedValue")
		).put(
			"tooltip",
			CustomPropertyUtil.getLocalizedValue(
				dataDefinitionField.getCustomProperties(), "tooltip")
		);

		return jsonObject;
	}

	@Override
	protected void addContext(Map<String, Object> context) {
		context.put(
			"dataType",
			CustomPropertyUtil.getString(
				dataDefinitionField.getCustomProperties(), "dataType",
				"decimal"));
		context.put(
			"placeholder",
			LocalizedValueUtil.getLocalizedValue(
				httpServletRequest.getLocale(),
				CustomPropertyUtil.getLocalizedValue(
					dataDefinitionField.getCustomProperties(), "placeholder")));
		context.put(
			"predefinedValue",
			_getFormattedValue(
				LocalizedValueUtil.getLocalizedValue(
					httpServletRequest.getLocale(),
					CustomPropertyUtil.getLocalizedValue(
						dataDefinitionField.getCustomProperties(),
						"predefinedValue")),
				httpServletRequest.getLocale()));
		context.put(
			"symbols",
			_getSymbolsMap(httpServletRequest.getLocale()));
		context.put(
			"tooltip",
			LocalizedValueUtil.getLocalizedValue(
				httpServletRequest.getLocale(),
				CustomPropertyUtil.getLocalizedValue(
					dataDefinitionField.getCustomProperties(), "tooltip")));
		context.put(
			"value",
			_getFormattedValue(
				CustomPropertyUtil.getString(
					dataDefinitionField.getCustomProperties(), "value"),
				httpServletRequest.getLocale()));
	}

	private static String _getFormattedValue(Object value, Locale locale) {
		if (Validator.isNull(value) ||
			StringUtil.equals((String)value, "NaN")) {

			return StringPool.BLANK;
		}

		DecimalFormat numberFormat = _getNumberFormat(locale);

		return numberFormat.format(GetterUtil.getNumber(value));
	}

	private static DecimalFormat _getNumberFormat(Locale locale) {
		DecimalFormat formatter = (DecimalFormat)DecimalFormat.getInstance(locale);

		formatter.setGroupingUsed(false);
		formatter.setMaximumFractionDigits(Integer.MAX_VALUE);
		formatter.setParseBigDecimal(true);

		return formatter;
	}

	private static Map<String, String> _getSymbolsMap(Locale locale) {
		DecimalFormat formatter = _getNumberFormat(locale);

		DecimalFormatSymbols decimalFormatSymbols =
			formatter.getDecimalFormatSymbols();

		Map<String, String> symbolsMap = new HashMap<>();

		symbolsMap.put(
			"decimalSymbol",
			String.valueOf(decimalFormatSymbols.getDecimalSeparator()));
		symbolsMap.put(
			"thousandsSeparator",
			String.valueOf(decimalFormatSymbols.getGroupingSeparator()));

		return symbolsMap;
	}

}