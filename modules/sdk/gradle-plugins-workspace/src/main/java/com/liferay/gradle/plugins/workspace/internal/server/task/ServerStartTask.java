package com.liferay.gradle.plugins.workspace.internal.server.task;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Drew Brokke
 */
public class ServerStartTask extends DefaultTask {

	private final Provider<File> _binDirProvider;
	private final Provider<File> _executableFileProvider;
	private final Provider<File> _catalinaPidFileProvider;
	private final Property<Integer> _intervalProperty;
	private final Property<Integer> _timeoutProperty;

	private String _getExecutableFilename() {
		return "catalina.sh";
	}

	public ServerStartTask() {
		Project project = getProject();

		_executableFileProvider = project.provider(
			this::_getExecutableFile);

		_binDirProvider = _executableFileProvider.map(File::getParentFile);

		_catalinaPidFileProvider = _binDirProvider.map(
			binDir -> new File(binDir, "catalina.pid"));

		onlyIf(task -> {
			if (!_binDirProvider.isPresent()) {
				return false;
			}

			if (_isReachable()) {
				return false;
			}

			return true;
		});

		ObjectFactory objects = project.getObjects();

		_intervalProperty = objects.property(Integer.class);
		_intervalProperty.set(1000);

		_timeoutProperty = objects.property(Integer.class);
		_timeoutProperty.set(120000);
	}

	private File _getExecutableFile() {
		Project project = getProject();

		WorkspaceExtension workspaceExtension =
			GradleUtil.getExtension(project, WorkspaceExtension.class);

		ConfigurableFileTree fileTree = project.fileTree(
			workspaceExtension.getHomeDir(),
			fileTree1 -> fileTree1.include(
				"**/tomcat*/bin/" + _getExecutableFilename()));

		return fileTree.getSingleFile();
	}

	private boolean _isReachable() {
		try {
			System.out.println("Trying to reach server...");
			URL url = new URL( "http://localhost:8080");

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("HEAD");

			int responseCode = httpURLConnection.getResponseCode();

			if ((responseCode > 0) && (responseCode < 400)) {
				System.out.println("Success!");

				return true;
			}
		}
		catch (IOException ioException) {
		}

		return false;
	}

	@TaskAction
	public void startServer() throws Exception {
		Project project = getProject();

		project.exec(
			execSpec -> {
				execSpec.setWorkingDir(_binDirProvider.get());
				execSpec.setExecutable(_executableFileProvider.get());
				execSpec.environment(
					"CATALINA_PID", _catalinaPidFileProvider.get());
			}
		);
		
		GradleUtil.waitFor(
			this::_isReachable, _intervalProperty.get(),
			_timeoutProperty.get());
	}
}