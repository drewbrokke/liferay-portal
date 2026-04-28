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
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.internal.VersionNumber;

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
	public void printVersionInfo() throws Exception {
		if (_checkInterval == -1) {
			return;
		}

		long timeDifference = System.currentTimeMillis() - _lastCheckedTime;

		if ((timeDifference < _checkInterval) && (_checkInterval != 0)) {
			return;
		}

		try {
			long currentTime = System.currentTimeMillis();

			String currentTimeString = String.valueOf(currentTime);

			Files.write(_cacheFile.toPath(), currentTimeString.getBytes());
		}
		catch (Exception exception) {
			if (_logger.isLifecycleEnabled()) {
				_logger.lifecycle("Failed to write to cache file.");
			}
		}

		if ((_currentVersionProperty == null) ||
			(_latestVersionProperty == null)) {

			if (_logger.isLifecycleEnabled()) {
				_logger.lifecycle("Unable to get workspace version.");
			}

			return;
		}

		VersionNumber currentWorkspaceVersion = VersionNumber.parse(
			_currentVersionProperty.get());
		VersionNumber latestWorkspaceVersion = VersionNumber.parse(
			_latestVersionProperty.get());

		if (latestWorkspaceVersion.compareTo(currentWorkspaceVersion) > 0) {
			if (_logger.isLifecycleEnabled()) {
				_logger.lifecycle(
					"Latest workspace version is newer than current " +
						"workspace version.");
				_logger.lifecycle(
					"Current Workspace Version: " + currentWorkspaceVersion);
				_logger.lifecycle(
					"Latest Workspace Version: " + latestWorkspaceVersion);
			}
		}
	}

	private long _getWorkspaceCheckInterval() {
		Project project = getProject();

		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		String time = workspaceExtension.getVersionCheckFrequency();

		if ((time == null) || time.equals("0")) {
			return 0;
		}

		if (time.equals("-1")) {
			return -1;
		}

		Matcher matcher = _workspaceCheckFrequencyPattern.matcher(time.trim());

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

			return 0;
		}

		return 0;
	}

	private static final Pattern _workspaceCheckFrequencyPattern =
		Pattern.compile("(\\d+)([smhd])?", Pattern.CASE_INSENSITIVE);

	private final File _cacheFile;
	private long _checkInterval;
	private final Property<String> _currentVersionProperty;
	private final long _lastCheckedTime;
	private final Property<String> _latestVersionProperty;
	private final Logger _logger = getLogger();

}