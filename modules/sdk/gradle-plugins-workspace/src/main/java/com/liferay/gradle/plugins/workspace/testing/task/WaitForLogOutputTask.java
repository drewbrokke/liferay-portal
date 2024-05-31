package com.liferay.gradle.plugins.workspace.testing.task;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Drew Brokke
 */
public class WaitForLogOutputTask extends DefaultTask {

	public WaitForLogOutputTask() {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		_expectedLogOutput = objects.property(String.class);

		onlyIf(
			task -> {
				if (_expectedLogOutput.isPresent()) {
					return true;
				}

				return false;
			});

		_logFile = objects.fileProperty();

		ProjectLayout layout = project.getLayout();

		_logFile.set(layout.file(project.provider(this::_getBundleServerFile)));
	}

	private File _getBundleServerFile() {
		File logFile = null;

		int max = 0;

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

		return logFile;
	}

	@TaskAction
	public void checkLogs() throws Exception {
		RegularFile regularFile = _logFile.get();

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(
			regularFile.getAsFile(), "r")) {

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



	@InputFile
	public RegularFileProperty getLogFile() {
		return _logFile;
	}

	private final RegularFileProperty _logFile;

}