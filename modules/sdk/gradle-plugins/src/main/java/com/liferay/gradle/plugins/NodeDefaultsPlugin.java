/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.NodeExtension;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.task.ExecutePackageManagerTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.PublishNodeModuleTask;
import com.liferay.gradle.util.Validator;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

import org.osgi.framework.Version;

import org.parboiled.common.StringUtils;

/**
 * @author Andrea Di Giorgi
 */
public class NodeDefaultsPlugin extends BaseDefaultsPlugin<NodePlugin> {

	public static class NodeInfo {

		public String getDate() {
			return _date;
		}

		public String getNpmVersion() {
			return _npm;
		}

		public String getVersion() {
			return _version;
		}

		public String isLtsVersion() {
			return _lts;
		}

		@SerializedName("date")
		private String _date;

		@SerializedName("lts")
		private String _lts;

		@SerializedName("npm")
		private String _npm;

		@SerializedName("version")
		private String _version;

	}

	@Override
	protected void applyPluginDefaults(Project project, NodePlugin nodePlugin) {

		// Extensions

		ExtensionContainer extensionContainer = project.getExtensions();

		NodeExtension nodeExtension = extensionContainer.getByType(
			NodeExtension.class);

		Optional<NodeInfo> nodeInfoOptional = _getNodeVersionInfo();

		if (nodeInfoOptional.isPresent()) {
			NodeInfo nodeInfo = nodeInfoOptional.get();

			_nodeVersion = StringUtils.substring(nodeInfo.getVersion(), 1);
			_npmVersion = nodeInfo.getNpmVersion();
		}

		_configureExtensionNode(project, nodeExtension);

		// Containers

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecutePackageManagerTask.class,
			new Action<ExecutePackageManagerTask>() {

				@Override
				public void execute(
					ExecutePackageManagerTask executePackageManagerTask) {

					_configureTaskExecutePackageManager(
						executePackageManagerTask);
				}

			});

		taskContainer.withType(
			NpmInstallTask.class,
			new Action<NpmInstallTask>() {

				@Override
				public void execute(NpmInstallTask npmInstallTask) {
					_configureTaskNpmInstall(npmInstallTask);
				}

			});

