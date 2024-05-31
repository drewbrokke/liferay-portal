package com.liferay.gradle.plugins.workspace.testing.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Drew Brokke
 */
public class ExecuteAndWaitForTask extends DefaultTask {

	public ExecuteAndWaitForTask() {
		Project project = getProject();

		ObjectFactory objects = project.getObjects();

		ProjectLayout layout = project.getLayout();

		DirectoryProperty buildDirectory = layout.getBuildDirectory();

		_pidFile = buildDirectory.file(String.format("%s/pid", getName()));
	}

	@OutputFile
	public Provider<RegularFile> getPidFile() {
		return _pidFile;
	}

	private final Provider<RegularFile> _pidFile;

	@TaskAction
	public void run() throws IOException {
		RegularFile regularFile = _pidFile.get();

		File pidFile = regularFile.getAsFile();

		Files.write(pidFile.toPath(), "foo bar".getBytes());

		System.out.println("pidFile = " + pidFile);
	}

}
