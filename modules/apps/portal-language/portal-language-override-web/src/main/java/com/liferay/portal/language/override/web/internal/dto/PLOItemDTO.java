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

package com.liferay.portal.language.override.web.internal.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class PLOItemDTO {

	public PLOItemDTO(String key, String value) {
		_key = key;
		_value = value;
	}

	public String getKey() {
		return _key;
	}

	public String getValue() {
		return _value;
	}

	public boolean isOverride() {
		return _override;
	}

	public void setOverride(boolean override) {
		_override = override;
	}

	public List<String> getOverrideLanguages() {
		return _overrideLanguages;
	}

	public void addOverrideLanguage(String language) {
		_overrideLanguages.add(language);
	}

	private boolean _overrideSelectedLanguage;

	public boolean isOverrideSelectedLanguage() {
		return _overrideSelectedLanguage;
	}

	public void setOverrideSelectedLanguage(boolean overrideSelectedLanguage) {
		_overrideSelectedLanguage = overrideSelectedLanguage;
	}

	private final String _key;
	private boolean _override;
	private final String _value;
	private final List<String> _overrideLanguages = new ArrayList<>();

}