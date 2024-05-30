package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;
import java.io.RandomAccessFile;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

/**
 * @author Drew Brokke
 */
public class TestSetUpTask extends DefaultTask {

	public TestSetUpTask() {
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
	}

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

	public Property<String> getExpectedLogOutput() {
		return _expectedLogOutput;
	}

	@Input
	private final Property<String> _expectedLogOutput;

}