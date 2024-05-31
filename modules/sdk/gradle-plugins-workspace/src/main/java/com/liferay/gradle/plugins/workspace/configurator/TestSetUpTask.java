package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.internal.TaskOutputsInternal;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Drew Brokke
 */
public class TestSetUpTask extends DefaultTask {

	public TestSetUpTask() {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		_expectedLogOutput = objects.property(String.class);
		_outputGlobs = objects.setProperty(String.class);
		_taskPaths = objects.setProperty(String.class);

		onlyIf(
			task -> {
				if (_expectedLogOutput.isPresent()) {
					return true;
				}

				return false;
			});

		project.afterEvaluate(
			project1 -> {
				TaskOutputs taskOutputs = getOutputs();

				if (_taskPaths.isPresent()) {
					Set<String> objects1 = _taskPaths.get();

					TaskContainer taskContainer = project1.getTasks();

					Task previous = null;

					for (String taskPath : objects1) {
						Task task = taskContainer.getByPath(taskPath);

						dependsOn(task);

						taskOutputs.files(task);

						if (previous != null) {
							task.mustRunAfter(previous);
						}

						previous = task;
					}
				}

				if (_outputGlobs.isPresent()) {
					WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
						project1.getGradle(), WorkspaceExtension.class);

					taskOutputs.files(
						project.fileTree(
							workspaceExtension.getHomeDir(),
							configurableFileTree -> {
								Set<String> strings = _outputGlobs.get();

								strings.forEach(configurableFileTree::include);
							}));
				}

			}
		);
		_startServer = objects.property(Boolean.class);
	}

	@TaskAction
	public void checkLogs() throws Exception {
		int max = 0;
		File logFile = null;

		Project project = getProject();

		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			project.getGradle(), WorkspaceExtension.class);

		ConfigurableFileTree files = project.fileTree(
			workspaceExtension.getHomeDir(),
			configurableFileTree -> configurableFileTree.include(
				"logs/liferay.*.log"));

		for (File file : files.getFiles()) {
			String name = file.getName();

			String[] fileNameParts = name.split("\\.");

			String datePart = fileNameParts[1];

			int date = Integer.parseInt(datePart.replaceAll("-", ""));

			if (date > max) {
				logFile = file;
			}
		}

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(
				logFile, "r")) {

			long length = randomAccessFile.length();

			randomAccessFile.skipBytes((int)length);

			String testLine = _expectedLogOutput.get();

			System.out.println("Waiting for expected output: ${testLine}");

			int checkInterval = 3 * 1000;
			int timeout = 2 * 60 * 1000;

			GradleUtil.waitFor(
				() -> {
					String line = randomAccessFile.readLine();

					while (line != null) {
						if (line.contains(testLine)) {
							System.out.println("FOUND!");

							return true;
						}

						line = randomAccessFile.readLine();
					}

					return false;
				},
				checkInterval, timeout);
		}
	}

	@Input
	public Property<String> getExpectedLogOutput() {
		return _expectedLogOutput;
	}

	private final Property<String> _expectedLogOutput;

	@Input
	public SetProperty<String> getOutputGlobs() {
		return _outputGlobs;
	}

	private final SetProperty<String> _outputGlobs;

	@Input
	public SetProperty<String> getTaskPaths() {
		return _taskPaths;
	}

	private final SetProperty<String> _taskPaths;

	@Input
	public Property<Boolean> getStartServer() {
		return _startServer;
	}

	private final Property<Boolean> _startServer;

}