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

import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.trash.TrashHelper;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Neil Griffin
 */
@Component(immediate = true, service = BookmarksSearchContainerFactory.class)
public class BookmarksSearchContainerFactory {

	public BookmarksSearchContainer getBookmarksSearchContainer(
			long folderId, RenderRequest renderRequest,
			RenderResponse renderResponse, long scopeGroupId,
			SearchContainerViewState searchContainerViewState, boolean signedIn)
		throws PortalException {

		BookmarksSearchContainer bookmarksSearchContainer =
			new BookmarksSearchContainer(
				folderId, renderRequest, renderResponse);

		/*
		bookmarksSearchContainer.setDelta(searchContainerViewState.getDelta());

		bookmarksSearchContainer.setOrderByCol(
			searchContainerViewState.getOrderByCol());

		bookmarksSearchContainer.setOrderByType(
			searchContainerViewState.getOrderByType());
		 */

		EntriesChecker entriesChecker = new EntriesChecker(
			renderResponse, signedIn);

		entriesChecker.setCssClass("entry-selector");

		bookmarksSearchContainer.setRowChecker(entriesChecker);

		if (folderId == 0) {
			entriesChecker.setRememberCheckBoxStateURLRegex(
				"mvcRenderCommandName=~bookmarks~view_bookmarks(&.|$)");
		}
		else {
			entriesChecker.setRememberCheckBoxStateURLRegex(
				StringBundler.concat(
					"^(?!.*", renderResponse.getNamespace(),
					"redirect).*(folderId=", folderId, ")"));
		}

		EntriesMover entriesMover = new EntriesMover(
			_trashHelper.isTrashEnabled(scopeGroupId));

		bookmarksSearchContainer.setRowMover(entriesMover);

		return bookmarksSearchContainer;
	}

	@Reference
	private TrashHelper _trashHelper;

}