/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.internal;

import com.liferay.gradle.plugins.node.internal.util.NodePluginUtil;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.Validator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.process.ExecSpec;

/**
 * @author Seiphon Wang
 */
public class PnpmExecutor {

	public PnpmExecutor(Project project) {
		_project = project;

		_workingDir = project.getProjectDir();
	}

	public PnpmExecutor args(Iterable<?> args) {
		GUtil.addToCollection(_args, args);

		return this;
	}

	public PnpmExecutor args(Object... args) {
		return args(Arrays.asList(args));
	}

	public PnpmExecutor environment(Map<?, ?> environment) {
		_environment.putAll(environment);

		return this;
	}

	public PnpmExecutor environment(Object key, Object value) {
		_environment.put(key, value);

		return this;
	}

	public String execute() throws Exception {
		File workingDir = getWorkingDir();

		workingDir.mkdir();

		if (isUseGradleExec()) {
			return _executeGradleExec();
		}

		return _executeProcessBuilder();
	}

	public List<Object> getArgs() {
		return _args;
	}

	public String getCommand() {
		return GradleUtil.toString(_command);
	}

	public Map<?, ?> getEnvironment() {
		return _environment;
	}

	public File getNodeDir() {
		return GradleUtil.toFile(_project, _nodeDir);
	}

	public File getWorkingDir() {
		return GradleUtil.toFile(_project, _workingDir);
	}

	public boolean isUseGradleExec() {
		return _useGradleExec;
	}

	public void setArgs(Iterable<?> args) {
		_args.clear();

		args(args);
	}

	public void setArgs(Object... args) {
		setArgs(Arrays.asList(args));
	}

	public void setCommand(Object command) {
		_command = command;
	}

	public void setEnvironment(Map<?, ?> environment) {
		_environment.clear();

		environment(environment);
	}

	public void setNodeDir(Object nodeDir) {
		_nodeDir = nodeDir;
	}

	public void setUseGradleExec(boolean useGradleExec) {
		_useGradleExec = useGradleExec;
	}

	private String _executeGradleExec() {
		final ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		_project.exec(
			new Action<ExecSpec>() {

				@Override
				public void execute(ExecSpec execSpec) {
					execSpec.setCommandLine(_getCommandLine());
					execSpec.setEnvironment(
						_getEnvironment(execSpec.getEnvironment()));
					execSpec.setErrorOutput(
						new TeeOutputStream(byteArrayOutputStream, System.out));
					execSpec.setStandardOutput(
						new TeeOutputStream(byteArrayOutputStream, System.out));
					execSpec.setWorkingDir(getWorkingDir());
				}

			});

		String result = byteArrayOutputStream.toString();

		return result.trim();
	}

	private String _executeProcessBuilder() throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder(_getCommandLine());

		processBuilder.directory(getWorkingDir());
		processBuilder.redirectErrorStream(true);

		_updateEnvironment(processBuilder.environment());

		if (_logger.isInfoEnabled()) {
			_logger.info(
				"Running {} from {} with environment variables {}",
				processBuilder.command(), processBuilder.directory(),
				processBuilder.environment());
		}

