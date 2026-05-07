/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import java.io.File;

import java.nio.file.Files;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import org.osgi.framework.Version;

/**
 * @author Kyle Miho
 */
public class CheckWorkspaceVersionTask extends DefaultTask {

	@Inject
	public CheckWorkspaceVersionTask(ObjectFactory objects) {
		_cacheFileProperty = objects.fileProperty();
		_checkIntervalDaysProperty = objects.property(Integer.class);
		_currentVersionProperty = objects.property(String.class);
		_forceProperty = objects.property(
			Boolean.class
		).convention(
			false
		);

		_latestVersionProperty = objects.property(String.class);

		onlyIf(
			"Current version is set",
			task -> _currentVersionProperty.isPresent());
		onlyIf(
			"Latest version is set",
			task -> _latestVersionProperty.isPresent());
		onlyIf("The version check interval has elapsed", task -> _shouldRun());
	}

	@Internal
	public RegularFileProperty getCacheFileProperty() {
		return _cacheFileProperty;
	}

	@Input
	@Optional
	public Property<Integer> getCheckIntervalDaysProperty() {
		return _checkIntervalDaysProperty;
	}

	@Input
	public Property<String> getCurrentVersionProperty() {
		return _currentVersionProperty;
	}

	@Input
	@Option(description = "Ignore the version check interval", option = "force")
	public Property<Boolean> getForce() {
		return _forceProperty;
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
			RegularFile regularFile = _cacheFileProperty.get();

			File cacheFile = regularFile.getAsFile();

			Files.writeString(
				cacheFile.toPath(), String.valueOf(System.currentTimeMillis()));
		}
		catch (Exception exception) {
			Logger logger = getLogger();

			logger.lifecycle("Failed to write to cache file.");
		}
	}

	private long _readLastCheckedTime() {
		RegularFile regularFile = _cacheFileProperty.get();

		File cacheFile = regularFile.getAsFile();

		if (!cacheFile.exists()) {
			return 0;
		}

		try {
			return Long.parseLong(Files.readString(cacheFile.toPath()));
		}
		catch (Exception exception) {
			return 0;
		}
	}

	private boolean _shouldRun() {
		if (Objects.equals(Boolean.TRUE, _forceProperty.get())) {
			return true;
		}

		int checkIntervalDays = _checkIntervalDaysProperty.getOrElse(7);

		if (checkIntervalDays < 0) {
			return false;
		}

		if (checkIntervalDays == 0) {
			return true;
		}

		long timeDifference =
			System.currentTimeMillis() - _readLastCheckedTime();

		if (timeDifference >= TimeUnit.DAYS.toMillis(checkIntervalDays)) {
			return true;
		}

		return false;
	}

	private final RegularFileProperty _cacheFileProperty;
	private final Property<Integer> _checkIntervalDaysProperty;
	private final Property<String> _currentVersionProperty;
	private final Property<Boolean> _forceProperty;
	private final Property<String> _latestVersionProperty;

}