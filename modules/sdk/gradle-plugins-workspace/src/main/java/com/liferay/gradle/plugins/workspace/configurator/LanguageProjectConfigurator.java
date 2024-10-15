/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.lang.builder.BuildLangTask;
import com.liferay.gradle.plugins.lang.builder.LangBuilderPlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.task.WriteLanguageBatchEngineDataTask;

import java.io.File;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Drew Brokke
 */
public class LanguageProjectConfigurator extends BaseProjectConfigurator {

	public static final String WRITE_LANGUAGE_BATCH_ENGINE_DATA_TASK_NAME =
		"writeLanguageBatchEngineData";

	public LanguageProjectConfigurator(Settings settings) {
		super(settings);

		_clientExtensionProjectConfigurator =
			new ClientExtensionProjectConfigurator(settings);
	}

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(
			project.getRootProject(), LangBuilderPlugin.class);

		TaskProvider<BuildLangTask> buildLangTaskProvider =
			GradleUtil.getTaskProvider(
				project.getRootProject(),
				LangBuilderPlugin.BUILD_LANG_TASK_NAME, BuildLangTask.class);

		buildLangTaskProvider.configure(
			task -> task.setLangDir(project.getProjectDir()));

		TaskProvider<WriteLanguageBatchEngineDataTask>
			writeLanguageBatchEngineDataTaskProvider =
				GradleUtil.addTaskProvider(
					project, WRITE_LANGUAGE_BATCH_ENGINE_DATA_TASK_NAME,
					WriteLanguageBatchEngineDataTask.class);

		writeLanguageBatchEngineDataTaskProvider.configure(
			task -> task.dependsOn(buildLangTaskProvider));

		_clientExtensionProjectConfigurator.apply(project);
	}

	@Override
	public String getName() {
		return "language";
	}

	@Override
	protected Iterable<File> doGetProjectDirs(File rootDir) throws Exception {
		final Set<File> projectDirs = new HashSet<>();

		Files.walkFileTree(
			rootDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (isExcludedDirName(dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (_isLanguageProject(rootDir, dirPath.toFile())) {
						projectDirs.add(dirPath.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return projectDirs;
	}

	private boolean _isLanguageProject(File rootDir, File projectDir) {
		Path dirPath = projectDir.toPath();

		if (Objects.equals(rootDir.toPath(), dirPath.getParent()) &&
			dirPath.endsWith(Paths.get("language")) &&
			Files.exists(
				Paths.get(dirPath.toString(), "Language.properties"))) {

			return true;
		}

		return false;
	}

	private final ClientExtensionProjectConfigurator
		_clientExtensionProjectConfigurator;

}