		Process process = processBuilder.start();

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));

		StringBuilder sb = new StringBuilder();

		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);

			sb.append(line + System.lineSeparator());
		}

		int exitValue = process.waitFor();

		if (exitValue != 0) {
			throw new IOException(
				"Process '" + processBuilder.command() +
					"' finished with non-zero exit value " + exitValue);
		}

		String result = sb.toString();

		return result.trim();
	}

	private List<String> _getCommandLine() {
		List<String> commandLine = new ArrayList<>();

		if (OSDetector.isWindows()) {
			commandLine.add("cmd");
			commandLine.addAll(_getWindowsArgs());
		}
		else {
			commandLine.add(_getExecutable());
			commandLine.addAll(GradleUtil.toStringList(getArgs()));
		}

		return commandLine;
	}

	private Map<String, String> _getEnvironment(Map<?, ?> environment) {
		Map<String, String> newEnvironment = new HashMap<>();

		GUtil.addToMap(newEnvironment, environment);

		_updateEnvironment(newEnvironment);

		return newEnvironment;
	}

	private String _getExecutable() {
		String executable = GradleUtil.toString(_command);

		if (OSDetector.isWindows()) {
			executable += "-win-x64.exe";
		}
		else if (OSDetector.isAppleARM()) {
			executable += "-macos-arm64";
		}
		else if (OSDetector.isApple()) {
			executable += "-macos-x64";
		}
		else {
			executable += "-linux-x64";
		}

		File executableDir = _getExecutableDir();

		if (executableDir != null) {
			File executableFile = new File(executableDir, executable);

			if (executableFile.exists() && !OSDetector.isWindows()) {
				Set<PosixFilePermission> permissions = new HashSet<>();

				permissions.add(PosixFilePermission.GROUP_EXECUTE);
				permissions.add(PosixFilePermission.OTHERS_EXECUTE);
				permissions.add(PosixFilePermission.OWNER_EXECUTE);
				permissions.add(PosixFilePermission.OWNER_READ);
				permissions.add(PosixFilePermission.OWNER_WRITE);

				try {
					Files.setPosixFilePermissions(
						executableFile.toPath(), permissions);
				}
				catch (IOException ioException) {
					new IOException(
						"There is no execute permission for executable file " +
							executableFile.getAbsolutePath());
				}
			}

			executable = executableFile.getAbsolutePath();
		}

		return executable;
	}

	private File _getExecutableDir() {
		File nodeDir = getNodeDir();

		if (nodeDir == null) {
			return null;
		}

		return NodePluginUtil.getBinDir(NodePluginUtil.getPnpmDir(nodeDir));
	}

	private List<String> _getWindowsArgs() {
		List<String> windowsArgs = new ArrayList<>(2);

		windowsArgs.add("/c");

		StringBuilder sb = new StringBuilder();

		sb.append('"');

		String executable = _getExecutable();

		if (executable.indexOf(File.separatorChar) == -1) {
			sb.append(executable);
		}
		else {
			sb.append('"');
			sb.append(executable);
			sb.append('"');
		}

		List<String> args = GradleUtil.toStringList(getArgs());

		for (String arg : args) {
			sb.append(" \"");

			if (Validator.isNotNull(arg)) {
				sb.append(arg);
			}

			sb.append('"');
		}

		sb.append('"');

		windowsArgs.add(sb.toString());

		return windowsArgs;
	}

	private void _updateEnvironment(Map<String, String> environment) {
		GUtil.addToMap(environment, getEnvironment());

		File executableDir = _getExecutableDir();

		if (executableDir != null) {
			for (String pathKey : _PATH_KEYS) {
				String path = environment.get(pathKey);

				if (Validator.isNull(path)) {
					continue;
				}

				path =
					executableDir.getAbsolutePath() + File.pathSeparator + path;

				environment.put(pathKey, path);
			}
		}
	}

	private static final String[] _PATH_KEYS = {"Path", "PATH"};

	private static final Logger _logger = Logging.getLogger(PnpmExecutor.class);

	private final List<Object> _args = new ArrayList<>();
	private Object _command = "pnpm";
	private final Map<Object, Object> _environment = new LinkedHashMap<>();
	private Object _nodeDir;
	private final Project _project;
	private boolean _useGradleExec;
	private final Object _workingDir;

	private static class TeeOutputStream extends OutputStream {

		public TeeOutputStream(
			OutputStream outputStream1, OutputStream outputStream2) {

			_outputStream1 = outputStream1;
			_outputStream2 = outputStream2;
		}

		@Override
		public void close() throws IOException {
			try {
				_outputStream1.close();
			}
			finally {
				_outputStream2.close();
			}
		}

		@Override
		public void flush() throws IOException {
			_outputStream1.flush();
			_outputStream2.flush();
		}

		@Override
		public synchronized void write(byte[] bytes) throws IOException {
			_outputStream1.write(bytes);
			_outputStream2.write(bytes);
		}

		@Override
		public synchronized void write(byte[] bytes, int offset, int length)
			throws IOException {

			_outputStream1.write(bytes, offset, length);
			_outputStream2.write(bytes, offset, length);
		}

		@Override
		public synchronized void write(int b) throws IOException {
			_outputStream1.write(b);
			_outputStream2.write(b);
		}

		private final OutputStream _outputStream1;
		private final OutputStream _outputStream2;

	}

}