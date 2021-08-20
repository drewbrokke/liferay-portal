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

package com.liferay.bookmarks.web.internal.portlet.model;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;

import java.util.Date;

/**
 * @author Neil Griffin
 */
public class Bookmark {

	public Bookmark(BookmarksEntry bookmarksEntry) {
		_wrapped = bookmarksEntry;
		_bookmarkId = bookmarksEntry.getEntryId();
		_description = bookmarksEntry.getDescription();
		_entityName = "entry";
		_entriesCount = 0;
		_entryId = bookmarksEntry.getEntryId();
		_folder = false;
		_foldersCount = 0;
		_folderId = 0L;
		_icon = "link";
		_modelResource = BookmarksEntry.class.getName();
		_modifiedDate = bookmarksEntry.getModifiedDate();
		_name = bookmarksEntry.getName();
		_parentFolderId = bookmarksEntry.getFolderId();
		_url = bookmarksEntry.getUrl();
		_visits = bookmarksEntry.getVisits();
	}

	public Bookmark(
		BookmarksFolder bookmarksFolder, int foldersCount, int entriesCount) {

		_foldersCount = foldersCount;
		_entriesCount = entriesCount;

		_wrapped = bookmarksFolder;
		_bookmarkId = bookmarksFolder.getFolderId();
		_description = bookmarksFolder.getDescription();
		_entityName = "folder";
		_entryId = 0L;
		_folder = true;
		_folderId = bookmarksFolder.getFolderId();
		_icon = "folder";
		_modelResource = BookmarksFolder.class.getName();
		_modifiedDate = bookmarksFolder.getModifiedDate();
		_name = bookmarksFolder.getName();
		_parentFolderId = bookmarksFolder.getParentFolderId();
		_url = null;
		_visits = null;
	}

	public long getBookmarkId() {
		return _bookmarkId;
	}

	public String getDescription() {
		return _description;
	}

	public String getEntityName() {
		return _entityName;
	}

	public int getEntriesCount() {
		return _entriesCount;
	}

	public long getEntryId() {
		return _entryId;
	}

	public long getFolderId() {
		return _folderId;
	}

	public int getFoldersCount() {
		return _foldersCount;
	}

	public String getIcon() {
		return _icon;
	}

	public String getModelResource() {
		return _modelResource;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public String getName() {
		return _name;
	}

	public long getParentFolderId() {
		return _parentFolderId;
	}

	public String getUrl() {
		return _url;
	}

	public Long getVisits() {
		return _visits;
	}

	public Object getWrapped() {
		return _wrapped;
	}

	public boolean isEntry() {
		return !_folder;
	}

	public boolean isFolder() {
		return _folder;
	}

	private final long _bookmarkId;
	private final String _description;
	private final String _entityName;
	private final int _entriesCount;
	private final long _entryId;
	private final boolean _folder;
	private final long _folderId;
	private final int _foldersCount;
	private final String _icon;
	private final String _modelResource;
	private final Date _modifiedDate;
	private final String _name;
	private final long _parentFolderId;
	private final String _url;
	private final Long _visits;
	private final Object _wrapped;

}