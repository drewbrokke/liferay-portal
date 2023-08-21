/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.task;

import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.internal.PnpmExecutor;
import com.liferay.gradle.plugins.node.internal.util.FileUtil;
import com.liferay.gradle.plugins.node.internal.util.GradleUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Seiphon Wang
 */
public class ExecutePnpmTask extends DefaultTask {

	public ExecutePnpmTask() {
		_pnpmExecutor = new PnpmExecutor(getProject());

		dependsOn(NodePlugin.DOWNLOAD_NODE_TASK_NAME);
	}

	public ExecutePnpmTask args(Iterable<?> args) {
		_pnpmExecutor.args(args);

		return this;
	}

	public ExecutePnpmTask args(Object... args) {
		_pnpmExecutor.args(args);

		return this;
	}

	public ExecutePnpmTask environment(Map<?, ?> environment) {
		_pnpmExecutor.environment(environment);

		return this;
	}

	public ExecutePnpmTask environment(Object key, Object value) {
		_pnpmExecutor.environment(key, value);

		return this;
	}

	@TaskAction
	public void executeNode() throws Exception {
		setArgs(getCompleteArgs());

		int pnpmInstallRetries = getNpmInstallRetries();

		PnpmInstallTask pnpmInstallTask = GradleUtil.fetchTask(
			getProject(), "pnpmInstall", PnpmInstallTask.class);

		if ((pnpmInstallRetries <= 0) || (pnpmInstallTask == null)) {
			_result = _pnpmExecutor.execute();

			return;
		}

		Logger logger = getLogger();

		for (int i = 1; i <= pnpmInstallRetries; i++) {
			try {
				_result = _pnpmExecutor.execute();

				break;
			}
			catch (IOException ioException) {
				if (i == pnpmInstallRetries) {
					throw ioException;
				}

				if (logger.isWarnEnabled()) {
					logger.warn(
						ioException.getMessage() +
							". Running \"pnpm install\" again");
				}

				pnpmInstallTask.executePnpmInstall(true);
			}
		}
	}

	@Input
	@Optional
	public List<Object> getArgs() {
		return _pnpmExecutor.getArgs();
	}

	@Input
	@Optional
	public String getCommand() {
		return _pnpmExecutor.getCommand();
	}

	@Input
	@Optional
	public Map<?, ?> getEnvironment() {
		return _pnpmExecutor.getEnvironment();
	}

	@Input
	public int getNpmInstallRetries() {
		return _pnpmInstallRetries;
	}

	@Internal
	public String getResult() {
		if (_result == null) {
			return "";
		}

		return _result;
	}

	@Internal
	public File getScriptFile() {
		File file = GradleUtil.toFile(getProject(), _scriptFile);

		if (file == null) {
			return null;
		}

		return file;
	}

	@Internal
	public File getWorkingDir() {
		return _pnpmExecutor.getWorkingDir();
	}

	public void setArgs(Iterable<?> args) {
		_pnpmExecutor.setArgs(args);
	}

	public void setArgs(Object... args) {
		_pnpmExecutor.setArgs(args);
	}

	public void setCommand(Object command) {
		_pnpmExecutor.setCommand(command);
	}

	public void setEnvironment(Map<?, ?> environment) {
		_pnpmExecutor.setEnvironment(environment);
	}

	@Internal
	protected List<String> getCompleteArgs() {
		File scriptFile = getScriptFile();

		List<String> args = GradleUtil.toStringList(getArgs());

		if (scriptFile == null) {
			return args;
		}

		List<String> completeArgs = new ArrayList<>();

		completeArgs.add(FileUtil.getAbsolutePath(scriptFile));

		completeArgs.addAll(args);

		return completeArgs;
	}

	private final PnpmExecutor _pnpmExecutor;
	private int _pnpmInstallRetries;
	private String _result;
	private Object _scriptFile;

}