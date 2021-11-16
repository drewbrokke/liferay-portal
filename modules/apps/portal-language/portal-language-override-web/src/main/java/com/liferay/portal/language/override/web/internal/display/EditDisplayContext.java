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

package com.liferay.portal.language.override.web.internal.display;

import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Drew Brokke
 */
public class EditDisplayContext {

	public String getBackURL() {
		return _backURL;
	}

	public String getKey() {
		return _key;
	}

	public LocalizedValuesMap getValuesLocalizedValuesMap() {
		return _valuesLocalizedValuesMap;
	}

	private final String _backURL;
	private final String _key;
	private final String _selectedLanguage;
	private final LocalizedValuesMap _valuesLocalizedValuesMap;

	public EditDisplayContext(
		String backURL, String key,
		LocalizedValuesMap valuesLocalizedValuesMap,
		LocalizedValuesMap originalValuesLocalizedValuesMap,
		String selectedLanguage) {
		_backURL = backURL;
		_key = key;
		_valuesLocalizedValuesMap = valuesLocalizedValuesMap;
		_originalValuesLocalizedValuesMap = originalValuesLocalizedValuesMap;
		_selectedLanguage = selectedLanguage;
	}

	public String getSelectedLanguage() {
		return _selectedLanguage;
	}

	public LocalizedValuesMap getOriginalValuesLocalizedValuesMap() {
		return _originalValuesLocalizedValuesMap;
	}

	private final LocalizedValuesMap _originalValuesLocalizedValuesMap;

}