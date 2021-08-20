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
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewStateFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Neil Griffin
 */
@Component(
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=~bookmarks~move_entry",
		"mvc.command.name=~bookmarks~move_folder"
	},
	service = MVCRenderCommand.class
)
public class MoveEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchContainerViewState searchContainerViewState =
			_searchContainerViewStateFactory.create(
				"descriptive", "all", "title", "asc", renderRequest,
				new String[] {"createDate", "title"});

		renderRequest.setAttribute(
			"searchContainerViewState", searchContainerViewState);

		try {
			List<BookmarksEntry> entries = ActionUtil.getEntries(renderRequest);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_ENTRIES, entries);

			String mvcRenderCommandName = ParamUtil.getString(
				renderRequest, "mvcRenderCommandName");

			if (mvcRenderCommandName.contains("move_entry")) {
				renderRequest.setAttribute(
					BookmarksWebKeys.BOOKMARKS_ENTRY,
					ActionUtil.getEntry(renderRequest));
			}

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_FOLDERS,
				ActionUtil.getFolders(renderRequest));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/move_entries.jsp";
	}

	@Reference
	private SearchContainerViewStateFactory _searchContainerViewStateFactory;

}