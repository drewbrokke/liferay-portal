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

package com.liferay.dynamic.data.mapping.type.numeric.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidationException;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidator;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=numeric",
	service = DDMFormFieldValueValidator.class
)
public class NumericDDMFormFieldValueValidator
	implements DDMFormFieldValueValidator {

	@Override
	public void validate(
			DDMFormField ddmFormField, DDMFormFieldValue ddmFormFieldValue)
		throws DDMFormFieldValueValidationException {

		Value value = ddmFormFieldValue.getValue();

		for (Locale availableLocale : value.getAvailableLocales()) {
			String valueString = value.getString(availableLocale);

			if (!isNumber(ddmFormField.getDataType(), valueString)) {
				throw new DDMFormFieldValueValidationException(
					String.format(
						"\"%s\" is not a %s", valueString,
						ddmFormField.getDataType()));
			}
		}
	}

	@Activate
	protected void activate() {
		_dataTypeValidatorMap.put("double", Double::parseDouble);
		_dataTypeValidatorMap.put("integer", Integer::parseInt);
	}

	protected boolean isNumber(String dataType, String valueString) {
		Function<String, ?> validatorFunction = _dataTypeValidatorMap.get(
			dataType);

		try {
			validatorFunction.apply(valueString);
		}
		catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	private final Map<String, Function<String, ?>> _dataTypeValidatorMap =
		new HashMap<>();

}