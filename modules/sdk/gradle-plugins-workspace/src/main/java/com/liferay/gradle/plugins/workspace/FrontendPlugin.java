/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.json.JsonSlurper;

import groovy.lang.Closure;

import java.io.File;

import java.util.Collections;
import java.util.Map;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

/**
 * @author Gregory Amerson
 */
public class FrontendPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, BasePlugin.class);
		GradleUtil.applyPlugin(project, LiferayBasePlugin.class);
		GradleUtil.applyPlugin(project, LiferayWorkspaceNodePlugin.class);

		LiferayWorkspaceNodeUtil.ensureMinimumVersions(project);

		Map<String, Object> packageJsonMap = _getPackageJsonMap(project);

		_configureArchivesBaseName(project, packageJsonMap);
		_configureVersion(project, packageJsonMap);

		_configureArtifacts(project);
		_configureConfigurationDefault(project);
		_configureTaskClean(project);
		_configureTaskDeploy(project);
	}

	private void _configureArchivesBaseName(
		Project project, Map<String, Object> packageJsonMap) {

		String name = null;

		if (packageJsonMap != null) {
			name = (String)packageJsonMap.get("name");
		}

		if (Validator.isNull(name)) {
			return;
		}

		BasePluginConvention basePluginConvention = GradleUtil.getConvention(
			project, BasePluginConvention.class);

		basePluginConvention.setArchivesBaseName(name);
	}

	@SuppressWarnings("serial")
	private void _configureArtifacts(final Project project) {
		ArtifactHandler artifacts = project.getArtifacts();

		artifacts.add(
			Dependency.ARCHIVES_CONFIGURATION, _getJarFile(project),
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(
					ConfigurablePublishArtifact configurablePublishArtifact) {

					Task buildTask = GradleUtil.getTask(
						project, LifecycleBasePlugin.BUILD_TASK_NAME);

					if (GradleUtil.hasTask(
							project, NodePlugin.PACKAGE_RUN_BUILD_TASK_NAME)) {

						buildTask.finalizedBy(
							NodePlugin.PACKAGE_RUN_BUILD_TASK_NAME);
					}
				}

			});
	}

	private void _configureConfigurationDefault(Project project) {
		Configuration defaultConfiguration = GradleUtil.getConfiguration(
			project, Dependency.DEFAULT_CONFIGURATION);

		Configuration archivesConfiguration = GradleUtil.getConfiguration(
			project, Dependency.ARCHIVES_CONFIGURATION);

		defaultConfiguration.extendsFrom(archivesConfiguration);
	}

	private void _configureTaskClean(Project project) {
		Delete delete = (Delete)GradleUtil.getTask(
			project, BasePlugin.CLEAN_TASK_NAME);

		delete.delete("build", "dist");
	}

	private void _configureTaskDeploy(Project project) {
		Copy copy = (Copy)GradleUtil.getTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME);

		copy.dependsOn("build");
		copy.from(_getJarFile(project));
	}

	private void _configureVersion(
		Project project, Map<String, Object> packageJsonMap) {

		String version = (String)packageJsonMap.get("version");

		if (Validator.isNotNull(version)) {
			project.setVersion(version);
		}
	}

	private File _getJarFile(Project project) {
		return project.file(
			"dist/" + GradleUtil.getArchivesBaseName(project) + "-" +
				project.getVersion() + ".jar");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> _getPackageJsonMap(Project project) {
		File file = project.file("package.json");

		if (!file.exists()) {
			return Collections.emptyMap();
		}

		JsonSlurper jsonSlurper = new JsonSlurper();

		return (Map<String, Object>)jsonSlurper.parse(file);
	}

}