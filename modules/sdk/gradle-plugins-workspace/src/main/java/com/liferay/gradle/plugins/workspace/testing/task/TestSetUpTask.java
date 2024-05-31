package com.liferay.gradle.plugins.workspace.testing.task;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
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

		_outputGlobs = objects.setProperty(String.class);
		_taskPaths = objects.setProperty(String.class);

		project.afterEvaluate(
			project1 -> {
				if (_taskPaths.isPresent()) {
					Task previousTask = null;
					TaskContainer taskContainer = project.getTasks();

					for (String taskPath : _taskPaths.get()) {
						Task task = taskContainer.findByPath(taskPath);

						if (task == null) {
							continue;
						}

						dependsOn(task);

						if (previousTask != null) {
							task.mustRunAfter(previousTask);
						}

						previousTask = task;
					}
				}

				if (_outputGlobs.isPresent()) {
					TaskOutputs taskOutputs = getOutputs();

					WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
						project1.getGradle(), WorkspaceExtension.class);

					taskOutputs.files(
						project.fileTree(
							workspaceExtension.getHomeDir(),
							configurableFileTree -> {
								for (String outputGlob : _outputGlobs.get()) {
									configurableFileTree.include(outputGlob);
								}
							}));
				}

			}
		);
		_startServer = objects.property(Boolean.class);
		_startServer.set(false);
	}

	@TaskAction
	public void foo() {

	}

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