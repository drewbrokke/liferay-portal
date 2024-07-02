/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.resource.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class ResourceUtilTest extends ResourceUtil {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Path tempDirectoryPath = Files.createTempDirectory("ResourceUtilTest_");

		File tempDirectoryFile = tempDirectoryPath.toFile();

		tempDirectoryFile.deleteOnExit();

		_newFile = new File(tempDirectoryFile, "newFile.txt");

		Files.createFile(_newFile.toPath());

		_nontexistentFile = new File(tempDirectoryFile, "nonexistentFile.txt");

		_oldFile = new File(tempDirectoryFile, "oldFile.txt");

		Files.createFile(_oldFile.toPath());

		Instant now = Instant.now();

		Files.setLastModifiedTime(
			_oldFile.toPath(),
			FileTime.from(now.minus(_OLD_FILE_AGE, ChronoUnit.DAYS)));
	}

	@Test
	public void testGetClassLoaderResolver() throws Exception {
		_assertResolveThrows(
			exception -> Assert.assertEquals(
				"Unable to get resource from class path: nonexistent",
				exception.getMessage()),
			NullPointerException.class, getClassLoaderResolver("nonexistent"));

		_assertResolveSucceeds(getClassLoaderResolver("/root.txt"));
		_assertResolveSucceeds(
			getClassLoaderResolver(ResourceUtilTest.class, "foo.txt"));
	}

	@Test
	public void testGetLocalFileResolver() throws Exception {
		int days = _OLD_FILE_AGE - 1;

		_assertResolveSucceeds(getLocalFileResolver(_newFile));
		_assertResolveSucceeds(getLocalFileResolver(_oldFile));
		_assertResolveSucceeds(
			getLocalFileResolver(_newFile, days, ChronoUnit.DAYS));

		_assertResolveThrows(
			exception -> Assert.assertEquals(
				String.format(
					"Unable to get resource from local file: %s",
					_nontexistentFile.getAbsolutePath()),
				exception.getMessage()),
			FileNotFoundException.class,
			getLocalFileResolver(_nontexistentFile));
		_assertResolveThrows(
			exception -> Assert.assertEquals(
				String.format(
					"Cached file %s is older than max age of 2 Days",
					_oldFile.getAbsolutePath()),
				exception.getMessage()),
			Exception.class,
			getLocalFileResolver(_oldFile, days, ChronoUnit.DAYS));
	}

	@Test
	public void testGetURIResolver() {
	}

	@Test
	public void testGetURLResolver() {
	}

	@Test
	public void testReadJson() {
	}

	@Test
	public void testReadProperties() {
	}

	@Test
	public void testReadString() {
	}

	private void _assertResolveSucceeds(Resolver resolver) throws Exception {
		try (InputStream inputStream = resolver.resolve()) {
			Assert.assertNotNull(inputStream);
		}
	}

	private void _assertResolveThrows(
		Consumer<Exception> consumer, Class<?> exceptionClass,
		Resolver resolver) {

		try (InputStream ignoredInputStream = resolver.resolve()) {
			resolver.resolve();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(exceptionClass, exception.getClass());

			if (consumer != null) {
				consumer.accept(exception);
			}
		}
	}

	private static final int _OLD_FILE_AGE = 3;

	private static File _newFile;
	private static File _nontexistentFile;
	private static File _oldFile;

}