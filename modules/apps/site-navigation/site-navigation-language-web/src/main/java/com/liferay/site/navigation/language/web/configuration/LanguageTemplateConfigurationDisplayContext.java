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

package com.liferay.site.navigation.language.web.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class LanguageTemplateConfigurationDisplayContext {

	public void addTemplateValue(
		String templateKey, String templateDisplayName) {

		_templateValues.add(new String[] {templateKey, templateDisplayName});
	}

	public String getCurrentTemplateName() {
		return _currentTemplateName;
	}

	public String getFieldLabel() {
		return _fieldLabel;
	}

	public String getRedirect() {
		return _redirect;
	}

	public List<String[]> getTemplateValues() {
		return _templateValues;
	}

	public String getTitle() {
		return _title;
	}

	public void setCurrentTemplateName(String currentTemplateName) {
		_currentTemplateName = currentTemplateName;
	}

	public void setFieldLabel(String fieldLabel) {
		_fieldLabel = fieldLabel;
	}

	public void setRedirect(String redirect) {
		_redirect = redirect;
	}

	public void setTitle(String title) {
		_title = title;
	}

	private String _currentTemplateName;
	private String _fieldLabel;
	private String _redirect;
	private final List<String[]> _templateValues = new ArrayList<>();
	private String _title;

}