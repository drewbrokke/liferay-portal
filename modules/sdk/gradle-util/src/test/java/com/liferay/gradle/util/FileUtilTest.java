/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.util;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class FileUtilTest {

	@Test
	public void testBuildMirrorsUrlCustomHostname() throws Exception {
		Assert.assertEquals(
			"http://mirrors.example.com/example.com/foo/bar.zip",
			_buildMirrorsUrl(
				"https://example.com/foo/bar.zip", "mirrors.example.com",
				false));
	}

	@Test
	public void testBuildMirrorsUrlDefault() throws Exception {
		Assert.assertEquals(
			"http://mirrors.lax.liferay.com/example.com/foo/bar.zip",
			_buildMirrorsUrl(
				"https://example.com/foo/bar.zip", "mirrors.lax.liferay.com",
				false));
	}

	@Test
	public void testBuildMirrorsUrlDisabledWhenEmpty() throws Exception {
		Assert.assertNull(
			_buildMirrorsUrl("https://example.com/foo/bar.zip", "", false));
	}

	@Test
	public void testBuildMirrorsUrlDisabledWhenNull() throws Exception {
		Assert.assertNull(
			_buildMirrorsUrl("https://example.com/foo/bar.zip", null, false));
	}

	@Test
	public void testBuildMirrorsUrlQuotesReplacement() throws Exception {
		Assert.assertEquals(
			"http://mirrors.$1.example.com/example.com/foo/bar.zip",
			_buildMirrorsUrl(
				"https://example.com/foo/bar.zip", "mirrors.$1.example.com",
				false));
	}

	@Test
	public void testBuildMirrorsUrlSsl() throws Exception {
		Assert.assertEquals(
			"https://mirrors.lax.liferay.com/example.com/foo/bar.zip",
			_buildMirrorsUrl(
				"http://example.com/foo/bar.zip", "mirrors.lax.liferay.com",
				true));
	}

	private String _buildMirrorsUrl(
			String url, String mirrorsHostname, boolean ssl)
		throws Exception {

		Method method = FileUtil.class.getDeclaredMethod(
			"_buildMirrorsUrl", String.class, String.class, boolean.class);

		method.setAccessible(true);

		return (String)method.invoke(null, url, mirrorsHostname, ssl);
	}

}