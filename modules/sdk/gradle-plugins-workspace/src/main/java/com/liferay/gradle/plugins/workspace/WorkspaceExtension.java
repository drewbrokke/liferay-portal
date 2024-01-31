/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.gradle.plugins.workspace.configurator.ClientExtensionProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ExtProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ModulesProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.PluginsProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.RootProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ThemesProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.WarsProjectConfigurator;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;
import com.liferay.portal.tools.bundle.support.constants.BundleSupportConstants;
import com.liferay.workspace.bundle.url.codec.BundleURLCodec;

import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;

/**
 * @author David Truong
 * @author Andrea Di Giorgi
 * @author Simon Jiang
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class WorkspaceExtension {

	@SuppressWarnings("serial")
	public WorkspaceExtension(Settings settings) {
		_gradle = settings.getGradle();

		_product = _getProperty(settings, "product", (String)null);

		_projectConfigurators.add(
			new ClientExtensionProjectConfigurator(settings));
		_projectConfigurators.add(new ExtProjectConfigurator(settings));
		_projectConfigurators.add(new ModulesProjectConfigurator(settings));
		_projectConfigurators.add(new PluginsProjectConfigurator(settings));
		_projectConfigurators.add(new ThemesProjectConfigurator(settings));
		_projectConfigurators.add(new WarsProjectConfigurator(settings));

		_productInfoMap = _getProductInfoMap(
			Arrays.asList(
				() -> _getProductInfoMap(_PRODUCT_INFO_URL),
				() -> _getProductInfoMap(_CDN_PRODUCT_INFO_URL),
				() -> {
					String resourcePath = "/.product_info.json";

					try (InputStream inputStream =
							WorkspaceExtension.class.getResourceAsStream(
								resourcePath)) {

						Objects.requireNonNull(
							inputStream,
							String.format(
								"No resource found at path %s", resourcePath));

						return _getProductInfoMap(
							new InputStreamReader(inputStream));
					}
					catch (Exception exception) {
						System.out.printf(
							"Could not get local resource: %s%n",
							exception.getMessage());

						return null;
					}
				}));

		_appServerTomcatVersion = GradleUtil.getProperty(
			settings, "app.server.tomcat.version",
			_getDefaultAppServerVersion());
		_bundleCacheDir = _getProperty(
			settings, "bundle.cache.dir", _BUNDLE_CACHE_DIR);
		_bundleChecksumMD5 = _getProperty(
			settings, "bundle.checksum.md5", getDefaultBundleChecksumMD5());
		_bundleDistIncludeMetadata = _getProperty(
			settings, "bundle.dist.include.metadata",
			_BUNDLE_DIST_INCLUDE_METADATA);
		_bundleDistRootDirName = _getProperty(
			settings, "bundle.dist.root.dir", _BUNDLE_DIST_ROOT_DIR_NAME);
		_bundleTokenDownload = _getProperty(
			settings, "bundle.token.download", _BUNDLE_TOKEN_DOWNLOAD);
		_bundleTokenEmailAddress = _getProperty(
			settings, "bundle.token.email.address",
			_BUNDLE_TOKEN_EMAIL_ADDRESS);
		_bundleTokenForce = _getProperty(
			settings, "bundle.token.force", _BUNDLE_TOKEN_FORCE);
		_bundleTokenPassword = _getProperty(
			settings, "bundle.token.password", _BUNDLE_TOKEN_PASSWORD);
		_bundleTokenPasswordFile = _getProperty(
			settings, "bundle.token.password.file",
			_BUNDLE_TOKEN_PASSWORD_FILE);
		_bundleUrl = _getProperty(
			settings, "bundle.url", getDefaultBundleUrl());
		_configsDir = _getProperty(
			settings, "configs.dir",
			BundleSupportConstants.DEFAULT_CONFIGS_DIR_NAME);
		_dirExcludesGlobs = StringUtil.split(
			GradleUtil.toString(_getProperty(settings, "dir.excludes.globs")));
		_dockerDir = _getProperty(settings, "docker.dir", _DOCKER_DIR);
		_dockerImageLiferay = _getProperty(
			settings, "docker.image.liferay", _getDefaultDockerImage());
		_dockerLocalRegistryAddress = _getProperty(
			settings, "docker.local.registry.address");
		_dockerPullPolicy = _getProperty(
			settings, "docker.pull.policy", _DOCKER_PULL_POLICY);
		_dockerUserAccessToken = _getProperty(
			settings, "docker.user.access.token");
		_dockerUserName = _getProperty(settings, "docker.username");
		_environment = _getProperty(
			settings, "environment",
			BundleSupportConstants.DEFAULT_ENVIRONMENT);
		_homeDir = _getProperty(
			settings, "home.dir",
			BundleSupportConstants.DEFAULT_LIFERAY_HOME_DIR_NAME);
		_nodePackageManager = _getProperty(
			settings, "node.package.manager", _NODE_PACKAGE_MANAGER);
		_targetPlatformVersion = _getProperty(
			settings, "target.platform.version",
			_getDefaultTargetplatformVersion());

		_gradle.projectsEvaluated(
			new Closure<Void>(_gradle) {

				@SuppressWarnings("unused")
				public void doCall() {
					Project rootProject = _gradle.getRootProject();

					Logger logger = rootProject.getLogger();

					if (!logger.isLifecycleEnabled()) {
						return;
					}

					if (_product == null) {
						logger.lifecycle(
							"The property `liferay.workspace.product` has " +
								"not been set. It is recommended to set this " +
									"property in gradle.properties in the " +
										"workspace directory. See LPS-111700.");

						return;
					}

					String overridePropertyInfo =
						"The %s property is currently overriding the default " +
							"value managed by the liferay.workspace.product " +
								"setting.";

					if (!Objects.equals(
							getAppServerTomcatVersion(),
							_getDefaultAppServerVersion())) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"app.server.tomcat.version"));
					}

					if (!Objects.equals(
							getBundleChecksumMD5(),
							getDefaultBundleChecksumMD5())) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.bundle.checksum.md5"));
					}

					if (!Objects.equals(
							getBundleUrl(), getDefaultBundleUrl())) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.bundle.url"));
					}

					if (!Objects.equals(
							getDockerImageLiferay(),
							_getDefaultDockerImage())) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.docker.image.liferay"));
					}

					if (!Objects.equals(
							getTargetPlatformVersion(),
							_getDefaultTargetplatformVersion())) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.target.platform.version"));
					}
				}

			});

		_rootProjectConfigurator = new RootProjectConfigurator(settings);
	}

	public String getAppServerTomcatVersion() {
		if (Objects.isNull(_appServerTomcatVersion)) {
			return _getDefaultAppServerVersion();
		}

		return GradleUtil.toString(_appServerTomcatVersion);
	}

	public File getBundleCacheDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _bundleCacheDir);
	}

	public String getBundleChecksumMD5() {
		if (Objects.isNull(_bundleChecksumMD5)) {
			return getDefaultBundleChecksumMD5();
		}

		return GradleUtil.toString(_bundleChecksumMD5);
	}

	public String getBundleDistRootDirName() {
		return GradleUtil.toString(_bundleDistRootDirName);
	}

	public String getBundleTokenEmailAddress() {
		return GradleUtil.toString(_bundleTokenEmailAddress);
	}

	public String getBundleTokenPassword() {
		return GradleUtil.toString(_bundleTokenPassword);
	}

	public File getBundleTokenPasswordFile() {
		return GradleUtil.toFile(
			_gradle.getRootProject(), _bundleTokenPasswordFile);
	}

	public String getBundleUrl() {
		if (Objects.isNull(_bundleUrl)) {
			return getDefaultBundleUrl();
		}

		return GradleUtil.toString(_bundleUrl);
	}

	public File getConfigsDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _configsDir);
	}

	public String getDefaultBundleChecksumMD5() {
		return Optional.ofNullable(
			_getProductInfo(getProduct())
		).map(
			ProductInfo::getBundleChecksumMD5
		).orElse(
			null
		);
	}

	public String getDefaultBundleUrl() {
		return Optional.ofNullable(
			_getProductInfo(getProduct())
		).map(
			this::_decodeBundleUrl
		).orElse(
			null
		);
	}

	public List<String> getDirExcludesGlobs() {
		return GradleUtil.toStringList(_dirExcludesGlobs);
	}

	public String getDockerContainerId() {
		return GradleUtil.toString(_dockerContainerId);
	}

	public File getDockerDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _dockerDir);
	}

	public String getDockerImageId() {
		return GradleUtil.toString(_dockerImageId);
	}

	public String getDockerImageLiferay() {
		if (Objects.isNull(_dockerImageLiferay)) {
			return _getDefaultDockerImage();
		}

		return GradleUtil.toString(_dockerImageLiferay);
	}

	public String getDockerLocalRegistryAddress() {
		return GradleUtil.toString(_dockerLocalRegistryAddress);
	}

	public boolean getDockerPullPolicy() {
		return GradleUtil.toBoolean(_dockerPullPolicy);
	}

	public String getDockerUserAccessToken() {
		return GradleUtil.toString(_dockerUserAccessToken);
	}

	public String getDockerUserName() {
		return GradleUtil.toString(_dockerUserName);
	}

	public String getEnvironment() {
		return GradleUtil.toString(_environment);
	}

	public File getHomeDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _homeDir);
	}

	public String getNodePackageManager() {
		return GradleUtil.toString(_nodePackageManager);
	}

	public String getProduct() {
		return GradleUtil.toString(_product);
	}

	public ProductInfo getProductInfo() {
		return _getProductInfo(getProduct());
	}

	public Iterable<ProjectConfigurator> getProjectConfigurators() {
		return Collections.unmodifiableSet(_projectConfigurators);
	}

	public Plugin<Project> getRootProjectConfigurator() {
		return _rootProjectConfigurator;
	}

	public String getTargetPlatformVersion() {
		if (Objects.isNull(_targetPlatformVersion)) {
			return _getDefaultTargetplatformVersion();
		}

		return GradleUtil.toString(_targetPlatformVersion);
	}

	public boolean isBundleDistIncludeMetadata() {
		return GradleUtil.toBoolean(_bundleDistIncludeMetadata);
	}

	public boolean isBundleTokenDownload() {
		return GradleUtil.toBoolean(_bundleTokenDownload);
	}

	public boolean isBundleTokenForce() {
		return GradleUtil.toBoolean(_bundleTokenForce);
	}

	public ProjectConfigurator propertyMissing(String name) {
		for (ProjectConfigurator projectConfigurator : _projectConfigurators) {
			if (name.equals(projectConfigurator.getName())) {
				return projectConfigurator;
			}
		}

		throw new MissingPropertyException(name, ProjectConfigurator.class);
	}

	public void setBundleCacheDir(Object bundleCacheDir) {
		_bundleCacheDir = bundleCacheDir;
	}

	public void setBundleChecksumMD5(Object bundleChecksumMD5) {
		_bundleChecksumMD5 = bundleChecksumMD5;
	}

	public void setBundleDistIncludeMetadata(Object bundleDistIncludeMetadata) {
		_bundleDistIncludeMetadata = bundleDistIncludeMetadata;
	}

	public void setBundleDistRootDirName(Object bundleDistRootDirName) {
		_bundleDistRootDirName = bundleDistRootDirName;
	}

	public void setBundleTokenDownload(Object bundleTokenDownload) {
		_bundleTokenDownload = bundleTokenDownload;
	}

	public void setBundleTokenEmailAddress(Object bundleTokenEmailAddress) {
		_bundleTokenEmailAddress = bundleTokenEmailAddress;
	}

	public void setBundleTokenForce(Object bundleTokenForce) {
		_bundleTokenForce = bundleTokenForce;
	}

	public void setBundleTokenPassword(Object bundleTokenPassword) {
		_bundleTokenPassword = bundleTokenPassword;
	}

	public void setBundleTokenPasswordFile(Object bundleTokenPasswordFile) {
		_bundleTokenPasswordFile = bundleTokenPasswordFile;
	}

	public void setBundleUrl(Object bundleUrl) {
		_bundleUrl = bundleUrl;
	}

	public void setConfigsDir(Object configsDir) {
		_configsDir = configsDir;
	}

	public void setDirExcludesGlobs(Iterable<String> dirExcludesGlobs) {
		_dirExcludesGlobs = dirExcludesGlobs;
	}

	public void setDockerContainerId(Object dockerContainerId) {
		_dockerContainerId = dockerContainerId;
	}

	public void setDockerDir(Object dockerDir) {
		_dockerDir = dockerDir;
	}

	public void setDockerImageId(Object dockerImageId) {
		_dockerImageId = dockerImageId;
	}

	public void setDockerImageLiferay(Object dockerImageLiferay) {
		_dockerImageLiferay = dockerImageLiferay;
	}

	public void setDockerLocalRegistryAddress(
		Object dockerLocalRegistryAddress) {

		_dockerLocalRegistryAddress = dockerLocalRegistryAddress;
	}

	public void setDockerPullPolicy(Object dockerPullPolicy) {
		_dockerPullPolicy = dockerPullPolicy;
	}

	public void setDockerUserAccessToken(Object dockerUserAccessToken) {
		_dockerUserAccessToken = dockerUserAccessToken;
	}

	public void setDockerUserName(Object dockerUserName) {
		_dockerUserName = dockerUserName;
	}

	public void setEnvironment(Object environment) {
		_environment = environment;
	}

	public void setHomeDir(Object homeDir) {
		_homeDir = homeDir;
	}

	public void setNodePackageManager(Object nodePackageManager) {
		_nodePackageManager = nodePackageManager;
	}

	public void setProduct(Object product) {
		_product = product;
	}

	public void setTargetPlatformVersion(Object targetPlatformVersion) {
		_targetPlatformVersion = targetPlatformVersion;
	}

	public class ProductInfo {

		public String getAppServerTomcatVersion() {
			return _appServerTomcatVersion;
		}

		public String getBundleChecksumMD5() {
			return _bundleChecksumMD5;
		}

		public String getBundleUrl() {
			return _bundleUrl;
		}

		public String getLiferayDockerImage() {
			return _liferayDockerImage;
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

		@SerializedName("appServerTomcatVersion")
		private String _appServerTomcatVersion;

		@SerializedName("bundleChecksumMD5")
		private String _bundleChecksumMD5;

		@SerializedName("bundleUrl")
		private String _bundleUrl;

		@SerializedName("liferayDockerImage")
		private String _liferayDockerImage;

		@SerializedName("liferayProductVersion")
		private String _liferayProductVersion;

		@SerializedName("releaseDate")
		private String _releaseDate;

		@SerializedName("targetPlatformVersion")
		private String _targetPlatformVersion;

	}

	private String _decodeBundleUrl(ProductInfo productInfo) {
		try {
			return BundleURLCodec.decode(
				productInfo.getBundleUrl(), productInfo.getReleaseDate());
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to determine bundle URL", exception);
		}
	}

	private String _getDefaultAppServerVersion() {
		return Optional.ofNullable(
			_getProductInfo(getProduct())
		).map(
			ProductInfo::getAppServerTomcatVersion
		).orElse(
			null
		);
	}

	private String _getDefaultDockerImage() {
		return Optional.ofNullable(
			_getProductInfo(getProduct())
		).map(
			ProductInfo::getLiferayDockerImage
		).orElse(
			null
		);
	}

	private String _getDefaultTargetplatformVersion() {
		return Optional.ofNullable(
			_getProductInfo(getProduct())
		).map(
			ProductInfo::getTargetPlatformVersion
		).orElse(
			null
		);
	}

	private ProductInfo _getProductInfo(String product) {
		if (product == null) {
			return null;
		}

		return _productInfoMap.get(product);
	}

	private Map<String, ProductInfo> _getProductInfoMap(
			List<Supplier<Map<String, ProductInfo>>> suppliers)
		throws GradleException {

		for (Supplier<Map<String, ProductInfo>> supplier : suppliers) {
			Map<String, ProductInfo> productInfoMap = supplier.get();

			if (productInfoMap != null) {
				return productInfoMap;
			}
		}

		throw new GradleException("Could not get product info");
	}

	private Map<String, ProductInfo> _getProductInfoMap(Reader reader)
		throws IOException {

		try (JsonReader jsonReader = new JsonReader(reader)) {
			Gson gson = new Gson();

			TypeToken<Map<String, ProductInfo>> typeToken =
				new TypeToken<Map<String, ProductInfo>>() {
				};

			return gson.fromJson(jsonReader, typeToken.getType());
		}
	}

	private Map<String, ProductInfo> _getProductInfoMap(String url) {
		DownloadCommand downloadCommand = new DownloadCommand();

		downloadCommand.setCacheDir(_workspaceCacheDir);
		downloadCommand.setConnectionTimeout(5 * 1000);
		downloadCommand.setPassword(null);
		downloadCommand.setQuiet(true);
		downloadCommand.setToken(false);
		downloadCommand.setUserName(null);

		try {
			downloadCommand.setUrl(new URL(url));

			downloadCommand.execute();

			return _getProductInfoMap(
				Files.newBufferedReader(downloadCommand.getDownloadPath()));
		}
		catch (Exception exception) {
			System.out.printf(
				"Could not get resource from url %s: %s%n", url,
				exception.getMessage());

			return null;
		}
	}

	private Object _getProperty(Object object, String keySuffix) {
		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix);
	}

	private boolean _getProperty(
		Object object, String keySuffix, boolean defaultValue) {

		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix, defaultValue);
	}

	private Object _getProperty(
		Object object, String keySuffix, File defaultValue) {

		Object value = GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix);

		if ((value instanceof String) && Validator.isNull((String)value)) {
			value = null;
		}

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	private String _getProperty(
		Object object, String keySuffix, String defaultValue) {

		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix, defaultValue);
	}

	private static final File _BUNDLE_CACHE_DIR = new File(
		System.getProperty("user.home"),
		BundleSupportConstants.DEFAULT_BUNDLE_CACHE_DIR_NAME);

	private static final boolean _BUNDLE_DIST_INCLUDE_METADATA = false;

	private static final String _BUNDLE_DIST_ROOT_DIR_NAME = null;

	private static final boolean _BUNDLE_TOKEN_DOWNLOAD = false;

	private static final String _BUNDLE_TOKEN_EMAIL_ADDRESS = null;

	private static final boolean _BUNDLE_TOKEN_FORCE = false;

	private static final String _BUNDLE_TOKEN_PASSWORD = null;

	private static final String _BUNDLE_TOKEN_PASSWORD_FILE = null;

	private static final String _CDN_PRODUCT_INFO_URL =
		"https://releases-cdn.liferay.com/tools/workspace/.product_info.json";

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME =
		".liferay/workspace";

	private static final File _DOCKER_DIR = new File(
		Project.DEFAULT_BUILD_DIR_NAME + File.separator + "docker");

	private static final boolean _DOCKER_PULL_POLICY = true;

	private static final String _NODE_PACKAGE_MANAGER = "yarn";

	private static final String _PRODUCT_INFO_URL =
		"https://releases.liferay.com/tools/workspace/.product_info.json";

	private final Object _appServerTomcatVersion;
	private Object _bundleCacheDir;
	private Object _bundleChecksumMD5;
	private Object _bundleDistIncludeMetadata;
	private Object _bundleDistRootDirName;
	private Object _bundleTokenDownload;
	private Object _bundleTokenEmailAddress;
	private Object _bundleTokenForce;
	private Object _bundleTokenPassword;
	private Object _bundleTokenPasswordFile;
	private Object _bundleUrl;
	private Object _configsDir;
	private Iterable<String> _dirExcludesGlobs;
	private Object _dockerContainerId;
	private Object _dockerDir;
	private Object _dockerImageId;
	private Object _dockerImageLiferay;
	private Object _dockerLocalRegistryAddress;
	private Object _dockerPullPolicy;
	private Object _dockerUserAccessToken;
	private Object _dockerUserName;
	private Object _environment;
	private final Gradle _gradle;
	private Object _homeDir;
	private Object _nodePackageManager;
	private Object _product;
	private final Map<String, ProductInfo> _productInfoMap;
	private final Set<ProjectConfigurator> _projectConfigurators =
		new LinkedHashSet<>();
	private final Plugin<Project> _rootProjectConfigurator;
	private Object _targetPlatformVersion;
	private final File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);

}