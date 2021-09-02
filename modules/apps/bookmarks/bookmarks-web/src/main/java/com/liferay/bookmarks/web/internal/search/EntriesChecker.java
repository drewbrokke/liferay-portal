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

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.GetterUtil;

import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class EntriesChecker extends EmptyOnClickRowChecker {

	public EntriesChecker(RenderResponse renderResponse, boolean signedIn) {
		super(renderResponse);

		_signedIn = signedIn;

		_portletNamespace = renderResponse.getNamespace();
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		long entryId = GetterUtil.getLong(primaryKey);

		BookmarksEntry entry =
			BookmarksEntryLocalServiceUtil.fetchBookmarksEntry(entryId);

		BookmarksFolder folder = null;

		if (entry == null) {
			folder = BookmarksFolderLocalServiceUtil.fetchBookmarksFolder(
				entryId);
		}

		String name = null;

		if (entry != null) {
			name = BookmarksEntry.class.getSimpleName();
		}
		else if (folder != null) {
			name = BookmarksFolder.class.getSimpleName();
		}

		String checkBoxRowIds = getEntryRowIds();
		String checkBoxAllRowIds = "'#" + getAllRowIds() + "'";

		return getRowCheckBox(
			httpServletRequest, checked, disabled,
			_portletNamespace + RowChecker.ROW_IDS + name, primaryKey,
			checkBoxRowIds, checkBoxAllRowIds, StringPool.BLANK);
	}

	@Override
	public boolean isDisabled(Object object) {
		return !_signedIn;
	}

	protected String getEntryRowIds() {
		return StringBundler.concat(
			"['", _portletNamespace, RowChecker.ROW_IDS,
			Folder.class.getSimpleName(), "', '", _portletNamespace,
			RowChecker.ROW_IDS, DLFileShortcut.class.getSimpleName(), "', '",
			_portletNamespace, RowChecker.ROW_IDS,
			FileEntry.class.getSimpleName(), "']");
	}

	private final String _portletNamespace;
	private final boolean _signedIn;

}