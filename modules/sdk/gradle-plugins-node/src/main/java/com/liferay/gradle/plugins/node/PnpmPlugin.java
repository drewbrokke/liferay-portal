/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node;

import com.liferay.gradle.plugins.node.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.task.ExecutePnpmTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.PnpmInstallTask;

import java.io.File;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Seiphon Wang
 */
public class PnpmPlugin implements Plugin<Project> {

	public static final String PNPM_INSTALL_TASK_NAME = "pnpmInstall";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, NodePlugin.class);

		TaskProvider<PnpmInstallTask> pnpmInstallTaskProvider =
			GradleUtil.addTaskProvider(
				project, PNPM_INSTALL_TASK_NAME, PnpmInstallTask.class);

		_configureTaskPnpmInstallProvider(pnpmInstallTaskProvider);

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecutePnpmTask.class,
			new Action<ExecutePnpmTask>() {

				@Override
				public void execute(ExecutePnpmTask executePnpmTask) {
					executePnpmTask.setUseGradleExec(
						GradleUtil.isRunningInsideDaemon());

					executePnpmTask.setNodeDir(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								NodeExtension nodeExtension =
									GradleUtil.getExtension(
										executePnpmTask.getProject(),
										NodeExtension.class);

								if (nodeExtension.isDownload()) {
									return nodeExtension.getNodeDir();
								}

								return null;
							}

						});
				}

			});

		project.allprojects(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureNodeProject(project, pnpmInstallTaskProvider);
				}

			});
	}

	private void _configureNodeProject(
		Project project,
		TaskProvider<PnpmInstallTask> pnpmInstallTaskProvider) {

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					TaskContainer taskContainer = project.getTasks();

					taskContainer.withType(
						NpmInstallTask.class,
						new Action<NpmInstallTask>() {

							@Override
							public void execute(NpmInstallTask npmInstallTask) {
								NodeExtension nodeExtension =
									GradleUtil.getExtension(
										npmInstallTask.getProject(),
										NodeExtension.class);

								nodeExtension.setPackageManager("pnpm");

								npmInstallTask.finalizedBy(
									pnpmInstallTaskProvider);
							}

						});
				}

			});
	}

	private void _configureTaskPnpmInstallProvider(
		TaskProvider<PnpmInstallTask> pnpmInstallTaskProvider) {

		pnpmInstallTaskProvider.configure(
			new Action<PnpmInstallTask>() {

				@Override
				public void execute(PnpmInstallTask pnpmInstallTask) {
					pnpmInstallTask.setDescription(
						"Installs Node packages from package.json.");
					pnpmInstallTask.setFrozenLockFile(
						Boolean.parseBoolean(
							System.getProperty(
								"frozen.lockfile", Boolean.TRUE.toString())));
				}

			});
	}

}