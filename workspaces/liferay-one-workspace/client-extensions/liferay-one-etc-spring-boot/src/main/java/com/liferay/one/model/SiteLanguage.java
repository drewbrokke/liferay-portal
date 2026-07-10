/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.model;

import org.json.JSONObject;

/**
 * @author Drew Brokke
 */
public class SiteLanguage {

	public SiteLanguage(JSONObject jsonObject) {
		_countryName = jsonObject.optString("countryName");
		_id = jsonObject.optString("id");
		_markedAsDefault = jsonObject.optBoolean("markedAsDefault");
		_name = jsonObject.optString("name");
	}

	public String getCountryName() {
		return _countryName;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public boolean isMarkedAsDefault() {
		return _markedAsDefault;
	}

	private final String _countryName;
	private final String _id;
	private final boolean _markedAsDefault;
	private final String _name;

}