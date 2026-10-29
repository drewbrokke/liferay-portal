/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

/**
 * @author Drew Brokke
 */
public class UpdateWorkspaceTask extends DefaultTask {

	public UpdateWorkspaceTask() {
		Project project = getProject();

		ObjectFactory objectFactory = project.getObjects();

		_toVersionProperty = objectFactory.property(String.class);

		_toVersionProperty.convention("latest.release");
	}

	@Input
	@Option(
		description = "The version of the evergreen workspace to update to. Defaults to the latest release.",
		option = "to-version"
	)
	public Property<String> getToVersion() {
		return _toVersionProperty;
	}

	@TaskAction
	public void updateWorkspace() {
		Project project = getProject();

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		DependencyHandler dependencyHandler = project.getDependencies();

		Configuration configuration =
			configurationContainer.detachedConfiguration(
				dependencyHandler.create(
					_EVERGREEN_GROUP + ":" + _EVERGREEN_ARTIFACT_ID + ":" +
						_toVersionProperty.get()));

		configuration.setTransitive(false);

		ResolvedConfiguration resolvedConfiguration =
			configuration.getResolvedConfiguration();

		Set<ResolvedArtifact> resolvedArtifacts =
			resolvedConfiguration.getResolvedArtifacts();

		Iterator<ResolvedArtifact> iterator = resolvedArtifacts.iterator();

		ResolvedArtifact resolvedArtifact = iterator.next();

		ResolvedModuleVersion resolvedModuleVersion =
			resolvedArtifact.getModuleVersion();

		ModuleVersionIdentifier moduleVersionIdentifier =
			resolvedModuleVersion.getId();

		Logger logger = getLogger();

		logger.lifecycle(
			"Updating workspace to {} version {}", _EVERGREEN_ARTIFACT_ID,
			moduleVersionIdentifier.getVersion());

		File file = resolvedArtifact.getFile();

		File projectDir = project.getProjectDir();

		List<String> addedPaths = new ArrayList<>();
		List<String> unchangedPaths = new ArrayList<>();
		List<String> updatedPaths = new ArrayList<>();

		project.copy(
			copySpec -> {
				copySpec.eachFile(
					fileCopyDetails -> {
						RelativePath relativePath =
							fileCopyDetails.getRelativePath();

						String path = relativePath.getPathString();

						File targetFile = relativePath.getFile(projectDir);

						if (!targetFile.exists()) {
							addedPaths.add(path);

							return;
						}

						if (_hasSameContent(
								fileCopyDetails.getFile(), targetFile)) {

							unchangedPaths.add(path);

							fileCopyDetails.exclude();

							return;
						}

						updatedPaths.add(path);
					});
				copySpec.from(project.zipTree(file));
				copySpec.into(projectDir);
			});

		_logPaths(logger, "Added", addedPaths);
		_logPaths(logger, "Updated", updatedPaths);

		logger.lifecycle("Unchanged: {} files", unchangedPaths.size());
	}

	private boolean _hasSameContent(File file1, File file2) {
		if (file1.length() != file2.length()) {
			return false;
		}

		try {
			long mismatchIndex = Files.mismatch(file1.toPath(), file2.toPath());

			if (mismatchIndex == -1) {
				return true;
			}

			return false;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private void _logPaths(Logger logger, String label, List<String> paths) {
		if (paths.isEmpty()) {
			return;
		}

		Collections.sort(paths);

		logger.lifecycle("{}:", label);

		for (String path : paths) {
			logger.lifecycle("  {}", path);
		}
	}

	private static final String _EVERGREEN_ARTIFACT_ID =
		"com.liferay.sample.workspace.evergreen";

	private static final String _EVERGREEN_GROUP = "com.liferay.workspace";

	private final Property<String> _toVersionProperty;

}