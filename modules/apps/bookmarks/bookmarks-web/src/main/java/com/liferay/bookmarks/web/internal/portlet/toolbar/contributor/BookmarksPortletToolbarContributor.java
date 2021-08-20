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

package com.liferay.bookmarks.web.internal.portlet.toolbar.contributor;

import com.liferay.bookmarks.configuration.BookmarksGroupServiceOverriddenConfiguration;
import com.liferay.bookmarks.constants.BookmarksConstants;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksFolderPermission;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.petra.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"mvc.render.command.name=-",
		"mvc.render.command.name=~bookmarks~view_bookmarks",
		"mvc.render.command.name=~bookmarks~view_folder"
	},
	service = {
		BookmarksPortletToolbarContributor.class,
		PortletToolbarContributor.class
	}
)
public class BookmarksPortletToolbarContributor
	extends BasePortletToolbarContributor {

	protected void addPortletTitleAddBookmarkMenuItem(
			List<MenuItem> menuItems, BookmarksFolder folder,
			ThemeDisplay themeDisplay, PortletRequest portletRequest,
			PortletResponse portletResponse,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		long folderId = _getFolderId(folder);

		if (!BookmarksFolderPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_ENTRY)) {

			return;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setLabel(
			LanguageUtil.get(
				_portal.getHttpServletRequest(portletRequest), "bookmark"));

		urlMenuItem.setURL(
			RenderURLBuilder.createRenderURL(
				(MimeResponse)portletResponse
			).setMVCRenderCommandName(
				"~bookmarks~add_entry"
			).setNavigation(
				searchContainerViewState.getNavigation()
			).setParameter(
				"categoryId", searchContainerViewState.getCategoryId()
			).setParameter(
				"cur", searchContainerViewState.getCur()
			).setParameter(
				"delta", searchContainerViewState.getDelta()
			).setParameter(
				"displayStyle", searchContainerViewState.getDisplayStyle()
			).setParameter(
				"orderByCol", searchContainerViewState.getOrderByCol()
			).setParameter(
				"orderByType", searchContainerViewState.getOrderByType()
			).setParameter(
				"parentFolderId", folderId
			).setParameter(
				"resetCur", searchContainerViewState.getResetCur()
			).setParameter(
				"tag", searchContainerViewState.getTag(), false
			).buildString());

		menuItems.add(urlMenuItem);
	}

	protected void addPortletTitleAddFolderMenuItem(
			List<MenuItem> menuItems, BookmarksFolder folder,
			ThemeDisplay themeDisplay, PortletRequest portletRequest,
			PortletResponse portletResponse,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		long folderId = _getFolderId(folder);

		if (!BookmarksFolderPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_FOLDER)) {

			return;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setLabel(
			LanguageUtil.get(
				_portal.getHttpServletRequest(portletRequest), "folder"));

		urlMenuItem.setURL(
			RenderURLBuilder.createRenderURL(
				(MimeResponse)portletResponse
			).setMVCRenderCommandName(
				"~bookmarks~add_folder"
			).setNavigation(
				searchContainerViewState.getNavigation()
			).setParameter(
				"categoryId", searchContainerViewState.getCategoryId()
			).setParameter(
				"cur", searchContainerViewState.getCur()
			).setParameter(
				"delta", searchContainerViewState.getDelta()
			).setParameter(
				"displayStyle", searchContainerViewState.getDisplayStyle()
			).setParameter(
				"orderByCol", searchContainerViewState.getOrderByCol()
			).setParameter(
				"orderByType", searchContainerViewState.getOrderByType()
			).setParameter(
				"parentFolderId", folderId
			).setParameter(
				"resetCur", searchContainerViewState.getResetCur()
			).setParameter(
				"tag", searchContainerViewState.getTag(), false
			).buildString());

		menuItems.add(urlMenuItem);
	}

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainerViewState searchContainerViewState =
			(SearchContainerViewState)portletRequest.getAttribute(
				"searchContainerViewState");

		BookmarksFolder folder = _getFolder(themeDisplay, portletRequest);

		List<MenuItem> menuItems = new ArrayList<>();

		try {
			addPortletTitleAddFolderMenuItem(
				menuItems, folder, themeDisplay, portletRequest,
				portletResponse, searchContainerViewState);
		}
		catch (PortalException portalException) {
			_log.error("Unable to add folder menu item", portalException);
		}

		try {
			addPortletTitleAddBookmarkMenuItem(
				menuItems, folder, themeDisplay, portletRequest,
				portletResponse, searchContainerViewState);
		}
		catch (PortalException portalException) {
			_log.error("Unable to add bookmark menu item", portalException);
		}

		return menuItems;
	}

	private BookmarksFolder _getFolder(
		ThemeDisplay themeDisplay, PortletRequest portletRequest) {

		BookmarksFolder folder = (BookmarksFolder)portletRequest.getAttribute(
			BookmarksWebKeys.BOOKMARKS_FOLDER);

		if (folder != null) {
			return folder;
		}

		long rootFolderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		try {
			BookmarksGroupServiceOverriddenConfiguration
				bookmarksGroupServiceOverriddenConfiguration =
					ConfigurationProviderUtil.getConfiguration(
						BookmarksGroupServiceOverriddenConfiguration.class,
						new GroupServiceSettingsLocator(
							themeDisplay.getScopeGroupId(),
							BookmarksConstants.SERVICE_NAME));

			rootFolderId =
				bookmarksGroupServiceOverriddenConfiguration.rootFolderId();
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to obtain bookmarks root folder ID for group " +
					themeDisplay.getScopeGroupId(),
				configurationException);
		}

		long folderId = BeanParamUtil.getLong(
			folder, portletRequest, "folderId", rootFolderId);

		if (folderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			try {
				folder = _bookmarksFolderService.getFolder(folderId);
			}
			catch (NoSuchFolderException noSuchFolderException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFolderException, noSuchFolderException);
				}

				folder = null;
			}
			catch (PortalException portalException) {
				_log.error(portalException, portalException);
			}
		}

		return folder;
	}

	private long _getFolderId(BookmarksFolder folder) {
		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (folder != null) {
			folderId = folder.getFolderId();
		}

		return folderId;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BookmarksPortletToolbarContributor.class);

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	private ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private Portal _portal;

}