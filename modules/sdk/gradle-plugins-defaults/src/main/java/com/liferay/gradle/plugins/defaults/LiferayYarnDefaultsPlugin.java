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

package com.liferay.gradle.plugins.defaults;

import com.liferay.gradle.plugins.NodeDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.LiferayCIPatcherPlugin;
import com.liferay.gradle.plugins.defaults.internal.LiferayCIPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.CIUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.YarnPlugin;
import com.liferay.gradle.util.Validator;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Peter Shin
 */
public class LiferayYarnDefaultsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, YarnPlugin.class);

		GradleUtil.applyPlugin(project, NodeDefaultsPlugin.class);

		com.liferay.gradle.plugins.defaults.internal.NodeDefaultsPlugin.
			INSTANCE.apply(project);

		if (CIUtil.isRunningInCIEnvironment()) {
			LiferayCIPlugin.INSTANCE.apply(project);
		}

		if (CIUtil.isRunningInCIPatcherEnvironment()) {
			LiferayCIPatcherPlugin.INSTANCE.apply(project);
		}

		TaskProvider<Task> downloadNodeTaskProvider =
			GradleUtil.getTaskProvider(
				project, NodePlugin.DOWNLOAD_NODE_TASK_NAME);

		_configureTaskProvider(project, downloadNodeTaskProvider);

		TaskProvider<Task> yarnInstallTaskProvider = GradleUtil.getTaskProvider(
			project, YarnPlugin.YARN_INSTALL_TASK_NAME);

		_configureTaskProvider(project, yarnInstallTaskProvider);
	}

	private void _configureTaskProvider(
		final Project project, TaskProvider<Task> taskProvider) {

		taskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Gradle gradle = project.getGradle();

					StartParameter startParameter = gradle.getStartParameter();

					String buildProfile = System.getProperty("build.profile");

					if (startParameter.isParallelProjectExecutionEnabled() ||
						Validator.isNotNull(buildProfile)) {

						task.setEnabled(false);
					}
				}

			});
	}

}