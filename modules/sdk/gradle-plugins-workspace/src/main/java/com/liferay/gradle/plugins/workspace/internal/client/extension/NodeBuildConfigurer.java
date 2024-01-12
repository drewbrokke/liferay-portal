/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceNodeUtil;

import groovy.json.JsonSlurper;

import java.io.File;

import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Gregory Amerson
 */
public class NodeBuildConfigurer implements ClientExtensionConfigurer {

	@Override
	public void apply(
		Project project,
		TaskProvider<Copy> assembleClientExtensionTaskProvider) {

		if (!_hasFrontendScript(project)) {
			return;
		}

		LiferayWorkspaceNodeUtil.applyNodePlugin(project);

		LiferayWorkspaceNodeUtil.configureMinimumVersions(project);

		assembleClientExtensionTaskProvider.configure(
			assembleClientExtensionTask -> {
				TaskContainer tasks = project.getTasks();

				Task task = tasks.findByName(
					NodePlugin.PACKAGE_RUN_BUILD_TASK_NAME);

				if (task != null) {
					assembleClientExtensionTask.dependsOn(task);
				}
			});
	}

	@SuppressWarnings("unchecked")
	private boolean _hasFrontendScript(Project project) {
		File packageJsonFile = project.file("package.json");

		if (!packageJsonFile.exists()) {
			return false;
		}

		JsonSlurper jsonSlurper = new JsonSlurper();

		Map<String, Object> packageJsonMap =
			(Map<String, Object>)jsonSlurper.parse(packageJsonFile);

		Map<String, Object> liferayThemeMap =
			(Map<String, Object>)packageJsonMap.get("liferayTheme");
		Map<String, Object> scriptsMap =
			(Map<String, Object>)packageJsonMap.get("scripts");

		if ((liferayThemeMap == null) && (scriptsMap != null)) {
			return true;
		}

		return false;
	}

}