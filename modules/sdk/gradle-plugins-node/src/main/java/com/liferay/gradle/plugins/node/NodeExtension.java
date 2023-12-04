/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.gradle.plugins.node.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.internal.util.NodePluginUtil;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.Validator;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import org.osgi.framework.Version;

/**
 * @author Andrea Di Giorgi
 */
public class NodeExtension {

	public NodeExtension(final Project project) {
		_download = GradleUtil.getProperty(project, "nodeDownload", true);

		_nodeDir = new Callable<File>() {

			@Override
			public File call() throws Exception {
				if (!isDownload()) {
					return null;
				}

				Project curProject = project;

				if (isGlobal()) {
					curProject = curProject.getRootProject();
				}

				return new File(curProject.getBuildDir(), "node");
			}

		};

		_nodeUrl = new Callable<String>() {

			@Override
			public String call() throws Exception {
				String nodeVersion = getNodeVersion();

				if (Validator.isNull(nodeVersion)) {
					return null;
				}

				StringBuilder sb = new StringBuilder();

				sb.append("http://nodejs.org/dist/v");
				sb.append(nodeVersion);

				if (OSDetector.isWindows() &&
					_npmVersions.containsKey(nodeVersion)) {

					sb.append("/win-x");

					String bitmode = OSDetector.getBitmode();

					if (bitmode.equals("32")) {
						bitmode = "86";
					}

					sb.append(bitmode);
					sb.append("/node.exe");
				}
				else {
					sb.append("/node-v");
					sb.append(nodeVersion);
					sb.append('-');

					String os = "linux";

					if (OSDetector.isApple()) {
						os = "darwin";
					}
					else if (OSDetector.isWindows()) {
						os = "win";
					}

					sb.append(os);

					String processorArchitecture = "-x";

					int nodeMajorVersion = NodePluginUtil.getNodeMajorVersion(
						nodeVersion);

					if (OSDetector.isAppleARM() && (nodeMajorVersion > 15)) {
						processorArchitecture = "-arm";
					}

					sb.append(processorArchitecture);

					String bitmode = OSDetector.getBitmode();

					if (bitmode.equals("32")) {
						bitmode = "86";
					}

					sb.append(bitmode);

					if (OSDetector.isWindows()) {
						sb.append(".zip");
					}
					else {
						sb.append(".tar.gz");
					}
				}

				return sb.toString();
			}

		};

		_npmUrl = new Callable<String>() {

			@Override
			public String call() throws Exception {
				String npmVersion = getNpmVersion();

				if (OSDetector.isWindows() && Validator.isNull(npmVersion)) {
					String nodeVersion = getNodeVersion();

					if (_npmVersions.containsKey(nodeVersion)) {
						npmVersion = _npmVersions.get(nodeVersion);
					}
				}

				if (Validator.isNull(npmVersion)) {
					return null;
				}

				return "https://registry.npmjs.org/npm/-/npm-" + npmVersion +
					".tgz";
			}

		};

		_project = project;

		_scriptFile = new Callable<File>() {

			@Override
			public File call() throws Exception {
				File nodeDir = getNodeDir();

				if (nodeDir == null) {
					return null;
				}

				if (isUseNpm()) {
					return new File(
						NodePluginUtil.getNpmDir(nodeDir), "bin/npm-cli.js");
				}

				return new File(
					NodePluginUtil.getYarnDir(nodeDir),
					"yarn-" + getYarnVersion() + ".js");
			}

		};

		_yarnUrl = new Callable<String>() {

			@Override
			public String call() throws Exception {
				String yarnVersion = getYarnVersion();

				if (Validator.isNull(yarnVersion)) {
					return null;
				}

				StringBuilder sb = new StringBuilder();

				sb.append(
					"https://github.com/yarnpkg/yarn/releases/download/v");
				sb.append(yarnVersion);
				sb.append("/yarn-");
				sb.append(yarnVersion);
				sb.append(".js");

				return sb.toString();
			}

		};
	}

	public File getNodeDir() {
		return GradleUtil.toFile(_project, _nodeDir);
	}

	public String getNodeUrl() {
		return GradleUtil.toString(_nodeUrl);
	}

	public String getNodeVersion() {
		if (isUseLatestNode()) {
			Optional<NodeInfo> nodeInfoOptional = _getNodeVersionInfo();

			if (nodeInfoOptional.isPresent()) {
				NodeInfo nodeInfo = nodeInfoOptional.get();

				return nodeInfo.getVersion(
				).substring(
					1
				);
			}
		}

		return GradleUtil.toString(_nodeVersion);
	}

