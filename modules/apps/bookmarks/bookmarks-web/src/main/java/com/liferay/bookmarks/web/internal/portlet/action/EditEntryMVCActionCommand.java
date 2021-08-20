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

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.exception.EntryURLException;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.bookmarks.web.internal.portlet.view.FolderViewState;
import com.liferay.bookmarks.web.internal.portlet.view.FolderViewStateFactory;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.service.TrashEntryService;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.RenderURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=~bookmarks~add_entry",
		"mvc.command.name=~bookmarks~edit_entry",
		"mvc.command.name=~bookmarks~move_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	protected long deleteEntry(ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long parentFolderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		long[] deleteEntryIds = null;

		long bookmarkId = ParamUtil.getLong(actionRequest, "bookmarkId");

		if (bookmarkId > 0) {
			deleteEntryIds = new long[] {bookmarkId};
		}
		else {
			deleteEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsBookmarksEntry");
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteEntryId : deleteEntryIds) {
			if (moveToTrash) {
				BookmarksEntry deleteEntry = _bookmarksEntryService.getEntry(
					deleteEntryId);

				parentFolderId = deleteEntry.getFolderId();

				BookmarksEntry entry = _bookmarksEntryService.moveEntryToTrash(
					deleteEntryId);

				trashedModels.add(entry);
			}
			else {
				BookmarksEntry deleteEntry = _bookmarksEntryService.getEntry(
					deleteEntryId);

				parentFolderId = deleteEntry.getFolderId();

				_bookmarksEntryService.deleteEntry(deleteEntryId);
			}
		}

		long[] deleteFolderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksFolder");

		for (long deleteFolderId : deleteFolderIds) {
			if (moveToTrash) {
				BookmarksFolder folder =
					_bookmarksFolderService.moveFolderToTrash(deleteFolderId);

				parentFolderId = folder.getParentFolderId();

				trashedModels.add(folder);
			}
			else {
				BookmarksFolder deleteFolder =
					_bookmarksFolderService.getFolder(deleteFolderId);

				parentFolderId = deleteFolder.getParentFolderId();

				_bookmarksFolderService.deleteFolder(deleteFolderId);
			}
		}

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}

		return parentFolderId;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		FolderViewState folderViewState = null;

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				BookmarksEntry updatedEntry = updateEntry(actionRequest);

				folderViewState = _folderViewStateFactory.getFolderViewState(
					updatedEntry.getFolder());
			}
			else if (cmd.equals(Constants.DELETE)) {
				long viewStateFolderId = deleteEntry(actionRequest, false);

				folderViewState = _folderViewStateFactory.getFolderViewState(
					viewStateFolderId);
			}
			else if (cmd.equals(Constants.MOVE)) {
				folderViewState = _folderViewStateFactory.getFolderViewState(
					moveEntries(actionRequest));
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				folderViewState = _folderViewStateFactory.getFolderViewState(
					deleteEntry(actionRequest, true));
			}
			else if (cmd.equals(Constants.RESTORE)) {
				restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				subscribeEntry(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				unsubscribeEntry(actionRequest);
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}

			RenderURL redirectURL = actionResponse.createRedirectURL(
				MimeResponse.Copy.ALL);

			if (folderViewState != null) {
				folderViewState.applyRenderParameters(redirectURL);
			}

			actionRequest.setAttribute(
				WebKeys.REDIRECT, redirectURL.toString());
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/bookmarks/error.jsp");
			}
			else if (exception instanceof EntryURLException ||
					 exception instanceof NoSuchFolderException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof AssetCategoryException ||
					 exception instanceof AssetTagException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				throw exception;
			}
		}
	}

	protected long moveEntries(ActionRequest actionRequest) throws Exception {
		long newFolderId = ParamUtil.getLong(actionRequest, "newFolderId");

		long[] folderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksFolder");

		for (long folderId : folderIds) {
			_bookmarksFolderService.moveFolder(folderId, newFolderId);
		}

		long[] entryIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksEntry");

		for (long entryId : entryIds) {
			_bookmarksEntryService.moveEntry(entryId, newFolderId);
		}

		return newFolderId;
	}

	protected void restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	protected void subscribeEntry(ActionRequest actionRequest)
		throws Exception {

		_bookmarksEntryService.subscribeEntry(
			ParamUtil.getLong(actionRequest, "bookmarkId"));
	}

	protected void unsubscribeEntry(ActionRequest actionRequest)
		throws Exception {

		_bookmarksEntryService.unsubscribeEntry(
			ParamUtil.getLong(actionRequest, "bookmarkId"));
	}

	protected BookmarksEntry updateEntry(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long bookmarkId = ParamUtil.getLong(actionRequest, "bookmarkId");

		long groupId = themeDisplay.getScopeGroupId();
		long parentFolderId = ParamUtil.getLong(
			actionRequest, "parentFolderId");
		String name = ParamUtil.getString(actionRequest, "name");
		String url = ParamUtil.getString(actionRequest, "url");
		String description = ParamUtil.getString(actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			BookmarksEntry.class.getName(), actionRequest);

		BookmarksEntry entry = null;

		if (bookmarkId <= 0) {

			// Add entry

			entry = _bookmarksEntryService.addEntry(
				groupId, parentFolderId, name, url, description,
				serviceContext);
		}
		else {

			// Update entry

			entry = _bookmarksEntryService.updateEntry(
				bookmarkId, groupId, parentFolderId, name, url, description,
				serviceContext);
		}

		return entry;
	}

	@Reference
	private BookmarksEntryService _bookmarksEntryService;

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private FolderViewStateFactory _folderViewStateFactory;

	@Reference
	private TrashEntryService _trashEntryService;

}