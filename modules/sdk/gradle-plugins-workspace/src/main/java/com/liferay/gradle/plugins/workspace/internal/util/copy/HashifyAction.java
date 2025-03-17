/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.util.copy;

import com.liferay.gradle.util.hash.HashUtil;
import com.liferay.gradle.util.hash.HashValue;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.gradle.api.Action;
import org.gradle.api.file.FileCopyDetails;

/**
 * @author Peter Shin
 */
public class HashifyAction implements Action<FileCopyDetails> {

	public HashifyAction(String fileNameGlob) {
		FileSystem fileSystem = FileSystems.getDefault();

		_pathMatcher = fileSystem.getPathMatcher("glob:" + fileNameGlob);
	}

	@Override
	public void execute(FileCopyDetails fileCopyDetails) {
		if (!_pathMatcher.matches(Paths.get(fileCopyDetails.getName()))) {
			return;
		}

		fileCopyDetails.setName(_getHashedFileName(fileCopyDetails));
	}

	private String _getHashedFileName(FileCopyDetails fileCopyDetails) {
		HashValue hashValue = HashUtil.sha1(fileCopyDetails.getFile());

		String fileName = fileCopyDetails.getName();

		int index = fileName.lastIndexOf('.');

		if (index == -1) {
			return fileName + '.' + hashValue.asHexString();
		}

		String extension = fileName.substring(index + 1);
		String shortFileName = fileName.substring(0, index);

		return shortFileName + '.' + hashValue.asHexString() + '.' + extension;
	}

	private final PathMatcher _pathMatcher;

}