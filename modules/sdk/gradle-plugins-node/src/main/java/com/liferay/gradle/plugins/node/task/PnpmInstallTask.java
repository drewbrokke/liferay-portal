/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.task;

import com.liferay.gradle.plugins.node.internal.util.FileUtil;
import com.liferay.gradle.plugins.node.internal.util.GradleUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Project;
import org.gradle.api.tasks.Input;

/**
 * @author Seiphon Wang
 */
public class PnpmInstallTask extends ExecutePnpmTask {

	@Override
	public void executePnpm() throws Exception {
		File npmrcFile = _getNpmrcFile();

		if (!npmrcFile.exists()) {
			_createNpmrcFile(npmrcFile);
		}

		executePnpmInstall(false);
	}

	@Input
	public boolean isFrozenLockFile() {
		return GradleUtil.toBoolean(_frozenLockFile);
	}

	public void setFrozenLockFile(Object frozenLockFile) {
		_frozenLockFile = frozenLockFile;
	}

	protected void executePnpmInstall(boolean reset) throws Exception {
		_addPnpmWorkspaceYamlFile(getProject());

		super.executePnpm();
	}

	@Override
	protected List<String> getCompleteArgs() {
		List<String> completeArgs = super.getCompleteArgs();

		completeArgs.add("install");

		if (isFrozenLockFile()) {
			completeArgs.add("--frozen-lockfile");
		}

		return completeArgs;
	}

	private void _addPnpmWorkspaceYamlFile(Project project) throws Exception {
		File pnpmWorkspaceYamlFile = project.file("pnpm-workspace.yaml");

		if (!pnpmWorkspaceYamlFile.exists()) {
			pnpmWorkspaceYamlFile = new File(
				project.getRootDir(), "pnpm-workspace.yaml");
		}

		List<String> contents = new ArrayList<>();

		contents.add("packages:");
		contents.add("  # all packages in subdirs of modules/");
		contents.add("  - 'modules/**'");
		contents.add("  # all packages in direct subdirs of themes/");
		contents.add("  - 'themes/*'");
		contents.add("  # exclude packages that are inside test directories");
		contents.add("  - '!**/test/**'");

		FileUtil.write(pnpmWorkspaceYamlFile, contents);
	}

	private void _createNpmrcFile(File npmrcFile) throws Exception {
		List<String> contents = new ArrayList<>();

		contents.add("store-dir = ./node_modules_cache");

		FileUtil.write(npmrcFile, contents);
	}

	private File _getNpmrcFile() {
		return new File(getWorkingDir(), ".npmrc");
	}

	private Object _frozenLockFile;

}