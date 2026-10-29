/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.test.integration.task;

import com.liferay.gradle.plugins.test.integration.internal.util.GradleUtil;

import java.io.File;
import java.io.OutputStream;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import org.zeroturnaround.exec.StartedProcess;

/**
 * @author Andrea Di Giorgi
 */
public class StartTestableTomcatTask extends StartAppServerTask {

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getLicenseFile() {
		return GradleUtil.toFile(getProject(), _licenseFile);
	}

	@Internal
	public File getLiferayHome() {
		return GradleUtil.toFile(getProject(), _liferayHome);
	}

	@Input
	public boolean isDeleteLiferayHome() {
		return _deleteLiferayHome;
	}

	public void setDeleteLiferayHome(boolean deleteLiferayHome) {
		_deleteLiferayHome = deleteLiferayHome;
	}

	public void setLicenseFile(Object licenseFile) {
		_licenseFile = licenseFile;
	}

	public void setLiferayHome(Object liferayHome) {
		_liferayHome = liferayHome;
	}

	@Override
	public void startAppServer() throws Exception {
		if (isDeleteLiferayHome()) {
			_deleteLiferayHome();
		}

		_deployLicense();

		super.startAppServer();
	}

	@Override
	protected void waitForStarted(
		StartedProcess startedProcess, OutputStream outputStream) {

		waitFor(
			new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return isReachable();
				}

			});

		super.waitForStarted(startedProcess, outputStream);
	}

	private void _deleteLiferayHome() {
		File liferayHome = getLiferayHome();

		if (liferayHome == null) {
			throw new InvalidUserDataException(
				"No value has been specified for property 'liferayHome'");
		}

		Project project = getProject();

		project.delete(
			new File(liferayHome, "data"), new File(liferayHome, "logs"),
			new File(liferayHome, "osgi/state"),
			new File(liferayHome, "portal-setup-wizard.properties"));
	}

	private void _deployLicense() {
		final File licenseFile = getLicenseFile();

		if (licenseFile == null) {
			return;
		}

		final File liferayHome = getLiferayHome();

		if (liferayHome == null) {
			return;
		}

		Project project = getProject();

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(licenseFile);
					copySpec.into(new File(liferayHome, "deploy"));
				}

			});
	}

	private boolean _deleteLiferayHome = true;
	private Object _licenseFile;
	private Object _liferayHome;

}