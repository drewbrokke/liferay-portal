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

package com.liferay.portal.vulcan.internal.test.util;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

/**
 * @author Brian Wing Shun Chan
 */
public class URLConnectionUtil {

	public static URLConnection createURLConnection(String urlString)
		throws IOException {

		return createURLConnection(new URL(urlString));
	}

	public static URLConnection createURLConnection(URL url)
		throws IOException {

		URLConnection urlConnection = url.openConnection();

		Base64.Encoder encoder = Base64.getEncoder();

		String encodedUserNameAndPassword = encoder.encodeToString(
			"test@liferay.com:test".getBytes(StandardCharsets.UTF_8));

		urlConnection.setRequestProperty(
			"Authorization", "Basic " + encodedUserNameAndPassword);

		return urlConnection;
	}

	public static InputStream getInputStream(String urlString)
		throws IOException {

		URLConnection urlConnection = createURLConnection(urlString);

		urlConnection.connect();

		return urlConnection.getInputStream();
	}

	public static String read(String urlString) throws IOException {
		return StringUtil.read(getInputStream(urlString));
	}

}