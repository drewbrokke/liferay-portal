/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3.internal;

/**
 * @author Drew Brokke
 */
public class StringBundler {

	public static String concat(String... strings) {
		if (strings.length == 0) {
			return _BLANK;
		}

		if (strings.length == 1) {
			return strings[0];
		}

		if (strings.length == 2) {
			return strings[0] + strings[1];
		}

		StringBuilder sb = new StringBuilder(strings.length);

		for (String string : strings) {
			sb.append(string);
		}

		return sb.toString();
	}

	private static final String _BLANK = "";

}