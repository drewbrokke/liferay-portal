/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node;

import com.liferay.gradle.plugins.node.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.task.PnpmInstallTask;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Seiphon Wang
 */
public class PnpmPlugin implements Plugin<Project> {

	public static final String PNPM_INSTALL_TASK_NAME = "pnpmInstall";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, NodePlugin.class);

		TaskProvider<PnpmInstallTask> yarnInstallTaskProvider =
			GradleUtil.addTaskProvider(
				project, PNPM_INSTALL_TASK_NAME, PnpmInstallTask.class);

		_configureTaskPnpmInstallProvider(yarnInstallTaskProvider);
	}

	private void _configureTaskPnpmInstallProvider(
		TaskProvider<PnpmInstallTask> pnpmInstallTaskProvider) {

		pnpmInstallTaskProvider.configure(
			new Action<PnpmInstallTask>() {

				@Override
				public void execute(PnpmInstallTask pnpmInstallTask) {
					pnpmInstallTask.setDescription(
						"Installs Node packages from package.json.");
				}

			});
	}

}