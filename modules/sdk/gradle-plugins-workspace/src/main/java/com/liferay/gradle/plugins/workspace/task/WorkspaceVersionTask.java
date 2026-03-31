/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import java.io.File;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;

import org.gradle.api.invocation.Gradle;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.VersionNumber;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kyle Miho
 */
public class WorkspaceVersionTask extends DefaultTask {
	@Input
	public Property<String> getCurrentVersionProperty() {
		return _currentVersionProperty;
	}

	private final Property<String> _currentVersionProperty;

	@Input
	public Property<String> getLatestVersionProperty() {
		return _latestVersionProperty;
	}

	private final Property<String> _latestVersionProperty;

	public WorkspaceVersionTask() throws Exception {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		_currentVersionProperty = objects.property(String.class);
		_latestVersionProperty = objects.property(String.class);

		_latestVersionProperty.convention(project.provider(() -> {
			return null;
		}));

		_cacheFile = new File(project.getRootDir(), ".workspacecheck");

		if (_cacheFile.exists()) {
			try {
				String content = new String(
					Files.readAllBytes(_cacheFile.toPath())
				).trim();

				_lastCheckedTime = Long.parseLong(content);
			}
			catch (Exception exception) {
				throw new Exception("Failed to read from .workspacecheck file");
			}
		}
		else {
			_lastCheckedTime = 0;
		}

		_checkInterval = _getWorkspaceCheckInterval();
	}

	@TaskAction
	public void printVersionInfo() throws Exception {
		if (_checkInterval == -1) {
			return;
		}

		long timeDifference = System.currentTimeMillis() - _lastCheckedTime;

		if ((timeDifference < _checkInterval) && (_checkInterval != 0)) {
			long days = TimeUnit.MILLISECONDS.toDays(timeDifference);
			long hours = TimeUnit.MILLISECONDS.toHours(timeDifference) % 24;
			long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60;
			long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference) % 60;

			System.out.println(
				"Time since last workspace version check: " +
					String.format(
						"%dd %dh %dm %ds", days, hours, minutes, seconds));

			return;
		}

		 _currentWorkspaceVersion = _getLiferayWorkspaceVersion(null, null);

		try {
			URL url = new URL(
				"https://repository.liferay.com/nexus/content/groups/public/com/liferay/com.liferay.gradle.plugins.workspace/maven-metadata.xml");

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.setReadTimeout(10000);
			httpURLConnection.setRequestMethod("GET");

			DocumentBuilderFactory documentBuilderFactory =
				DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document xmlDocument = documentBuilder.parse(
				httpURLConnection.getInputStream());

			NodeList nodeList = xmlDocument.getElementsByTagName("latest");

			if (nodeList.getLength() > 0) {
				Node node = nodeList.item(0);

				String content = node.getTextContent();

				_latestWorkspaceVersion = VersionNumber.parse(content);
			}
		}
		catch (Exception exception) {
			throw new Exception(
				"Unable to get latest workspace version from repostory.liferay.com");
		}

		try {
			long currentTime = System.currentTimeMillis();

			Files.write(
				_cacheFile.toPath(),
				String.valueOf(
					currentTime
				).getBytes());
		}
		catch (Exception exception) {
			System.out.println("Failed to write to cache file.");
		}

		if ((_currentWorkspaceVersion == null) ||
			(_latestWorkspaceVersion == null)) {

			System.out.println("Unable to get workspace version");

			return;
		}

		if (_latestWorkspaceVersion.compareTo(_currentWorkspaceVersion) > 0) {
			System.out.println(
				"Latest workspace version is newer than current workspace version.");
			System.out.println(
				"Current Workspace Version: " + _currentWorkspaceVersion);
			System.out.println(
				"Latest Workspace Version: " + _latestWorkspaceVersion);
		}
		else {
			System.out.println("Current workspace version is up to date.");
		}
	}

	private VersionNumber _getLiferayWorkspaceVersion(String groupName, String artifactName) {
		if (true) {
			return VersionNumber.parse(_currentVersionProperty.get());
		}
		//project.getGradle().getSettings().getBuildscript().getConfigurations().getByName("classpath").getIncoming().getArtifacts().getArtifacts()

		//getProject().getRootProject().getGradle().getSettings().getBuildscript().getConfigurations().getByName("classpath").getDependencies();

		Project project = getProject();

		Project rootProject = project.getRootProject();

		Gradle gradle = rootProject.getGradle();

		//Settings settings = gradle.getSettings();

		ExtensionContainer extensionContainer = gradle.getExtensions();

		ExtraPropertiesExtension extraPropertiesExtension = extensionContainer.getExtraProperties();

		Object liferayWorkspaceVersion = extraPropertiesExtension.get("liferayWorkspaceVersion");

		if (liferayWorkspaceVersion != null) {
			return VersionNumber.parse(liferayWorkspaceVersion.toString());
		}

		return null;
	}

	private long _getWorkspaceCheckInterval() {
		String time = (String)getProject().findProperty(
			"liferay.workspace.version.check.frequency");

		if ((time == null) || time.equals("0")) {
			return 0;
		}

		if (time.equals("-1")) {
			return -1;
		}

		Matcher matcher = _workspaceCheckFrequencyPattern.matcher(time.trim());

		if (matcher.matches()) {
			long value = Long.parseLong(matcher.group(1));

			String unit = matcher.group(
				2
			).toLowerCase();

			if (unit.equals("s")) {
				return TimeUnit.SECONDS.toMillis(value);
			}
			else if (unit.equals("m")) {
				return TimeUnit.MINUTES.toMillis(value);
			}
			else if (unit.equals("h")) {
				return TimeUnit.HOURS.toMillis(value);
			}
			else if (unit.equals("d")) {
				return TimeUnit.DAYS.toMillis(value);
			}

			return 0;
		}

		return 0;
	}

	private static final Pattern _gradlePluginsWorkspaceVersionPattern =
		Pattern.compile(
			"group:.*?\"com\\.liferay\".*?name:.*?\"com\\.liferay\\.gradle\\.plugins\\.workspace\".*?version:.*?\"(.*)\"");
	private static final Pattern _workspaceCheckFrequencyPattern =
		Pattern.compile("(\\d+)([smhd])", Pattern.CASE_INSENSITIVE);

	private final File _cacheFile;
	private long _checkInterval;
	private VersionNumber _currentWorkspaceVersion;
	private final long _lastCheckedTime;
	private VersionNumber _latestWorkspaceVersion;

}