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

package com.liferay.bookmarks.web.internal.portlet.util;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.bookmarks.util.comparator.EntryCreateDateComparator;
import com.liferay.bookmarks.util.comparator.EntryModifiedDateComparator;
import com.liferay.bookmarks.util.comparator.EntryNameComparator;
import com.liferay.bookmarks.util.comparator.EntryPriorityComparator;
import com.liferay.bookmarks.util.comparator.EntryURLComparator;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.petra.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.RenderURL;

/**
 * @author Brian Wing Shun Chan
 */
public class BookmarksUtil {

	public static void addPortletBreadcrumbEntries(
			BookmarksEntry entry, RenderRequest renderRequest,
			RenderResponse renderResponse,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		BookmarksFolder folder = entry.getFolder();

		if (folder.getFolderId() !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			addPortletBreadcrumbEntries(
				folder, renderRequest, renderResponse,
				searchContainerViewState);
		}
	}

	public static void addPortletBreadcrumbEntries(
			BookmarksFolder folder, RenderRequest renderRequest,
			RenderResponse renderResponse,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String mvcRenderCommandName = ParamUtil.getString(
			renderRequest, "mvcRenderCommandName");

		RenderURL renderURL = null;

		if (mvcRenderCommandName.equals("~bookmarks~select_folder")) {
			renderURL = RenderURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"~bookmarks~select_folder"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildRenderURL();
		}
		else {
			renderURL = RenderURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"~bookmarks~view_bookmarks"
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
				"resetCur", searchContainerViewState.getResetCur()
			).setParameter(
				"tag", searchContainerViewState.getTag(), false
			).buildRenderURL();
		}

		PortalUtil.addPortletBreadcrumbEntry(
			themeDisplay.getRequest(), themeDisplay.translate("home"),
			renderURL.toString());

		if (folder == null) {
			return;
		}

		MutableRenderParameters mutableRenderParameters =
			renderURL.getRenderParameters();

		if (!mvcRenderCommandName.equals("~bookmarks~select_folder")) {
			mutableRenderParameters.setValue(
				"mvcRenderCommandName", "~bookmarks~view_folder");
		}

		List<BookmarksFolder> ancestorFolders = folder.getAncestors();

		Collections.reverse(ancestorFolders);

		for (BookmarksFolder ancestorFolder : ancestorFolders) {
			mutableRenderParameters.setValue(
				"bookmarkId", String.valueOf(ancestorFolder.getFolderId()));

			PortalUtil.addPortletBreadcrumbEntry(
				themeDisplay.getRequest(), ancestorFolder.getName(),
				renderURL.toString());
		}

		mutableRenderParameters.setValue(
			"bookmarkId", String.valueOf(folder.getFolderId()));

		if (folder.getFolderId() !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			BookmarksFolder unescapedFolder = folder.toUnescapedModel();

			PortalUtil.addPortletBreadcrumbEntry(
				themeDisplay.getRequest(), unescapedFolder.getName(),
				renderURL.toString());
		}
	}

	public static void addPortletBreadcrumbEntries(
			long folderId, RenderRequest renderRequest,
			RenderResponse renderResponse,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		addPortletBreadcrumbEntries(
			BookmarksFolderLocalServiceUtil.getFolder(folderId), renderRequest,
			renderResponse, searchContainerViewState);
	}

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$BOOKMARKS_ENTRY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-user-who-added-the-bookmark-entry")
		).put(
			"[$BOOKMARKS_ENTRY_STATUS_BY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-user-who-updated-the-bookmark-entry")
		).put(
			"[$BOOKMARKS_ENTRY_URL$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-bookmark-entry-url")
		).put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient")
		).build();
	}

	public static List<Object> getEntries(Hits hits) {
		List<Object> entries = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			String entryClassName = document.get(Field.ENTRY_CLASS_NAME);
			long entryClassPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			try {
				Object object = null;

				if (entryClassName.equals(BookmarksEntry.class.getName())) {
					object = BookmarksEntryLocalServiceUtil.getEntry(
						entryClassPK);
				}
				else if (entryClassName.equals(
							BookmarksFolder.class.getName())) {

					object = BookmarksFolderLocalServiceUtil.getFolder(
						entryClassPK);
				}

				entries.add(object);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Bookmarks search index is stale and contains entry " +
							entryClassPK,
						exception);
				}
			}
		}

		return entries;
	}

	public static OrderByComparator<BookmarksEntry> getEntryOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<BookmarksEntry> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = new EntryCreateDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new EntryModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new EntryNameComparator(orderByAsc);
		}
		else if (orderByCol.equals("priority")) {
			orderByComparator = new EntryPriorityComparator(orderByAsc);
		}
		else if (orderByCol.equals("url")) {
			orderByComparator = new EntryURLComparator(orderByAsc);
		}

		return orderByComparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(BookmarksUtil.class);

}