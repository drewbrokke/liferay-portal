/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.release.util;

import java.io.File;
import java.io.InputStream;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class ReleaseUtilTest {

	@Before
	public void setUp() throws Exception {
		_mirrorDir = _createTempDir("releasesMirror");
		_workspaceCacheDir = _createTempDir("workspaceCache");
	}

	@Test
	public void testCustomMirrors() throws Exception {
		_writeFile(
			"releases.json", Paths.get(_mirrorDir.getPath(), "releases.json"));
		_writeFile(
			"release.properties",
			Paths.get(
				_mirrorDir.getPath(), "dxp", "second", "release.properties"));

		URI mirrorDirURI = _mirrorDir.toURI();

		ReleaseUtil.initialize(
			0, Collections.singletonList(mirrorDirURI.toString()),
			_workspaceCacheDir);

		ReleaseEntry releaseEntry1 = ReleaseUtil.getReleaseEntry("FIRST");

		Assert.assertNotNull(releaseEntry1);

		Assert.assertEquals(
			"appServerTomcatVersion value",
			releaseEntry1.getAppServerTomcatVersion());
		Assert.assertEquals(
			"buildTimestamp value", releaseEntry1.getBuildTimestamp());
		Assert.assertEquals(
			"bundleChecksumSHA512 value",
			releaseEntry1.getBundleChecksumSHA512());
		Assert.assertEquals("bundleUrl value", releaseEntry1.getBundleUrl());
		Assert.assertEquals(
			"gitHashLiferayDocker value",
			releaseEntry1.getGitHashLiferayDocker());
		Assert.assertEquals(
			"gitHashLiferayPortalEE value",
			releaseEntry1.getGitHashLiferayPortalEE());
		Assert.assertEquals(
			"liferayDockerImage value", releaseEntry1.getLiferayDockerImage());
		Assert.assertEquals(
			"liferayDockerTags value", releaseEntry1.getLiferayDockerTags());
		Assert.assertEquals(
			"liferayProductVersion value",
			releaseEntry1.getLiferayProductVersion());
		Assert.assertEquals("product value", releaseEntry1.getProduct());
		Assert.assertEquals(
			"productGroupVersion value",
			releaseEntry1.getProductGroupVersion());
		Assert.assertEquals(
			"productVersion value", releaseEntry1.getProductVersion());
		Assert.assertTrue(releaseEntry1.isPromoted());
		Assert.assertEquals(
			"releaseDate value", releaseEntry1.getReleaseDate());
		Assert.assertEquals("FIRST", releaseEntry1.getReleaseKey());
		Assert.assertEquals(
			"targetPlatformVersion value",
			releaseEntry1.getTargetPlatformVersion());
		Assert.assertEquals("url value", releaseEntry1.getUrl());

		ReleaseEntry releaseEntry2 = ReleaseUtil.getReleaseEntry("SECOND");

		Assert.assertNotNull(releaseEntry2);

		Assert.assertEquals(
			"app.server.tomcat.version value",
			releaseEntry2.getAppServerTomcatVersion());
		Assert.assertEquals(
			"build.timestamp value", releaseEntry2.getBuildTimestamp());
		Assert.assertEquals(
			"bundle.checksum.sha512 value",
			releaseEntry2.getBundleChecksumSHA512());
		Assert.assertEquals("bundle.url value", releaseEntry2.getBundleUrl());
		Assert.assertEquals(
			"git.hash.liferay-docker value",
			releaseEntry2.getGitHashLiferayDocker());
		Assert.assertEquals(
			"git.hash.liferay-portal-ee value",
			releaseEntry2.getGitHashLiferayPortalEE());
		Assert.assertEquals(
			"liferay.docker.image value",
			releaseEntry2.getLiferayDockerImage());
		Assert.assertEquals(
			"liferay.docker.tags value", releaseEntry2.getLiferayDockerTags());
		Assert.assertEquals(
			"liferay.product.version value",
			releaseEntry2.getLiferayProductVersion());
		Assert.assertEquals("product value", releaseEntry2.getProduct());
		Assert.assertEquals(
			"productGroupVersion value",
			releaseEntry2.getProductGroupVersion());
		Assert.assertEquals(
			"productVersion value", releaseEntry2.getProductVersion());
		Assert.assertTrue(releaseEntry2.isPromoted());
		Assert.assertEquals(
			"release.date value", releaseEntry2.getReleaseDate());
		Assert.assertEquals("SECOND", releaseEntry2.getReleaseKey());
		Assert.assertEquals(
			"target.platform.version value",
			releaseEntry2.getTargetPlatformVersion());
		Assert.assertEquals(
			"https://releases-cdn.liferay.com/dxp/second",
			releaseEntry2.getUrl());
	}

	private File _createTempDir(String name) throws Exception {
		Path tempDirectoryPath = Files.createTempDirectory(
			"ReleasesUtilTest_" + name);

		File tempDirectoryFile = tempDirectoryPath.toFile();

		tempDirectoryFile.deleteOnExit();

		return tempDirectoryFile;
	}

	private void _writeFile(String resourceFileName, Path destinationPath)
		throws Exception {

		Path parentDirPath = destinationPath.getParent();

		File parentDirFile = parentDirPath.toFile();

		if (!parentDirFile.exists() && !parentDirFile.mkdirs()) {
			throw new Exception("Unable to create directory: " + parentDirPath);
		}

		try (InputStream inputStream =
				ReleaseUtilTest.class.getResourceAsStream(resourceFileName)) {

			if (inputStream == null) {
				throw new Exception(
					"Unable to read resource from class path: " +
						resourceFileName);
			}

			Files.copy(inputStream, destinationPath);
		}
	}

	private File _mirrorDir;
	private File _workspaceCacheDir;

}