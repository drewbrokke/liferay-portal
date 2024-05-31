package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.internal.TestIntegrationDefaultsPlugin;
import com.liferay.gradle.plugins.node.task.PackageRunTask;
import com.liferay.gradle.plugins.test.integration.TestIntegrationPlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationTomcatExtension;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceNodePlugin;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Drew Brokke
 */
public class PlaywrightProjectConfigurator extends BaseProjectConfigurator {

	public PlaywrightProjectConfigurator(Settings settings) {
		super(settings);
	}

	@Override
	public void apply(Project project) {

		// TODO make this a method

		boolean isDefaultRepositoryEnabled = true;

		if (isDefaultRepositoryEnabled) {
			GradleUtil.addDefaultRepositories(project);
		}

		GradleUtil.applyPlugin(project, LiferayBasePlugin.class);
		GradleUtil.applyPlugin(project, TestIntegrationPlugin.class);

		TestIntegrationDefaultsPlugin.INSTANCE.apply(project);

		TestIntegrationTomcatExtension testIntegrationTomcatExtension =
			GradleUtil.getExtension(
				project, TestIntegrationTomcatExtension.class);

		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			project.getGradle(), WorkspaceExtension.class);

		testIntegrationTomcatExtension.setDir(
			(Callable<File>)() -> new File(
				workspaceExtension.getHomeDir(), "tomcat"));

		LiferayWorkspaceNodePlugin.INSTANCE.apply(project);

		// set up package run task

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PackageRunTask.class,
			packageRunTask -> {
				String name = packageRunTask.getName();

				if (name.startsWith("packageRunTest")) {
					packageRunTask.dependsOn(
						TestIntegrationPlugin.START_TESTABLE_TOMCAT_TASK_NAME);

					_configureSetUpTearDown(project, packageRunTask);
				}
			});
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected Iterable<File> doGetProjectDirs(File rootDir) throws Exception {
		ArrayList<File> projectDirs = new ArrayList<>();

		Files.walkFileTree(
			rootDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path directoryPath, BasicFileAttributes attrs)
					throws IOException {

					if (Files.exists(
							directoryPath.resolve("playwright.config.ts"))) {

						projectDirs.add(directoryPath.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return super.preVisitDirectory(directoryPath, attrs);
				}

			});

		return projectDirs;
	}

	protected static final String NAME = "playwright";

	private void _configureSetUpTask(Project project, TestSetUpTask setUpTask) {
		project.afterEvaluate(
			project1 -> {
				Property<Boolean> startServerProperty =
					setUpTask.getStartServer();

				if (startServerProperty.getOrElse(false)) {
					setUpTask.mustRunAfter(
						TestIntegrationPlugin.START_TESTABLE_TOMCAT_TASK_NAME);
				}
			}
		);
	}

	private void _configureSetUpTearDown(
		Project project, PackageRunTask packageRunTask) {

		String capitalizedTaskName = StringUtil.capitalize(
			packageRunTask.getName());

		String setUpTaskName = "setUp" + capitalizedTaskName;
		String tearDownTaskName = "tearDown" + capitalizedTaskName;

		TestSetUpTask setUpTask = GradleUtil.addTask(
			project, setUpTaskName, TestSetUpTask.class);

		packageRunTask.dependsOn(setUpTask);

		_configureSetUpTask(project, setUpTask);

		DefaultTask tearDownTask = GradleUtil.addTask(
			project, tearDownTaskName, DefaultTask.class);

		packageRunTask.finalizedBy(tearDownTask);

		_configureTearDownTask(project, setUpTask, tearDownTask);
	}

	private void _configureTearDownTask(
		Project project, TestSetUpTask setUpTask, DefaultTask tearDownTask) {

		String cleanSetUpTaskName = "clean" + StringUtil.capitalize(setUpTask.getName());

		tearDownTask.dependsOn(cleanSetUpTaskName);

		project.afterEvaluate(
			project1 -> {
				Property<Boolean> startServerProperty =
					setUpTask.getStartServer();

				if (startServerProperty.getOrElse(false)) {
					Task stopTestableTomcatTask = GradleUtil.getTask(
						project, TestIntegrationPlugin.STOP_TESTABLE_TOMCAT_TASK_NAME);

					stopTestableTomcatTask.mustRunAfter(tearDownTask);
				}
			}
		);
	}

}