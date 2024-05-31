package com.liferay.gradle.plugins.workspace;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author Drew Brokke
 */
public class LiferayWorkspaceServerPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		System.out.printf("Applying the server plugin to project %s%n", project.getPath());
	}
}
