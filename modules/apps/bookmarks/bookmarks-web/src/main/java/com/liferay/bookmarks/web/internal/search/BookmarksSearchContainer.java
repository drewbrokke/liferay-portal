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

package com.liferay.bookmarks.web.internal.search;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.web.internal.portlet.model.Bookmark;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.MutableRenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.RenderURL;

/**
 * @author Neil Griffin
 */
public class BookmarksSearchContainer extends SearchContainer<Bookmark> {

	public BookmarksSearchContainer(
		long folderId, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(
			renderRequest, null, null, "curEntry",
			SearchContainer.DEFAULT_DELTA,
			_getIteratorURL(folderId, renderRequest, renderResponse), null,
			"there-are-no-bookmarks-in-this-folder");
	}

	private static RenderURL _getIteratorURL(
		long folderId, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		RenderURL iteratorURL = renderResponse.createRenderURL();

		MutableRenderParameters renderParameters =
			iteratorURL.getRenderParameters();

		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			renderParameters.setValue(
				"mvcRenderCommandName", "~bookmarks~view_bookmarks");
		}
		else {
			renderParameters.setValue(
				"mvcRenderCommandName", "~bookmarks~view_folder");
			renderParameters.setValue("folderId", String.valueOf(folderId));
		}

		String navigation = ParamUtil.getString(
			renderRequest, "navigation", "all");

		renderParameters.setValue("navigation", navigation);

		return iteratorURL;
	}

}