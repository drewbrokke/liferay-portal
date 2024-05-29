package com.liferay.gradle.plugins.workspace.configurator;

import org.gradle.api.Project;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.initialization.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

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
		System.out.println("let's go! " + project.getPath());
	}
}
