/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.gradle.api.GradleException;

/**
 * @author Drew Brokke
 */
public class ReleaseUtil {

	public static final ReleaseUtil INSTANCE = new ReleaseUtil();

	public static <T> T getFromReleaseProperties(
		String releaseKey, Function<ReleaseProperties, T> function) {

		return function.apply(getReleaseProperties(releaseKey));
	}

	public static ReleaseProperties getReleaseProperties(String releaseKey) {
		if (releaseKey == null) {
			return _EMPTY_RELEASE_PROPERTIES;
		}

		return INSTANCE._releasePropertiesMap.computeIfAbsent(
			releaseKey, INSTANCE::_createReleaseProperties);
	}

	public static class ReleaseProperties {

		public String getAppServerTomcatVersion() {
			return _appServerTomcatVersion;
		}

		public String getBuildTimestamp() {
			return _buildTimestamp;
		}

		public String getBundleChecksumSHA512() {
			return _bundleChecksumSHA512;
		}

		public String getBundleUrl() {
			return _bundleUrl;
		}

		public String getGitHashLiferayDocker() {
			return _gitHashLiferayDocker;
		}

		public String getGitHashLiferayPortalEE() {
			return _gitHashLiferayPortalEE;
		}

		public String getLiferayDockerImage() {
			return _liferayDockerImage;
		}

		public String getLiferayDockerTags() {
			return _liferayDockerTags;
		}

		public String getLiferayProductVersion() {
			return _liferayProductVersion;
		}

		public String getReleaseDate() {
			return _releaseDate;
		}

		public String getTargetPlatformVersion() {
			return _targetPlatformVersion;
		}

		private ReleaseProperties() {
			this(new Properties());
		}

		private ReleaseProperties(Properties properties) {
			this(
				properties.getProperty("app.server.tomcat.version"),
				properties.getProperty("build.timestamp"),
				properties.getProperty("bundle.checksum.sha512"),
				properties.getProperty("bundle.url"),
				properties.getProperty("git.hash.liferay-docker"),
				properties.getProperty("git.hash.liferay-portal-ee"),
				properties.getProperty("liferay.docker.image"),
				properties.getProperty("liferay.docker.tags"),
				properties.getProperty("liferay.product.version"),
				properties.getProperty("release.date"),
				properties.getProperty("target.platform.version"));
		}

		private ReleaseProperties(
			String appServerTomcatVersion, String buildTimestamp,
			String bundleChecksumSHA512, String bundleUrl,
			String gitHashLiferayDocker, String gitHashLiferayPortalEE,
			String liferayDockerImage, String liferayDockerTags,
			String liferayProductVersion, String releaseDate,
			String targetPlatformVersion) {

			_appServerTomcatVersion = appServerTomcatVersion;
			_buildTimestamp = buildTimestamp;
			_bundleChecksumSHA512 = bundleChecksumSHA512;
			_bundleUrl = bundleUrl;
			_gitHashLiferayDocker = gitHashLiferayDocker;
			_gitHashLiferayPortalEE = gitHashLiferayPortalEE;
			_liferayDockerImage = liferayDockerImage;
			_liferayDockerTags = liferayDockerTags;
			_liferayProductVersion = liferayProductVersion;
			_releaseDate = releaseDate;
			_targetPlatformVersion = targetPlatformVersion;
		}

		private final String _appServerTomcatVersion;
		private final String _buildTimestamp;
		private final String _bundleChecksumSHA512;
		private final String _bundleUrl;
		private final String _gitHashLiferayDocker;
		private final String _gitHashLiferayPortalEE;
		private final String _liferayDockerImage;
		private final String _liferayDockerTags;
		private final String _liferayProductVersion;
		private final String _releaseDate;
		private final String _targetPlatformVersion;

	}

	private ReleaseUtil() {
		int maxAge = 30;

		String refreshLiferayReleases = System.getProperty(
			"liferay.workspace.refresh.liferay.releases");

		if (refreshLiferayReleases != null) {
			maxAge = 0;
		}

		File releasesJsonFile = new File(_workspaceCacheDir, "releases.json");

		ReleaseEntries releaseEntries = ResourceUtil.readJson(
			ReleaseEntries.class,
			ResourceUtil.getLocalFileResolver(
				releasesJsonFile, maxAge, ChronoUnit.DAYS),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir,
				"https://releases.liferay.com/releases.json"),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir,
				"https://releases-cdn.liferay.com/releases.json"),
			ResourceUtil.getLocalFileResolver(releasesJsonFile),
			ResourceUtil.getClassLoaderResolver("/releases.json"));

		for (ReleaseEntry releaseEntry : releaseEntries) {
			_releaseEntryMap.put(releaseEntry.getReleaseKey(), releaseEntry);
		}
	}

	private ReleaseProperties _createReleaseProperties(String releaseKey) {
		ReleaseEntry releaseEntry = _releaseEntryMap.get(releaseKey);

		if (releaseEntry == null) {
			throw new GradleException(
				String.format(
					"%s is not a valid product key. Must be one of %s",
					releaseKey, _releaseEntryMap.keySet()));
		}

		String product = releaseEntry.getProduct();

		File productReleasePropertiesCacheDir = new File(
			new File(_workspaceCacheDir, "releaseProperties"),
			String.format("%s/%s", product, releaseKey));

		String releasesCDNUrl = releaseEntry.getUrl() + "/release.properties";

		String releasesUrl = releasesCDNUrl.replaceFirst(
			"releases-cdn", "releases");

		Properties properties = ResourceUtil.readProperties(
			ResourceUtil.getLocalFileResolver(
				new File(
					productReleasePropertiesCacheDir, "release.properties")),
			ResourceUtil.getURLResolver(
				productReleasePropertiesCacheDir, releasesCDNUrl),
			ResourceUtil.getURLResolver(
				productReleasePropertiesCacheDir, releasesUrl));

		return new ReleaseProperties(properties);
	}

	private static final ReleaseProperties _EMPTY_RELEASE_PROPERTIES =
		new ReleaseProperties();

	private final Map<String, ReleaseEntry> _releaseEntryMap = new HashMap<>();
	private final Map<String, ReleaseProperties> _releasePropertiesMap = new HashMap<>();
	private final File _workspaceCacheDir = new File(
		System.getProperty("user.home"), ".liferay/workspace");

	private static class ReleaseEntries extends ArrayList<ReleaseEntry> {
	}

	private static class ReleaseEntry {

		public String getProduct() {
			return _product;
		}

		public String getProductGroupVersion() {
			return _productGroupVersion;
		}

		public String getProductVersion() {
			return _productVersion;
		}

		public String getReleaseKey() {
			return _releaseKey;
		}

		public String getUrl() {
			return _url;
		}

		public boolean isPromoted() {
			return _promoted;
		}

		@JsonProperty("product")
		private String _product;

		@JsonProperty("productGroupVersion")
		private String _productGroupVersion;

		@JsonProperty("productVersion")
		private String _productVersion;

		@JsonProperty("promoted")
		private boolean _promoted;

		@JsonProperty("releaseKey")
		private String _releaseKey;

		@JsonProperty("url")
		private String _url;

	}

}