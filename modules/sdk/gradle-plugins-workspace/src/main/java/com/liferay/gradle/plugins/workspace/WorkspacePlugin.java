/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.gradle.plugins.workspace;

import com.liferay.gradle.plugins.util.PortalTools;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import com.liferay.petra.string.StringUtil;
import groovy.lang.Closure;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.gradle.StartParameter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public class WorkspacePlugin implements Plugin<Settings> {

	public static final String EXTENSION_NAME = "liferayWorkspace";

	public static final String PROPERTY_PREFIX = "liferay.workspace.";

	@Override
	@SuppressWarnings("serial")
	public void apply(Settings settings) {
		Gradle gradle = settings.getGradle();

		final WorkspaceExtension workspaceExtension = _addWorkspaceExtension(
			settings);

		for (ProjectConfigurator projectConfigurator :
				workspaceExtension.getProjectConfigurators()) {

			for (File rootDir : projectConfigurator.getDefaultRootDirs()) {
				for (File projectDir :
						projectConfigurator.getProjectDirs(rootDir)) {

					String projectPath = GradleUtil.getProjectPath(
						projectDir, settings.getRootDir());

					settings.include(new String[] {projectPath});

					_projectConfiguratorsMap.put(
						projectPath, projectConfigurator);
				}
			}
		}

		gradle.beforeProject(
			new Closure<Void>(settings) {

				@SuppressWarnings("unused")
				public void doCall(Project project) {
					_setPortalVersion(project, workspaceExtension);

					Plugin<Project> plugin = null;

					if (project.getParent() == null) {
						for (ProjectConfigurator projectConfigurator :
								workspaceExtension.getProjectConfigurators()) {

							projectConfigurator.configureRootProject(
								project, workspaceExtension);
						}

						plugin =
							workspaceExtension.getRootProjectConfigurator();
					}
					else {
						plugin = _projectConfiguratorsMap.get(
							project.getPath());
					}

					if (plugin != null) {
						plugin.apply(project);
					}
				}

			});



		gradle.afterProject(project -> {
			List<String> globs = StringUtil.split(GradleUtil.getProperty(
				project.getExtensions(), WorkspacePlugin.PROPERTY_PREFIX + "dir.excludes.globs",
				null));

			FileSystem fileSystem = FileSystems.getDefault();
			List<PathMatcher> pathMatchers = new ArrayList<>();
			String rootDirPath = settings.getRootDir().getPath();

			System.out.println("DREWWASHERE");

			for (String glob : globs) {
				System.out.println(glob);
				pathMatchers.add(
					fileSystem.getPathMatcher(
						String.format("glob:%s/%s", rootDirPath, glob)));
			}

			StartParameter startParameter = gradle.getStartParameter();

			File projectDir = project.getProjectDir();

			if (Objects.equals(startParameter.getCurrentDir(), projectDir)) {
				return;
			}

			TaskContainer taskContainer = project.getTasks();

			Path path = projectDir.toPath();

			for (PathMatcher pathMatcher : pathMatchers) {
				if (!pathMatcher.matches(path)) {
					continue;
				}

				for (Task task : taskContainer) {
					System.out.printf("Disabling task %s\n", task.getPath());
					task.setEnabled(false);
				}

				return;
			}
		});
	}

	private WorkspaceExtension _addWorkspaceExtension(Settings settings) {
		ExtensionAware extensionAware = (ExtensionAware)settings.getGradle();

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		return extensionContainer.create(
			EXTENSION_NAME, WorkspaceExtension.class, settings);
	}

	private void _setPortalVersion(
		Project project, WorkspaceExtension workspaceExtension) {

		String portalVersion = GradleUtil.getProperty(
			project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, (String)null);

		if (Validator.isNotNull(portalVersion)) {
			return;
		}

		String bundleUrl = workspaceExtension.getBundleUrl();

		if (Objects.nonNull(bundleUrl) && bundleUrl.contains("7.0.")) {
			GradleUtil.setProperty(project, "portal.version", "7.0.x");
		}
	}

	private static final Map<String, ProjectConfigurator>
		_projectConfiguratorsMap = new HashMap<>();

}