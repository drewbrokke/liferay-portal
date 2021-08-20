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

package com.liferay.bookmarks.web.internal.portlet.view;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Neil Griffin
 */
@Component(immediate = true, service = FolderViewStateFactory.class)
public class FolderViewStateFactory {

	public FolderViewState getFolderViewState(BookmarksFolder bookmarksFolder) {
		if (bookmarksFolder == null) {
			return getFolderViewState(
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		return new FolderViewStateImpl(bookmarksFolder);
	}

	public FolderViewState getFolderViewState(long folderId) {
		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return new FolderViewStateImpl(
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID, true);
		}

		try {
			return new FolderViewStateImpl(
				_bookmarksFolderService.getFolder(folderId));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FolderViewStateFactory.class);

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

}