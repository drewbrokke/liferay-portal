/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.gradle.plugins.wsdd.builder;

import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.portal.tools.wsdd.builder.WSDDBuilderArgs;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class BuildWSDDTask extends JavaExec {

	public BuildWSDDTask() {
		Property<String> mainClass = getMainClass();

		mainClass.set("com.liferay.portal.tools.wsdd.builder.WSDDBuilder");
	}

	@Override
	public void exec() {
		setArgs(getCompleteArgs());

		super.exec();
	}

	@Input
	public String getBuilderClasspath() {
		return GradleUtil.toString(_builderClasspath);
	}

	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getInputFile() {
		return GradleUtil.toFile(getProject(), _inputFile);
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getOutputDir() {
		return GradleUtil.toFile(getProject(), _outputDir);
	}

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getServerConfigFile() {
		File file = GradleUtil.toFile(getProject(), _serverConfigFile);

		if (!file.exists()) {
			return null;
		}

		return file;
	}

	@Input
	public String getServiceNamespace() {
		return GradleUtil.toString(_serviceNamespace);
	}

	public void setBuilderClasspath(Object builderClasspath) {
		_builderClasspath = builderClasspath;
	}

	public void setInputFile(Object inputFile) {
		_inputFile = inputFile;
	}

	public void setOutputDir(Object outputDir) {
		_outputDir = outputDir;
	}

	public void setServerConfigFile(Object serverConfigFile) {
		_serverConfigFile = serverConfigFile;
	}

	public void setServiceNamespace(Object serviceNamespace) {
		_serviceNamespace = serviceNamespace;
	}

	@Internal
	protected List<String> getCompleteArgs() {
		List<String> args = new ArrayList<>(getArgs());

		args.add("wsdd.class.path=" + getBuilderClasspath());
		args.add("wsdd.input.file=" + FileUtil.getAbsolutePath(getInputFile()));
		args.add(
			"wsdd.output.path=" + FileUtil.getAbsolutePath(getOutputDir()) +
				"/");

		File serverConfigFile = _getOptionalFile(
			getServerConfigFile(), _serverConfigFile);

		args.add(
			"wsdd.server.config.file=" +
				FileUtil.getAbsolutePath(serverConfigFile));

		args.add("wsdd.service.namespace=" + getServiceNamespace());

		return args;
	}

	private File _getOptionalFile(File file, Object defaultFile) {
		if (file != null) {
			return file;
		}

		return GradleUtil.toFile(getProject(), defaultFile);
	}

	private Object _builderClasspath;
	private Object _inputFile;
	private Object _outputDir;
	private Object _serverConfigFile = WSDDBuilderArgs.SERVER_CONFIG_FILE_NAME;
	private Object _serviceNamespace = WSDDBuilderArgs.SERVICE_NAMESPACE;

}