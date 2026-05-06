/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;

import java.io.File;

import java.nio.file.Files;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.Property;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import org.osgi.framework.Version;

/**
 * @author Kyle Miho
 */
public class CheckWorkspaceVersionTask extends DefaultTask {

	public CheckWorkspaceVersionTask() throws Exception {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		_currentVersionProperty = objects.property(String.class);
		_latestVersionProperty = objects.property(String.class);

		_cacheFile = new File(project.getRootDir(), ".workspacecheck");

		if (_cacheFile.exists()) {
			try {
				String content = Files.readString(_cacheFile.toPath());

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

		onlyIf(
			"Current version is set",
			task -> _currentVersionProperty.isPresent());
		onlyIf(
			"Latest version is set",
			task -> _latestVersionProperty.isPresent());

		onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					if (_checkInterval == -1) {
						return false;
					}

					long timeDifference =
						System.currentTimeMillis() - _lastCheckedTime;

					if ((_checkInterval == 0) ||
						(timeDifference >= _checkInterval)) {

						return true;
					}

					return false;
				}

			});
	}

	@Input
	public Property<String> getCurrentVersionProperty() {
		return _currentVersionProperty;
	}

	@Input
	public Property<String> getLatestVersionProperty() {
		return _latestVersionProperty;
	}

	@TaskAction
	public void printVersionInfo() {
		Version currentVersion = Version.parseVersion(
			_currentVersionProperty.get());
		Version latestVersion = Version.parseVersion(
			_latestVersionProperty.get());

		if (currentVersion.compareTo(latestVersion) > 0) {
			Logger logger = getLogger();

			logger.lifecycle(
				"There is a newer version of Liferay Workspace available: ");
			logger.lifecycle("Current Workspace Version: " + currentVersion);
			logger.lifecycle("Latest Workspace Version: " + latestVersion);
		}

		try {
			Files.writeString(
				_cacheFile.toPath(),
				String.valueOf(System.currentTimeMillis()));
		}
		catch (Exception exception) {
			Logger logger = getLogger();

			logger.lifecycle("Failed to write to cache file.");
		}
	}

	private long _getWorkspaceCheckInterval() {
		Project project = getProject();

		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		String time = workspaceExtension.getVersionCheckInterval();

		if ((time == null) || time.equals("0")) {
			return 0;
		}

		if (time.equals("-1")) {
			return -1;
		}

		Matcher matcher = _workspaceCheckIntervalPattern.matcher(time.trim());

		if (matcher.matches()) {
			long value = Long.parseLong(matcher.group(1));

			String unit = matcher.group(2);

			if (unit == null) {
				return TimeUnit.SECONDS.toMillis(value);
			}

			if (unit.equalsIgnoreCase("s")) {
				return TimeUnit.SECONDS.toMillis(value);
			}
			else if (unit.equalsIgnoreCase("m")) {
				return TimeUnit.MINUTES.toMillis(value);
			}
			else if (unit.equalsIgnoreCase("h")) {
				return TimeUnit.HOURS.toMillis(value);
			}
			else if (unit.equalsIgnoreCase("d")) {
				return TimeUnit.DAYS.toMillis(value);
			}
		}

		Logger logger = getLogger();

		if (logger.isWarnEnabled()) {
			logger.warn("Invalid workspace check interval: " + time);
		}

		return 0;
	}

	private static final Pattern _workspaceCheckIntervalPattern =
		Pattern.compile("(\\d+)([smhd])?", Pattern.CASE_INSENSITIVE);

	private final File _cacheFile;
	private final long _checkInterval;
	private final Property<String> _currentVersionProperty;
	private final long _lastCheckedTime;
	private final Property<String> _latestVersionProperty;

}