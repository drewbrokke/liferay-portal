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

import java.util.Map;

/**
 * @author Drew Brokke
 */
public class EditDisplayContext {

	public String getKey() {
		return _key;
	}

	public EditDisplayContext(
		String key,
		LocalizedValuesMap localizedValuesMap) {

		_key = key;
		_localizedValuesMap = localizedValuesMap;
	}

	private String _key;

	public LocalizedValuesMap getLocalizedValuesMap() {
		return _localizedValuesMap;
	}

	private LocalizedValuesMap _localizedValuesMap;

}