		taskContainer.withType(
			PublishNodeModuleTask.class,
			new Action<PublishNodeModuleTask>() {

				@Override
				public void execute(
					PublishNodeModuleTask publishNodeModuleTask) {

					_configureTaskPublishNodeModule(publishNodeModuleTask);
				}

			});
	}

	@Override
	protected Class<NodePlugin> getPluginClass() {
		return NodePlugin.class;
	}

	private void _configureExtensionNode(
		Project project, NodeExtension nodeExtension) {

		nodeExtension.setGlobal(true);
		nodeExtension.setNodeVersion(_nodeVersion);
		nodeExtension.setNpmVersion(_npmVersion);

		String npmArgs = GradleUtil.getProperty(
			project, "nodejs.npm.args", (String)null);

		if (Validator.isNotNull(npmArgs)) {
			nodeExtension.npmArgs((Object[])npmArgs.split("\\s+"));
		}
	}

	private void _configureTaskExecutePackageManager(
		ExecutePackageManagerTask executePackageManagerTask) {

		String nodeEnv = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.node.env",
			(String)null);

		if (Validator.isNotNull(nodeEnv)) {
			executePackageManagerTask.environment("NODE_ENV", nodeEnv);
		}

		String registry = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.npm.registry",
			(String)null);

		if (Validator.isNotNull(registry)) {
			executePackageManagerTask.setRegistry(registry);
		}
	}

	private void _configureTaskNpmInstall(NpmInstallTask npmInstallTask) {
		String sassBinarySite = GradleUtil.getProperty(
			npmInstallTask.getProject(), "nodejs.npm.sass.binary.site",
			(String)null);

		if (Validator.isNotNull(sassBinarySite)) {
			boolean sassBinarySiteArg = false;

			for (Object object : npmInstallTask.getArgs()) {
				String arg = GradleUtil.toString(object);

				if (arg.startsWith(_SASS_BINARY_SITE_ARG)) {
					sassBinarySiteArg = true;
				}
			}

			if (!sassBinarySiteArg) {
				npmInstallTask.args(_SASS_BINARY_SITE_ARG + sassBinarySite);
			}
		}
	}

	private void _configureTaskPublishNodeModule(
		PublishNodeModuleTask publishNodeModuleTask) {

		Project project = publishNodeModuleTask.getProject();

		String moduleAuthor = GradleUtil.getProperty(
			project, "nodejs.npm.module.author", (String)null);

		if (Validator.isNotNull(moduleAuthor)) {
			publishNodeModuleTask.setModuleAuthor(moduleAuthor);
		}

		String moduleBugsUrl = GradleUtil.getProperty(
			project, "nodejs.npm.module.bugs.url", (String)null);

		if (Validator.isNotNull(moduleBugsUrl)) {
			publishNodeModuleTask.setModuleBugsUrl(moduleBugsUrl);
		}

		String moduleLicense = GradleUtil.getProperty(
			project, "nodejs.npm.module.license", (String)null);

		if (Validator.isNotNull(moduleLicense)) {
			publishNodeModuleTask.setModuleLicense(moduleLicense);
		}

		String moduleRepository = GradleUtil.getProperty(
			project, "nodejs.npm.module.repository", (String)null);

		if (Validator.isNotNull(moduleRepository)) {
			publishNodeModuleTask.setModuleRepository(moduleRepository);
		}

		String npmAccessToken = GradleUtil.getProperty(
			project, "nodejs.npm.access.token", (String)null);

		if (Validator.isNotNull(npmAccessToken)) {
			publishNodeModuleTask.setNpmAccessToken(npmAccessToken);
		}
	}

	private Optional<NodeInfo> _getNodeInfos(Path downloadPath)
		throws Exception {

		try (JsonReader jsonReader = new JsonReader(
				Files.newBufferedReader(downloadPath))) {

			List<NodeInfo> nodeInfos = _parserNodeInfos(jsonReader);

			return nodeInfos.stream(
			).filter(
				nodeInfo -> !Objects.equals(nodeInfo.isLtsVersion(), "false")
			).min(
				(first, second) -> {
					Version firstVersion = Version.parseVersion(
						StringUtils.substring(first.getVersion(), 1));
					Version secondVersion = Version.parseVersion(
						StringUtils.substring(second.getVersion(), 1));

					return -1 * firstVersion.compareTo(secondVersion);
				}
			);
		}
	}

	private Optional<NodeInfo> _getNodeVersionInfo() {
		DownloadCommand downloadCommand = new DownloadCommand();

		downloadCommand.setCacheDir(_nodeCacheDir);
		downloadCommand.setConnectionTimeout(5 * 1000);
		downloadCommand.setPassword(null);
		downloadCommand.setQuiet(true);
		downloadCommand.setToken(false);
		downloadCommand.setUserName(null);

		try {
			downloadCommand.setUrl(new URL(_PRODUCT_NODE_URL));

			downloadCommand.execute();

			return _getNodeInfos(downloadCommand.getDownloadPath());
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to get node version", exception.getCause());
		}
	}

	private List<NodeInfo> _parserNodeInfos(JsonReader jsonReader) {
		Gson gson = new Gson();

		TypeToken<List<NodeInfo>> typeToken = new TypeToken<List<NodeInfo>>() {
		};

		return gson.fromJson(jsonReader, typeToken.getType());
	}

	private static final String _DEFAULT_NODE_CACHE_DIR_NAME = ".liferay/node";

	private static final String _PRODUCT_NODE_URL =
		"https://nodejs.org/dist/index.json";

	private static final String _SASS_BINARY_SITE_ARG = "--sass-binary-site=";

	private static String _nodeVersion = "16.13.0";
	private static String _npmVersion = "8.1.0";

	private final File _nodeCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_NODE_CACHE_DIR_NAME);

}