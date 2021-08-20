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

import javax.portlet.MutableRenderParameters;
import javax.portlet.RenderURL;

/**
 * @author Neil Griffin
 */
public class FolderViewStateImpl implements FolderViewState {

	public FolderViewStateImpl(BookmarksFolder bookmarksFolder) {
		this(
			bookmarksFolder.getParentFolderId(), bookmarksFolder.getFolderId());
	}

	public FolderViewStateImpl(long parentFolderId, long folderId) {
		this(
			parentFolderId, folderId,
			folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	public FolderViewStateImpl(
		long parentFolderId, long folderId, boolean homeFolder) {

		_parentFolderId = parentFolderId;
		_folderId = folderId;
		_homeFolder = homeFolder;
	}

	@Override
	public void applyRenderParameters(RenderURL redirectURL) {
		MutableRenderParameters renderParameters =
			redirectURL.getRenderParameters();

		if (_homeFolder) {
			renderParameters.removeParameter("bookmarkId");
			renderParameters.removeParameter("mvcRenderCommandName");
			renderParameters.removeParameter("parentFolderId");
		}
		else {
			renderParameters.setValue("bookmarkId", String.valueOf(_folderId));
			renderParameters.setValue(
				"mvcRenderCommandName", "~bookmarks~view_folder");
			renderParameters.setValue(
				"parentFolderId", String.valueOf(_parentFolderId));
		}
	}

	private long _folderId;
	private boolean _homeFolder;
	private long _parentFolderId;

}