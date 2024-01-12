/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import aQute.bnd.version.Version;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.gradle.plugins.node.NodeExtension;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.gradle.util.Validator;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * @author Drew Brokke
 */
public class LiferayWorkspaceNodeUtil {

	public static void applyNodePlugin(Project project) {
		GradleUtil.applyPlugin(project, NodePlugin.class);

		configureLTS(project);
	}

	public static void configureLTS(Project project) {
		_getLTSNodeInfoOptional(
			project
		).ifPresent(
			nodeInfo -> {
				NodeExtension nodeExtension = GradleUtil.getExtension(
					project, NodeExtension.class);

				String nodeVersion = nodeInfo.getNodeVersion();
				String npmVersion = nodeInfo.getNpmVersion();

				Logger logger = project.getLogger();

				if (logger.isInfoEnabled()) {
					String lts = nodeInfo.getLts();

					logger.info(
						"Using {} LTS Node version: {}", StringUtil.quote(lts),
						nodeVersion);
					logger.info(
						"Using {} LTS NPM version: {}", StringUtil.quote(lts),
						npmVersion);
				}

				nodeExtension.setNodeVersion(nodeVersion);
				nodeExtension.setNpmVersion(npmVersion);
			}
		);
	}

	public static void configureMinimumVersions(Project project) {
		NodeExtension nodeExtension = GradleUtil.getExtension(
			project, NodeExtension.class);

		Logger logger = project.getLogger();

		try {
			Version nodeVersion = Version.parseVersion(
				nodeExtension.getNodeVersion());

			if (_MINIMUM_NODE_VERSION.compareTo(nodeVersion) > 0) {
				if (logger.isInfoEnabled()) {
					logger.info(
						"Using minimum Node version {}", _MINIMUM_NODE_VERSION);
				}

				nodeExtension.setNodeVersion(_MINIMUM_NODE_VERSION.toString());
			}
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to parse Node version", exception);
		}

		try {
			Version npmVersion = Version.parseVersion(
				nodeExtension.getNpmVersion());

			if (_MINIMUM_NPM_VERSION.compareTo(npmVersion) > 0) {
				if (logger.isInfoEnabled()) {
					logger.info(
						"Using minimum NPM version {}", _MINIMUM_NODE_VERSION);
				}

				nodeExtension.setNpmVersion(_MINIMUM_NPM_VERSION.toString());
			}
		}
		catch (Exception exception) {
			throw new GradleException("Unable to parse NPM version", exception);
		}
	}

	private static String _getLts(Project project) {
		return GradleUtil.getProperty(
			project, _LTS_PROPERTY_NAME, (String)null);
	}

	private static Optional<NodeInfo> _getLTSNodeInfoOptional(Project project) {
		String lts = _getLts(project);

		if (Validator.isNull(lts)) {
			return Optional.empty();
		}

		List<NodeInfo> nodeInfos = _getNodeInfos();

		Optional<NodeInfo> nodeInfoOptional = nodeInfos.stream(
		).filter(
			nodeInfo -> Objects.equals(nodeInfo.getLts(), lts)
		).max(
			(first, second) -> {
				Version firstVersion = Version.parseVersion(
					first.getNodeVersion());
				Version secondVersion = Version.parseVersion(
					second.getNodeVersion());

				return firstVersion.compareTo(secondVersion);
			}
		);

		if (!nodeInfoOptional.isPresent()) {
			Logger logger = project.getLogger();

			if (logger.isErrorEnabled()) {
				logger.error(
					"Property \"{}\" must be one of: {}", _LTS_PROPERTY_NAME,
					nodeInfos.stream(
					).map(
						NodeInfo::getLts
					).distinct(
					).filter(
						nodeInfoLts -> !Objects.equals(nodeInfoLts, "false")
					).sorted(
					).collect(
						Collectors.joining(", ")
					));
			}
		}

		return nodeInfoOptional;
	}

	private static List<NodeInfo> _getNodeInfos() {
		if (_nodeInfos == null) {
			DownloadCommand downloadCommand = new DownloadCommand();

			downloadCommand.setCacheDir(
				new File(
					System.getProperty("user.home"),
					_DEFAULT_NODE_CACHE_DIR_NAME));
			downloadCommand.setConnectionTimeout(5 * 1000);
			downloadCommand.setPassword(null);
			downloadCommand.setQuiet(true);
			downloadCommand.setToken(false);
			downloadCommand.setUserName(null);

			try {
				downloadCommand.setUrl(new URL(_PRODUCT_NODE_URL));

				downloadCommand.execute();
			}
			catch (Exception exception) {
				throw new GradleException(
					"Unable to get node version", exception.getCause());
			}

			try (JsonReader jsonReader = new JsonReader(
					Files.newBufferedReader(
						downloadCommand.getDownloadPath()))) {

				Gson gson = new Gson();

				TypeToken<List<NodeInfo>> typeToken =
					new TypeToken<List<NodeInfo>>() {
					};

				_nodeInfos = gson.fromJson(jsonReader, typeToken.getType());
			}
			catch (IOException ioException) {
				throw new GradleException(
					"Could not read downloaded file", ioException.getCause());
			}
		}

		return _nodeInfos;
	}

	private static final String _DEFAULT_NODE_CACHE_DIR_NAME = ".liferay/node";

	private static final String _LTS_PROPERTY_NAME = "node.lts.codename";

	private static final Version _MINIMUM_NODE_VERSION = Version.parseVersion(
		"10.15.3");

	private static final Version _MINIMUM_NPM_VERSION = Version.parseVersion(
		"6.4.1");

	private static final String _PRODUCT_NODE_URL =
		"https://nodejs.org/dist/index.json";

	private static List<NodeInfo> _nodeInfos;

	private static class NodeInfo {

		public String getLts() {
			return _lts;
		}

		public String getNodeVersion() {
			return _nodeVersion.substring(1);
		}

		public String getNpmVersion() {
			return _npmVersion;
		}

		@SerializedName("lts")
		private String _lts;

		@SerializedName("version")
		private String _nodeVersion;

		@SerializedName("npm")
		private String _npmVersion;

	}

}