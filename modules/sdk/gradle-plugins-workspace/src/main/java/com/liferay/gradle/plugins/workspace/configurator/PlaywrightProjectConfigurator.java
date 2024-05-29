package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.internal.TestIntegrationDefaultsPlugin;
import com.liferay.gradle.plugins.node.task.PackageRunTask;
import com.liferay.gradle.plugins.test.integration.TestIntegrationPlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationTomcatExtension;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceNodePlugin;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.TaskContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * @author Drew Brokke
 */
public class PlaywrightProjectConfigurator extends BaseProjectConfigurator {

	public PlaywrightProjectConfigurator(Settings settings) {
		super(settings);
	}

	@Override
	protected Iterable<File> doGetProjectDirs(File rootDir) throws Exception {
		ArrayList<File> projectDirs = new ArrayList<>();

		Files.walkFileTree(
			rootDir.toPath(),
			new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(
					Path dir, BasicFileAttributes attrs) throws IOException {

					if (Files.exists(dir.resolve("playwright.config.ts"))) {
						projectDirs.add(dir.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return super.preVisitDirectory(dir, attrs);
				}
			}
		);

		return projectDirs;
	}

	@Override
	public String getName() {
		return NAME;
	}

	protected static final String NAME = "playwright";

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
				project,
				TestIntegrationTomcatExtension.class);

		WorkspaceExtension workspaceExtension =
			GradleUtil.getExtension(
				project.getGradle(), WorkspaceExtension.class);

		testIntegrationTomcatExtension.setDir(
			(Callable<File>) () -> new File(
				workspaceExtension.getHomeDir(), "tomcat"));

		LiferayWorkspaceNodePlugin.INSTANCE.apply(project);

		// set up package run task

		TaskContainer taskContainer = project.getTasks();

		Task stopTestableTomcatTask = taskContainer.getByName(
			TestIntegrationPlugin.STOP_TESTABLE_TOMCAT_TASK_NAME);

		taskContainer.withType(
			PackageRunTask.class,
			packageRunTask -> {
				System.out.println(
					"packageRunTask.getPath() = " + packageRunTask.getPath());

				if (packageRunTask.getName().startsWith("packageRunTest")) {
					System.out.println("This one is a test");

					packageRunTask.dependsOn(TestIntegrationPlugin.START_TESTABLE_TOMCAT_TASK_NAME);

					stopTestableTomcatTask.mustRunAfter(packageRunTask);
				}
			}
		);
	}

}
