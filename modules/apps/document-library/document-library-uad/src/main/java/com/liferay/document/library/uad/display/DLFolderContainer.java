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

package com.liferay.document.library.uad.display;

import com.liferay.document.library.kernel.model.DLFolder;

/**
 * @author Pei-Jung Lan
 */
public class DLFolderContainer {

	public DLFolderContainer(DLFolder dlFolder) {
		_dlFolder = dlFolder;
		_count = 0;
	}

	public DLFolderContainer(DLFolder dlFolder, long count) {
		_dlFolder = dlFolder;
		_count = count;
	}

	public long getCount() {
		return _count;
	}

	public DLFolder getDlFolder() {
		return _dlFolder;
	}

	private final long _count;
	private final DLFolder _dlFolder;

}