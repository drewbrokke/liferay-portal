package com.liferay.gradle.plugins.workspace.testing.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.listener.ProcessListener;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.exec.stream.slf4j.Slf4jOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Drew Brokke
 */
public class ExecuteAndWaitForTask extends DefaultTask {

	@OutputFile
	public Provider<RegularFile> getStdoutFile() {
		return _stdoutFile;
	}

	private final Provider<RegularFile> _stdoutFile;

	@Input
	public Property<Integer> getWaitForTimeout() {
		return _waitForTimeout;
	}

	private final Property<Integer> _waitForTimeout;

	@Input
	public SetProperty<String> getExecArgs() {
		return _execArgs;
	}

	private final SetProperty<String> _execArgs;

	public ExecuteAndWaitForTask() {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		ProjectLayout layout = project.getLayout();

		DirectoryProperty buildDirectory = layout.getBuildDirectory();

		_pidFile = buildDirectory.file(String.format("%s/pid", getName()));
		_stdoutFile =
			buildDirectory.file(String.format("%s/stdout.txt", getName()));
		_expectedOutput = objects.property(String.class);
		_execArgs = objects.setProperty(String.class);

		_waitForTimeout = objects.property(Integer.class);

		_waitForTimeout.set(30 * 1000);
	}

	@OutputFile
	public Provider<RegularFile> getPidFile() {
		return _pidFile;
	}

	@Input
	public Provider<String> getExpectedOutput() {
		return _expectedOutput;
	}

	private final Property<String> _expectedOutput;

	private final Provider<RegularFile> _pidFile;


	@TaskAction
	public void run() throws Exception {
		String expectedOutput = _expectedOutput.get();
		RegularFile pidRegularFile = _pidFile.get();
		RegularFile stdoutRegularFile = _stdoutFile.get();
		Integer timeout = _waitForTimeout.get();

		File pidFile = pidRegularFile.getAsFile();
		File stdoutFile = stdoutRegularFile.getAsFile();

		Files.write(pidFile.toPath(), "foo bar".getBytes());

		System.out.println("pidFile = " + pidFile);

		ProcessExecutor processExecutor =
			new ProcessExecutor(_execArgs.get());

		CountDownLatch countDownLatch = new CountDownLatch(1);

		processExecutor.redirectOutputAlsoTo(
			new LogOutputStream() {
				@Override
				protected void processLine(String line) {
					if (countDownLatch.getCount() == 0) {
						return;
					}

					if (line.contains(expectedOutput)) {
						countDownLatch.countDown();
					}
				}
			}
		);

		processExecutor.redirectOutputAlsoTo(
			Files.newOutputStream(stdoutFile.toPath()));

		StartedProcess startedProcess = processExecutor.start();

		Process process = startedProcess.getProcess();

//		TODO record pid to file

		boolean await = countDownLatch.await(
			timeout, TimeUnit.MILLISECONDS);

		if (!await) {
			process.destroyForcibly();

			throw new GradleException("Could not find the expected output");
		}
	}

}
