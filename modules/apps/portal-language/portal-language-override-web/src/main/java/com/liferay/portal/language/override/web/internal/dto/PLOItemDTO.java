/*
 * *
 *  * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package com.liferay.portal.language.override.web.internal.dto;

/**
 * @author Drew Brokke
 */
public class PLOItemDTO {

	public PLOItemDTO(String key, String type) {
		_key = key;
		_type = type;
		_valueLanguageIds = null;
	}

	public String getKey() {
		return _key;
	}

	public String getType() {
		return _type;
	}

	public String[] getValueLanguageIds() {
		return _valueLanguageIds;
	}

	private final String _key;
	private final String _type;


	private final String[] _valueLanguageIds;

}
