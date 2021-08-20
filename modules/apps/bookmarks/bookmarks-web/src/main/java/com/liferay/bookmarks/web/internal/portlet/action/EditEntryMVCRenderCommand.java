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

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.EntryURLException;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.web.internal.portlet.util.BookmarksUtil;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
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
 * @author Levente Hudák
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=~bookmarks~add_entry",
		"mvc.command.name=~bookmarks~edit_entry"
	},
	service = MVCRenderCommand.class
)
public class EditEntryMVCRenderCommand extends BaseMVCRenderCommand {

	protected void checkPermissions(
			PermissionChecker permissionChecker, BookmarksEntry entry)
		throws PortalException {

		_bookmarksEntryModelResourcePermission.check(
			permissionChecker, entry, ActionKeys.UPDATE);
	}

	@Override
	protected String prepareView(
			long parentFolderId, RenderRequest renderRequest,
			RenderResponse renderResponse, ResourceBundle resourceBundle,
			SearchContainerViewState searchContainerViewState,
			ThemeDisplay themeDisplay)
		throws PortletException {

		renderRequest.setAttribute("BookmarksEntryClass", BookmarksEntry.class);

		renderRequest.setAttribute(
			"EntryURLExceptionClass", EntryURLException.class);

		renderRequest.setAttribute(
			"NoSuchFolderExceptionClass", NoSuchFolderException.class);

		try {
			BookmarksEntry entry = ActionUtil.getEntry(renderRequest);

			if (entry != null) {
				checkPermissions(themeDisplay.getPermissionChecker(), entry);
			}

			renderRequest.setAttribute(BookmarksWebKeys.BOOKMARKS_ENTRY, entry);

			Layout layout = themeDisplay.getLayout();

			if (entry != null) {
				BookmarksUtil.addPortletBreadcrumbEntries(
					entry, renderRequest, renderResponse,
					searchContainerViewState);

				if (!layout.isTypeControlPanel()) {
					_portal.addPortletBreadcrumbEntry(
						themeDisplay.getRequest(),
						LanguageUtil.get(resourceBundle, "edit"),
						themeDisplay.getURLCurrent());
				}
			}
			else {
				BookmarksUtil.addPortletBreadcrumbEntries(
					parentFolderId, renderRequest, renderResponse,
					searchContainerViewState);

				if (!layout.isTypeControlPanel()) {
					_portal.addPortletBreadcrumbEntry(
						themeDisplay.getRequest(),
						LanguageUtil.get(resourceBundle, "add-bookmark"),
						themeDisplay.getURLCurrent());
				}
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/edit_entry.jspx";
	}

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksEntry)"
	)
	private volatile ModelResourcePermission<BookmarksEntry>
		_bookmarksEntryModelResourcePermission;

	@Reference
	private Portal _portal;

}