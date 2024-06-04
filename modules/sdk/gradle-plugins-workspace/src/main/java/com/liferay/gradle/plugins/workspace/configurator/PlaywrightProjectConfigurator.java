package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.internal.TestIntegrationDefaultsPlugin;
import com.liferay.gradle.plugins.node.task.PackageRunTask;
import com.liferay.gradle.plugins.test.integration.TestIntegrationPlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationTomcatExtension;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceNodePlugin;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceServerPlugin;
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
import java.util.concurrent.TimeUnit;

import com.liferay.gradle.plugins.workspace.testing.task.ExecuteAndWaitForTask;
import com.liferay.gradle.plugins.workspace.testing.task.TestSetUpTask;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFile;
import org.gradle.api.initialization.Settings;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;

/**
 * @author Drew Brokke
 */
public class PlaywrightProjectConfigurator extends BaseProjectConfigurator {

	public PlaywrightProjectConfigurator(Settings settings) {
		super(settings);
	}

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, LifecycleBasePlugin.class);
		GradleUtil.applyPlugin(project, LiferayWorkspaceServerPlugin.class);
		LiferayWorkspaceNodePlugin.INSTANCE.apply(project);

		// set up package run task

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PackageRunTask.class,
			packageRunTask -> {
				String name = packageRunTask.getName();

				if (name.startsWith("packageRunTest")) {
					_configureSetUpTearDown(project, packageRunTask);
				}
			});

		taskContainer.withType(
			ExecuteAndWaitForTask.class,
			executeAndWaitForTask -> {
				String cleanTaskName =
					_getPrependedTaskName("clean", executeAndWaitForTask);

				Task cleanTask = taskContainer.findByPath(cleanTaskName);

				if (cleanTask == null) {
					return;
				}

				Provider<RegularFile> pidFileProvider =
					executeAndWaitForTask.getPidFile();

				RegularFile regularFile = pidFileProvider.get();

				File pidFile = regularFile.getAsFile();

				cleanTask.onlyIf(cleanTask1 -> pidFile.exists());

				cleanTask.doFirst(cleanTask1 -> {
					try {
						int pid = Integer.parseInt(new String(
							Files.readAllBytes(pidFile.toPath())));

						PidProcess process = Processes.newPidProcess(pid);

						if (!process.isAlive()) {
							System.out.println("process is already stopped");

							return;
						}

						ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 30, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);

						System.out.println("terminated process = " + process.getDescription() + " " + process.getPid());

					}
					catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				});
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

				if (startServerProperty.get()) {
//					setUpTask.mustRunAfter(
//						TestIntegrationPlugin.START_TESTABLE_TOMCAT_TASK_NAME);
				}
			}
		);
	}

	private void _configureSetUpTearDown(
		Project project, PackageRunTask packageRunTask) {

		TestSetUpTask setUpTask = GradleUtil.addTask(
			project, _getPrependedTaskName("setUp", packageRunTask),
			TestSetUpTask.class);

		packageRunTask.dependsOn(setUpTask);

		_configureSetUpTask(project, setUpTask);

		DefaultTask tearDownTask = GradleUtil.addTask(
			project, _getPrependedTaskName("tearDown", packageRunTask),
			DefaultTask.class);

		packageRunTask.finalizedBy(tearDownTask);

		_configureTearDownTask(project, setUpTask, tearDownTask);
	}

	private void _configureTearDownTask(
		Project project, TestSetUpTask setUpTask, DefaultTask tearDownTask) {

		String cleanSetUpTaskName = _getPrependedTaskName("clean", setUpTask);

		tearDownTask.dependsOn(cleanSetUpTaskName);

		project.afterEvaluate(
			project1 -> {
				Property<Boolean> startServerProperty =
					setUpTask.getStartServer();

				if (startServerProperty.get()) {
//					Task stopTestableTomcatTask = GradleUtil.getTask(
//						project, TestIntegrationPlugin.STOP_TESTABLE_TOMCAT_TASK_NAME);
//
//					stopTestableTomcatTask.mustRunAfter(tearDownTask);
				}

				TaskContainer taskContainer = project1.getTasks();

				taskContainer.withType(
					ExecuteAndWaitForTask.class,
					executeAndWaitTask -> {


						tearDownTask.dependsOn(_getPrependedTaskName("clean", executeAndWaitTask));
					}
				);
			}
		);
	}

	private String _getPrependedTaskName(String prefix, Task task) {
		return prefix + StringUtil.capitalize(task.getName());
	}

}