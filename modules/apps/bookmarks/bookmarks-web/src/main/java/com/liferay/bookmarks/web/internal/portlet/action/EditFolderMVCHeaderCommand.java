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
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCHeaderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ResourceBundle;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Neil Griffin
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=~bookmarks~add_folder",
		"mvc.command.name=~bookmarks~edit_folder"
	},
	service = MVCHeaderCommand.class
)
public class EditFolderMVCHeaderCommand implements MVCHeaderCommand {

	@Override
	public String renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws PortletException {

		try {
			BookmarksFolder folder = ActionUtil.getFolder(headerRequest);

			PortletConfig portletConfig =
				(PortletConfig)headerRequest.getAttribute(
					"javax.portlet.config");

			ResourceBundle resourceBundle = portletConfig.getResourceBundle(
				headerRequest.getLocale());

			if (folder == null) {
				long parentFolderId = ParamUtil.getLong(
					headerRequest, "parentFolderId",
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID);

				if (parentFolderId >
						BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

					headerResponse.setTitle(
						LanguageUtil.get(resourceBundle, "add-subfolder"));
				}
				else {
					headerResponse.setTitle(
						LanguageUtil.get(resourceBundle, "add-bookmark"));
				}
			}
			else {
				headerResponse.setTitle(
					LanguageUtil.format(
						resourceBundle, "edit-x", folder.getName(), false));
			}
		}
		catch (Exception exception) {
			_log.error(exception, exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditFolderMVCHeaderCommand.class);

}