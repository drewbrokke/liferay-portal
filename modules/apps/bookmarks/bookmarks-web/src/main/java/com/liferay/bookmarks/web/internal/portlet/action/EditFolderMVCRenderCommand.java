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

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.FolderNameException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.bookmarks.web.internal.portlet.util.BookmarksUtil;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import java.util.ResourceBundle;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=~bookmarks~add_folder",
		"mvc.command.name=~bookmarks~edit_folder"
	},
	service = MVCRenderCommand.class
)
public class EditFolderMVCRenderCommand extends BaseMVCRenderCommand {

	@Override
	protected String prepareView(
			long parentFolderId, RenderRequest renderRequest,
			RenderResponse renderResponse, ResourceBundle resourceBundle,
			SearchContainerViewState searchContainerViewState,
			ThemeDisplay themeDisplay)
		throws PortletException {

		renderRequest.setAttribute(
			"BookmarksFolderClass", BookmarksFolder.class);
		renderRequest.setAttribute(
			"FolderNameExceptionClass", FolderNameException.class);
		renderRequest.setAttribute(
			"NAME_INVALID_CHARACTERS",
			BookmarksFolderConstants.NAME_INVALID_CHARACTERS);
		renderRequest.setAttribute(
			"NAME_RESERVED_WORDS",
			BookmarksFolderConstants.NAME_RESERVED_WORDS);

		try {
			BookmarksFolder folder = ActionUtil.getFolder(renderRequest);

			if (folder != null) {
				_bookmarksFolderModelResourcePermission.check(
					themeDisplay.getPermissionChecker(), folder,
					ActionKeys.UPDATE);
			}

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_FOLDER, folder);

			Layout layout = themeDisplay.getLayout();

			if (folder != null) {
				BookmarksUtil.addPortletBreadcrumbEntries(
					folder.getFolderId(), renderRequest, renderResponse,
					searchContainerViewState);

				if (!layout.isTypeControlPanel()) {
					_portal.addPortletBreadcrumbEntry(
						themeDisplay.getRequest(),
						LanguageUtil.get(resourceBundle, "edit"),
						themeDisplay.getURLCurrent());
				}
			}
			else {
				if (parentFolderId > 0) {
					BookmarksUtil.addPortletBreadcrumbEntries(
						parentFolderId, renderRequest, renderResponse,
						searchContainerViewState);

					if (!layout.isTypeControlPanel()) {
						_portal.addPortletBreadcrumbEntry(
							themeDisplay.getRequest(),
							LanguageUtil.get(resourceBundle, "add-subfolder"),
							themeDisplay.getURLCurrent());
					}
				}
				else if (!layout.isTypeControlPanel()) {
					_portal.addPortletBreadcrumbEntry(
						themeDisplay.getRequest(),
						LanguageUtil.get(resourceBundle, "add-folder"),
						themeDisplay.getURLCurrent());
				}
			}

			String parentFolderName = LanguageUtil.get(resourceBundle, "home");

			if (parentFolderId !=
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				BookmarksFolder parentFolder =
					_bookmarksFolderService.getFolder(parentFolderId);

				parentFolderName = parentFolder.getName();
			}

			renderRequest.setAttribute("parentFolderName", parentFolderName);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFolderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/edit_folder.jspx";
	}

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	private volatile ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private Portal _portal;

}