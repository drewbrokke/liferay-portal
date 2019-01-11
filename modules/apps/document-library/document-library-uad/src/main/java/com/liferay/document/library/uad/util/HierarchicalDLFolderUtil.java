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

package com.liferay.document.library.uad.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sam Tran
 * @author Pei-Jung Lan
 */
public class HierarchicalDLFolderUtil {

	public static Map<Long, Integer> getTopLevelFoldersAndCount(
			List<DLFileEntry> dlFileEntries, List<DLFolder> dlFolders,
			long parentFolderId)
		throws PortalException {

		Map<Long, Integer> topLevelFolders = new HashMap<>();

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			if (!_isImmediateChild(dlFileEntry, parentFolderId)) {
				_addTopLevelFolderToMap(
					dlFileEntry, parentFolderId, topLevelFolders);
			}
		}

		for (DLFolder dlFolder : dlFolders) {
			if (!_isImmediateChild(dlFolder, parentFolderId)) {
				_addTopLevelFolderToMap(
					dlFolder, parentFolderId, topLevelFolders);
			}
		}

		return topLevelFolders;
	}

	private static void _addTopLevelFolderToMap(
			DLFileEntry dlFileEntry, long parentFolderId,
			Map<Long, Integer> topLevelFoldersMap)
		throws PortalException {

		_addTopLevelFolderToMap(
			dlFileEntry.getFolder(), parentFolderId, topLevelFoldersMap);
	}

	private static void _addTopLevelFolderToMap(
			DLFolder dlFolder, long parentFolderId,
			Map<Long, Integer> topLevelFoldersMap)
		throws PortalException {

		if ((parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
			!StringUtil.contains(
				dlFolder.getTreePath(), String.valueOf(parentFolderId), "/")) {

			return;
		}

		long topLevelFolderId = _getTopLevelFolderId(dlFolder, parentFolderId);

		topLevelFoldersMap.put(
			topLevelFolderId,
			topLevelFoldersMap.getOrDefault(topLevelFolderId, 0) + 1);
	}

	private static long _getTopLevelFolderId(
			DLFolder dlFolder, long parentFolderId)
		throws PortalException {

		if ((dlFolder.getParentFolderId() ==
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
			(dlFolder.getParentFolderId() == parentFolderId)) {

			return dlFolder.getFolderId();
		}

		List<Long> ancestorFolderIds = dlFolder.getAncestorFolderIds();

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return ancestorFolderIds.get(ancestorFolderIds.size() - 1);
		}

		return ancestorFolderIds.get(
			ancestorFolderIds.indexOf(parentFolderId) - 1);
	}

	private static boolean _isImmediateChild(
		DLFileEntry dlFileEntry, long parentFolderId) {

		if (dlFileEntry.getFolderId() == parentFolderId) {
			return true;
		}

		return false;
	}

	private static boolean _isImmediateChild(
		DLFolder dlFolder, long parentFolderId) {

		if (dlFolder.getParentFolderId() == parentFolderId) {
			return true;
		}

		return false;
	}

}