	public List<String> getNpmArgs() {
		return GradleUtil.toStringList(_npmArgs);
	}

	public String getNpmUrl() {
		return GradleUtil.toString(_npmUrl);
	}

	public String getNpmVersion() {
		if (isUseLatestNode()) {
			Optional<NodeInfo> nodeInfoOptional = _getNodeVersionInfo();

			if (nodeInfoOptional.isPresent()) {
				NodeInfo nodeInfo = nodeInfoOptional.get();

				return nodeInfo.getNpmVersion();
			}
		}

		return GradleUtil.toString(_npmVersion);
	}

	public File getScriptFile() {
		return GradleUtil.toFile(_project, _scriptFile);
	}

	public String getYarnUrl() {
		return GradleUtil.toString(_yarnUrl);
	}

	public String getYarnVersion() {
		return GradleUtil.toString(_yarnVersion);
	}

	public boolean isDownload() {
		return _download;
	}

	public boolean isGlobal() {
		return _global;
	}

	public boolean isUseLatestNode() {
		return _useLatestNode;
	}

	public boolean isUseNpm() {
		return GradleUtil.toBoolean(_useNpm);
	}

	public NodeExtension npmArgs(Iterable<?> npmArgs) {
		GUtil.addToCollection(_npmArgs, npmArgs);

		return this;
	}

	public NodeExtension npmArgs(Object... npmArgs) {
		return npmArgs(Arrays.asList(npmArgs));
	}

	public void setDownload(boolean download) {
		_download = download;
	}

	public void setGlobal(boolean global) {
		_global = global;
	}

	public void setNodeDir(Object nodeDir) {
		_nodeDir = nodeDir;
	}

	public void setNodeUrl(Object nodeUrl) {
		_nodeUrl = nodeUrl;
	}

	public void setNodeVersion(Object nodeVersion) {
		_nodeVersion = nodeVersion;
	}

	public void setNpmArgs(Iterable<?> npmArgs) {
		_npmArgs.clear();

		npmArgs(npmArgs);
	}

	public void setNpmArgs(Object... npmArgs) {
		setNpmArgs(Arrays.asList(npmArgs));
	}

	public void setNpmUrl(Object npmUrl) {
		_npmUrl = npmUrl;
	}

	public void setNpmVersion(Object npmVersion) {
		_npmVersion = npmVersion;
	}

	public void setScriptFile(Object scriptFile) {
		_scriptFile = scriptFile;
	}

	public void setUseLatestNode(boolean useLatestNode) {
		_useLatestNode = useLatestNode;
	}

	public void setUseNpm(Object useNpm) {
		_useNpm = useNpm;
	}

	public void setYarnUrl(Object yarnUrl) {
		_yarnUrl = yarnUrl;
	}

	public void setYarnVersion(Object yarnVersion) {
		_yarnVersion = yarnVersion;
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
						first.getVersion(
						).substring(
							1
						));
					Version secondVersion = Version.parseVersion(
						second.getVersion(
						).substring(
							1
						));

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

	private static final Map<String, String> _npmVersions =
		new HashMap<String, String>() {
			{
				put("5.5.0", "3.3.12");
				put("5.6.0", "3.6.0");
				put("5.7.0", "3.6.0");
				put("5.7.1", "3.6.0");
				put("5.8.0", "3.7.3");
				put("5.9.0", "3.7.3");
				put("5.9.1", "3.7.3");
				put("5.10.0", "3.8.3");
				put("5.10.1", "3.8.3");
				put("5.11.0", "3.8.6");
				put("5.11.1", "3.8.6");
				put("5.12.0", "3.8.6");
				put("6.0.0", "3.8.6");
				put("6.1.0", "3.8.6");
				put("6.2.0", "3.8.9");
			}
		};

	private boolean _download;
	private boolean _global;
	private final File _nodeCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_NODE_CACHE_DIR_NAME);
	private Object _nodeDir;
	private Object _nodeUrl;
	private Object _nodeVersion = "5.5.0";
	private final List<Object> _npmArgs = new ArrayList<>();
	private Object _npmUrl;
	private Object _npmVersion;
	private final Project _project;
	private Object _scriptFile;
	private boolean _useLatestNode;
	private Object _useNpm = true;
	private Object _yarnUrl;
	private Object _yarnVersion = "1.13.0";

	private static class NodeInfo {

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

}