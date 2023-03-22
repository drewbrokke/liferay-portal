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

package com.liferay.gradle.plugins.workspace.configurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.extensions.AppServer;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.TomcatAppServer;
import com.liferay.gradle.plugins.workspace.ProjectConfigurator;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.WorkspacePlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public abstract class BaseProjectConfigurator implements ProjectConfigurator {

	
	private static final List<String> _defaultIgnorePaths = Arrays.asList(
			".gradle", "**/.idea", ".settings", "**/.sass-cache", "**/bin", "**/build", "**/classes", "**/dist",
			"**/liferay-theme.json", "**/node_modules", "**/liferay-npm-bundler-report.html", "**/target", "bundles",
			"gradle");
	
	private String _modulesExcludeDirs = null;
	private Map<String, Path> _excludeProjectPathMap = Collections.emptyMap();
	
	public BaseProjectConfigurator(Settings settings) {
		String defaultRootDirNames = GradleUtil.getProperty(
			settings, getDefaultRootDirPropertyName(), (String)null);
		File rootDir = settings.getRootDir();

		if (Validator.isNotNull(defaultRootDirNames)) {
			if (defaultRootDirNames.equals("*")) {
				_defaultRootDirs = Collections.singleton(rootDir);
			}
			else {
				_defaultRootDirs = new HashSet<>();

				for (String dirName : defaultRootDirNames.split("\\s*,\\s*")) {
					File dir = new File(rootDir, dirName);

					_defaultRootDirs.add(dir);
				}
			}
		}
		else {
			_defaultRootDirs = Collections.singleton(rootDir);
		}
		
		_modulesExcludeDirs = GradleUtil.getProperty(
				settings, WorkspacePlugin.PROPERTY_PREFIX + "dir.excludes.globs",
				null);
		
 		_excludeProjectPathMap = _getExcludeProjectPathMap(settings);
	}
	
	protected void disableTasks(
			Map<String, Path> excludeProjectPathMap, Project project) {

			File projectDir = project.getProjectDir();

			Path projectDirPath = projectDir.toPath();

			Collection<Path> projectPaths = excludeProjectPathMap.values();

			for (Path excludeProjectPath : projectPaths) {
				if (projectDirPath.startsWith(excludeProjectPath)) {
					Map<Project, Set<Task>> projectTasksMap = project.getAllTasks(
						true);

					Collection<Set<Task>> projectTasks = projectTasksMap.values();

					for (Set<Task> tasks : projectTasks) {
						for (Task task : tasks) {
							task.setEnabled(false);
						}
					}
				}
			}
		}	
	
	protected Map<String, Path> getExcludeProjectMap(){
		return _excludeProjectPathMap;
	}
	
	private class ModulesProjectExcludeVisitor extends SimpleFileVisitor<Path> {

		public ModulesProjectExcludeVisitor(String modulesExcludeDirName, List<PathMatcher> ignorePathMatchers) {
			_modulesExcludeDirName = modulesExcludeDirName;
			_ignorePathMatchers = ignorePathMatchers;
		}

		public Path getModulesExcludePath() {
			return _modulesExcludePath;
		}

		@Override
		public FileVisitResult preVisitDirectory(
				Path dir, BasicFileAttributes basicFileAttributes)
			throws IOException {

			boolean shouldIgnorePath = _ignorePathMatchers.stream(
			).anyMatch(
				pathMatcher -> pathMatcher.matches(dir.toAbsolutePath())
			);

			if (shouldIgnorePath) {
				return FileVisitResult.SKIP_SUBTREE;
			}
			
			super.preVisitDirectory(dir, basicFileAttributes);

			if (Files.exists(dir.resolve(_modulesExcludeDirName))) {
				_modulesExcludePath = dir.resolve(_modulesExcludeDirName);

				return FileVisitResult.SKIP_SUBTREE;
			}

			return FileVisitResult.CONTINUE;
		}

		private List<PathMatcher> _ignorePathMatchers;
		private final String _modulesExcludeDirName;
		private Path _modulesExcludePath;

	}	

	private Map<String, Path> _getExcludeProjectPathMap(Settings settings) {
		if (Objects.isNull(_modulesExcludeDirs)) {
			return Collections.emptyMap();
		}

		List<String> excludeDirs = Arrays.asList(
			_modulesExcludeDirs.split(","));

		if (Objects.isNull(excludeDirs) ||
			excludeDirs.isEmpty()) {

			return Collections.emptyMap();
		}

		Map<String, Path> excludeProjectPathMap = new HashMap<>();

		File rootDir = settings.getRootDir();

		if (rootDir.isDirectory()) {

			FileSystem fileSystem = FileSystems.getDefault();
			
			List<PathMatcher> ignorePathMatchers = _defaultIgnorePaths.stream().map(
						ignorePath -> fileSystem.getPathMatcher("glob:" + ignorePath)
					).collect(
						Collectors.toList()
					);
			
			try {
				for (String excludeDirString : excludeDirs) {
					ModulesProjectExcludeVisitor modulesExcludeVisitor =
						new ModulesProjectExcludeVisitor(
							excludeDirString.trim(), ignorePathMatchers);
	
					Files.walkFileTree(
						rootDir.toPath(), modulesExcludeVisitor);
	
					Path modulesExcludePath =
						modulesExcludeVisitor.getModulesExcludePath();
	
					if (Objects.nonNull(modulesExcludePath)) {
						excludeProjectPathMap.put(
							excludeDirString, modulesExcludePath);
					}
				}
			}
			catch (Exception exception) {
				return Collections.emptyMap();
			}
	}

		return excludeProjectPathMap;
	}
	
	@Override
	public void configureRootProject(
		Project project, WorkspaceExtension workspaceExtension) {
	}

	@Override
	public Iterable<File> getDefaultRootDirs() {
		return _defaultRootDirs;
	}

	@Override
	public Iterable<File> getProjectDirs(File rootDir) {
		try {
			if (!rootDir.exists()) {
				return Collections.emptySet();
			}

			return doGetProjectDirs(rootDir);
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to get project directories from " + rootDir, exception);
		}
	}

	protected Copy addTaskDockerDeploy(
		Project project, Object sourcePath, File dockerDeployDir) {

		if (GradleUtil.hasTask(
				project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME)) {

			TaskProvider<Copy> taskProvider = GradleUtil.getTaskProvider(
				project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME,
				Copy.class);

			return taskProvider.get();
		}

		Copy copy = GradleUtil.addTask(
			project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME,
			Copy.class);

		copy.from(sourcePath);

		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return dockerDeployDir;
				}

			});

		copy.setDescription(
			"Assembles the project and deploys it to the Liferay Docker " +
				"container.");

		copy.setGroup(RootProjectConfigurator.DOCKER_GROUP);

		Task deployTask = GradleUtil.getTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME);

		deployTask.finalizedBy(copy);

		Task buildDockerImageTask = GradleUtil.getTask(
			project.getRootProject(),
			RootProjectConfigurator.BUILD_DOCKER_IMAGE_TASK_NAME);

		buildDockerImageTask.dependsOn(deployTask);

		return copy;
	}

	protected Copy addTaskDockerDeploy(
		Project project, Object sourcePath,
		WorkspaceExtension workspaceExtension) {

		File dockerDir = workspaceExtension.getDockerDir();

		File dockerDeployDir = new File(dockerDir, "deploy");

		return addTaskDockerDeploy(project, sourcePath, dockerDeployDir);
	}

	protected void configureLiferay(
		Project project, WorkspaceExtension workspaceExtension) {

		LiferayExtension liferayExtension = GradleUtil.getExtension(
			project, LiferayExtension.class);

		liferayExtension.setAppServerParentDir(workspaceExtension.getHomeDir());

		String version = GradleUtil.getProperty(
			project, "app.server.tomcat.version", (String)null);

		File dir = workspaceExtension.getHomeDir();

		if ((version == null) && dir.exists()) {
			for (String fileName : dir.list()) {
				if (fileName.startsWith("tomcat-")) {
					version = fileName.substring(fileName.indexOf("-") + 1);

					NamedDomainObjectCollection<AppServer>
						namedDomainObjectCollection =
							liferayExtension.getAppServers();

					TomcatAppServer tomcatAppServer =
						(TomcatAppServer)namedDomainObjectCollection.getByName(
							"tomcat");

					tomcatAppServer.setVersion(version);
				}
			}
		}
	}

	protected abstract Iterable<File> doGetProjectDirs(File rootDir)
		throws Exception;

	protected String getDefaultRootDirName() {
		return getName();
	}

	protected String getDefaultRootDirPropertyName() {
		return WorkspacePlugin.PROPERTY_PREFIX + getName() + ".dir";
	}

	protected boolean isExcludedDirName(String dirName) {
		if (dirName == null) {
			return false;
		}

		if (dirName.equals(".gradle") || dirName.equals("build") ||
			dirName.equals("build_gradle") || dirName.equals("dist") ||
			dirName.equals("gradle") || dirName.equals("node_modules") ||
			dirName.equals("node_modules_cache") || dirName.equals("src")) {

			return true;
		}

		return false;
	}

	private final Set<File> _defaultRootDirs;

}