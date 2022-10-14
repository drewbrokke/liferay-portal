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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Drew Brokke
 */
public class GenderedArgument {

	public static final String SUFFIX_FEMININE = "###f";

	public static final String SUFFIX_MASCULINE = "###m";

	public static GenderedArgument create(String argument) {
		Matcher matcher = _genderedArgumentPattern.matcher(argument);

		if (matcher.find()) {
			return new GenderedArgument(
				matcher.group(1), Objects.equals(matcher.group(2), "m"));
		}

		return new GenderedArgument(argument, null);
	}

	public static String markAsFeminine(String argument) {
		return argument + SUFFIX_FEMININE;
	}

	public static String markAsMasculine(String argument) {
		return argument + SUFFIX_MASCULINE;
	}

	public Boolean getMasculine() {
		return _masculine;
	}

	public String getText() {
		return _text;
	}

	public boolean isGendered() {
		if (_masculine != null) {
			return true;
		}

		return false;
	}

	private GenderedArgument(String text, Boolean masculine) {
		_text = text;
		_masculine = masculine;
	}

	private static final Pattern _genderedArgumentPattern = Pattern.compile(
		"(.+)###([mf])$");

	private final Boolean _masculine;
	private final String _text;

}