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

import java.util.Locale;
import java.util.Set;

/**
 * @author Drew Brokke
 */
public class EditDisplayContext {

	public EditDisplayContext(
		String backURL, String key, LocalizedValuesMap valuesLocalizedValuesMap,
		LocalizedValuesMap originalValuesLocalizedValuesMap,
		String selectedLanguage) {

		_backURL = backURL;
		_key = key;
		_valuesLocalizedValuesMap = valuesLocalizedValuesMap;
		_originalValuesLocalizedValuesMap = originalValuesLocalizedValuesMap;
		_selectedLanguage = selectedLanguage;
	}

	private Set<Locale> _availableLocales;

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public String getBackURL() {
		return _backURL;
	}

	public String getKey() {
		return _key;
	}

	public LocalizedValuesMap getOriginalValuesLocalizedValuesMap() {
		return _originalValuesLocalizedValuesMap;
	}

	public String getSelectedLanguage() {
		return _selectedLanguage;
	}

	public LocalizedValuesMap getValuesLocalizedValuesMap() {
		return _valuesLocalizedValuesMap;
	}

	public boolean isShowOriginalValues() {
		return _showOriginalValues;
	}

	public void setBackURL(String backURL) {
		_backURL = backURL;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setOriginalValuesLocalizedValuesMap(
		LocalizedValuesMap originalValuesLocalizedValuesMap) {

		_originalValuesLocalizedValuesMap = originalValuesLocalizedValuesMap;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		_selectedLanguage = selectedLanguage;
	}

	public void setShowOriginalValues(boolean showOriginalValues) {
		_showOriginalValues = showOriginalValues;
	}

	public void setValuesLocalizedValuesMap(
		LocalizedValuesMap valuesLocalizedValuesMap) {

		_valuesLocalizedValuesMap = valuesLocalizedValuesMap;
	}

	private String _backURL;
	private String _key;
	private LocalizedValuesMap _originalValuesLocalizedValuesMap;
	private String _selectedLanguage;
	private boolean _showOriginalValues;
	private LocalizedValuesMap _valuesLocalizedValuesMap;

}