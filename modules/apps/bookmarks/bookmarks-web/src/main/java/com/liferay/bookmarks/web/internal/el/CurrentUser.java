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

package com.liferay.bookmarks.web.internal.el;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.web.internal.portlet.model.Bookmark;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksEntryPermission;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksFolderPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermission;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.Objects;

/**
 * @author Neil Griffin
 */
public class CurrentUser {

	public CurrentUser(
		long companyId, boolean emailEntryAddedEnabled,
		boolean emailEntryUpdatedEnabled, GroupPermission groupPermission,
		String portletName, PermissionChecker permissionChecker,
		long scopeGroupId, boolean signedIn,
		StagingGroupHelper stagingGroupHelper,
		SubscriptionLocalService subscriptionLocalService, long userId) {

		_companyId = companyId;
		_emailEntryAddedEnabled = emailEntryAddedEnabled;
		_emailEntryUpdatedEnabled = emailEntryUpdatedEnabled;
		_groupPermission = groupPermission;
		_portletName = portletName;
		_permissionChecker = permissionChecker;
		_scopeGroupId = scopeGroupId;
		_signedIn = signedIn;
		_stagingGroupHelper = stagingGroupHelper;
		_subscriptionLocalService = subscriptionLocalService;
		_userId = userId;
	}

	public boolean isSignedIn() {
		return _signedIn;
	}

	public boolean isSubscribedToBookmark(Bookmark bookmark) {
		return _subscriptionLocalService.isSubscribed(
			_companyId, _userId, BookmarksEntry.class.getName(),
			bookmark.getBookmarkId());
	}

	public boolean mayDeleteBookmark(Bookmark bookmark) throws PortalException {
		return _checkPermission(bookmark, ActionKeys.DELETE);
	}

	public boolean mayEditBookmark(Bookmark bookmark) throws PortalException {
		return _checkPermission(bookmark, ActionKeys.UPDATE);
	}

	public boolean mayPermissionBookmark(Bookmark bookmark)
		throws PortalException {

		return _checkPermission(bookmark, ActionKeys.PERMISSIONS);
	}

	public boolean mayPublishBookmark(Bookmark bookmark) {
		boolean bookmarksAdmin = _portletName.equals(
			BookmarksPortletKeys.BOOKMARKS_ADMIN);

		boolean hasExportImportPortletInfoPermission = false;

		try {
			hasExportImportPortletInfoPermission = _groupPermission.contains(
				_permissionChecker, _scopeGroupId,
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"An exception occurred when checking if the publish " +
						"action should be displayed",
					portalException);
			}
		}

		boolean inStagingGroup = _stagingGroupHelper.isStagingGroup(
			_scopeGroupId);

		boolean portletStaged = _stagingGroupHelper.isStagedPortlet(
			_scopeGroupId, BookmarksPortletKeys.BOOKMARKS);

		if (Objects.equals(bookmark.getEntityName(), "folder") &&
			bookmarksAdmin && hasExportImportPortletInfoPermission &&
			inStagingGroup && portletStaged) {

			return true;
		}

		return false;
	}

	public boolean maySubscribeToBookmark(Bookmark bookmark)
		throws PortalException {

		if (_checkPermission(bookmark, ActionKeys.SUBSCRIBE) &&
			(_emailEntryAddedEnabled || _emailEntryUpdatedEnabled)) {

			return true;
		}

		return false;
	}

	public boolean mayViewBookmark(Bookmark bookmark) throws PortalException {
		return _checkPermission(bookmark, ActionKeys.VIEW);
	}

	private boolean _checkPermission(Bookmark bookmark, String actionKey)
		throws PortalException {

		if (bookmark.isFolder()) {
			return BookmarksFolderPermission.contains(
				_permissionChecker, (BookmarksFolder)bookmark.getWrapped(),
				actionKey);
		}

		return BookmarksEntryPermission.contains(
			_permissionChecker, (BookmarksEntry)bookmark.getWrapped(),
			actionKey);
	}

	private static final Log _log = LogFactoryUtil.getLog(CurrentUser.class);

	private final long _companyId;
	private final boolean _emailEntryAddedEnabled;
	private final boolean _emailEntryUpdatedEnabled;
	private final GroupPermission _groupPermission;
	private final PermissionChecker _permissionChecker;
	private final String _portletName;
	private final long _scopeGroupId;
	private final boolean _signedIn;
	private final StagingGroupHelper _stagingGroupHelper;
	private final SubscriptionLocalService _subscriptionLocalService;
	private final long _userId;

}