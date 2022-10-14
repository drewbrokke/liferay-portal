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

package com.liferay.portal.language;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Drew Brokke
 */
public class GenderedTranslationHelper {

	public GenderedTranslationHelper(Boolean masculine) {
		_masculine = masculine;
	}

	public String decorateTranslation(String translation) {
		if (_masculine == null) {
			return translation;
		}

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _genderedTranslationPattern.matcher(translation);

		int group = _masculine ? 1 : 2;

		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(group));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final Pattern _genderedTranslationPattern = Pattern.compile(
		"\\[(\\w+?)\\|(\\w+?)]");

	private final Boolean _masculine;

}