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

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.bookmarks.configuration.BookmarksGroupServiceOverriddenConfiguration;
import com.liferay.bookmarks.constants.BookmarksConstants;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.search.BookmarksSearcher;
import com.liferay.bookmarks.service.BookmarksEntryService;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.bookmarks.web.internal.dao.search.BookmarksResultRowSplitter;
import com.liferay.bookmarks.web.internal.el.CurrentUserFactory;
import com.liferay.bookmarks.web.internal.portlet.model.Bookmark;
import com.liferay.bookmarks.web.internal.portlet.toolbar.contributor.BookmarksPortletToolbarContributor;
import com.liferay.bookmarks.web.internal.portlet.util.BookmarksUtil;
import com.liferay.bookmarks.web.internal.portlet.view.BookmarksManagementToolbarViewState;
import com.liferay.bookmarks.web.internal.search.BookmarksSearchContainer;
import com.liferay.bookmarks.web.internal.search.BookmarksSearchContainerFactory;
import com.liferay.frontend.taglib.clay.view.state.ManagementToolbarViewState;
import com.liferay.frontend.taglib.clay.view.state.ManagementToolbarViewStateDisplayContextAdapter;
import com.liferay.frontend.taglib.clay.view.state.ManagementToolbarViewStateFactory;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.portlet.PortletException;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"javax.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/", "mvc.command.name=~bookmarks~view_bookmarks",
		"mvc.command.name=~bookmarks~view_folder"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand extends BaseMVCRenderCommand {

	@Override
	public String prepareView(
			long parentFolderId, RenderRequest renderRequest,
			RenderResponse renderResponse, ResourceBundle resourceBundle,
			SearchContainerViewState searchContainerViewState,
			ThemeDisplay themeDisplay)
		throws PortletException {

		try {
			_prepareView(
				ActionUtil.getFolder(renderRequest), renderRequest,
				renderResponse, resourceBundle, searchContainerViewState,
				themeDisplay);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFolderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/view.jsp";
	}

	@Override
	protected boolean isViewCancelable() {
		return false;
	}

	private List<Bookmark> _getBookmarks(
		List<?> assetEntries, long scopeGroupId) {

		List<Bookmark> bookmarks = new ArrayList<>();

		for (Object assetEntry : assetEntries) {
			if (assetEntry instanceof BookmarksFolder) {
				BookmarksFolder bookmarksFolder = (BookmarksFolder)assetEntry;

				long folderId = bookmarksFolder.getFolderId();

				bookmarks.add(
					new Bookmark(
						bookmarksFolder,
						_bookmarksFolderService.getFoldersCount(
							scopeGroupId, folderId),
						_bookmarksEntryService.getEntriesCount(
							scopeGroupId, folderId)));
			}
			else if (assetEntry instanceof BookmarksEntry) {
				bookmarks.add(new Bookmark((BookmarksEntry)assetEntry));
			}
			else {
				AssetEntry bookmarkAssetEntry = (AssetEntry)assetEntry;

				AssetRenderer<?> assetRenderer =
					bookmarkAssetEntry.getAssetRenderer();

				bookmarks.add(
					new Bookmark(
						(BookmarksEntry)assetRenderer.getAssetObject()));
			}
		}

		return bookmarks;
	}

	private long _getFolderId(
			BookmarksFolder bookmarksFolder, long configRootFolderId,
			long scopeGroupId)
		throws PortalException {

		if (bookmarksFolder != null) {
			return bookmarksFolder.getFolderId();
		}

		if (configRootFolderId !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			try {
				BookmarksFolder configFolder =
					_bookmarksFolderLocalService.getFolder(configRootFolderId);

				if (configFolder.getGroupId() == scopeGroupId) {
					return configFolder.getFolderId();
				}
			}
			catch (NoSuchFolderException noSuchFolderException) {
				return BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	private void _populateSearchContainerByAssetEntryQuery(
			long scopeGroupId,
			BookmarksSearchContainer bookmarksSearchContainer,
			SearchContainerViewState searchContainerViewState)
		throws PortalException {

		List<Object> bookmarks = new ArrayList<>();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
			BookmarksEntry.class.getName(), bookmarksSearchContainer);

		assetEntryQuery.setEnablePermissions(true);
		assetEntryQuery.setExcludeZeroViewCount(false);
		assetEntryQuery.setEnd(searchContainerViewState.getEnd());
		assetEntryQuery.setStart(searchContainerViewState.getStart());

		if (Validator.isNotNull(searchContainerViewState.getKeywords())) {
			assetEntryQuery.setAllKeywords(
				new String[] {searchContainerViewState.getKeywords()});
		}

		bookmarksSearchContainer.setTotal(
			_assetEntryService.getEntriesCount(assetEntryQuery));

		bookmarks.addAll(_assetEntryService.getEntries(assetEntryQuery));

		bookmarksSearchContainer.setResults(
			_getBookmarks(
				_assetEntryService.getEntries(assetEntryQuery), scopeGroupId));
	}

	private void _populateSearchContainerByFoldersAndEntries(
		long folderId, long scopeGroupId,
		BookmarksSearchContainer bookmarksSearchContainer,
		SearchContainerViewState searchContainerViewState) {

		bookmarksSearchContainer.setTotal(
			_bookmarksFolderService.getFoldersAndEntriesCount(
				scopeGroupId, folderId));

		bookmarksSearchContainer.setResults(
			_getBookmarks(
				_bookmarksFolderService.getFoldersAndEntries(
					scopeGroupId, folderId, WorkflowConstants.STATUS_APPROVED,
					searchContainerViewState.getStart(),
					searchContainerViewState.getEnd()),
				scopeGroupId));
	}

	private void _populateSearchContainerByGroup(
			long scopeGroupId,
			BookmarksSearchContainer bookmarksSearchContainer,
			SearchContainerViewState searchContainerViewState, boolean signedIn,
			long userId)
		throws PortalException {

		long groupEntriesUserId = 0;

		if (Objects.equals(searchContainerViewState.getNavigation(), "mine") &&
			signedIn) {

			groupEntriesUserId = userId;
		}

		List<Object> bookmarks = new ArrayList<>();

		bookmarks.addAll(
			_bookmarksEntryService.getGroupEntries(
				scopeGroupId, groupEntriesUserId,
				searchContainerViewState.getStart(),
				searchContainerViewState.getEnd()));

		bookmarksSearchContainer.setResults(
			_getBookmarks(bookmarks, scopeGroupId));

		bookmarksSearchContainer.setTotal(
			_bookmarksEntryService.getGroupEntriesCount(
				scopeGroupId, groupEntriesUserId));
	}

	private void _populateSearchContainerFromHits(
			long folderId, long scopeGroupId,
			BookmarksSearchContainer bookmarksSearchContainer,
			SearchContainerViewState searchContainerViewState,
			SearchContext searchContext)
		throws PortalException {

		Indexer<?> indexer = BookmarksSearcher.getInstance();

		searchContext.setAttribute("paginationType", "more");
		searchContext.setEnd(searchContainerViewState.getEnd());
		searchContext.setFolderIds(new long[] {folderId});
		searchContext.setIncludeInternalAssetCategories(true);
		searchContext.setKeywords(searchContainerViewState.getKeywords());
		searchContext.setStart(searchContainerViewState.getStart());

		Hits hits = indexer.search(searchContext);

		bookmarksSearchContainer.setResults(
			_getBookmarks(BookmarksUtil.getEntries(hits), scopeGroupId));
		bookmarksSearchContainer.setTotal(hits.getLength());
	}

	private void _prepareView(
			BookmarksFolder bookmarksFolder, RenderRequest renderRequest,
			RenderResponse renderResponse, ResourceBundle resourceBundle,
			SearchContainerViewState searchContainerViewState,
			ThemeDisplay themeDisplay)
		throws PortalException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		renderRequest.setAttribute(
			"application", httpServletRequest.getServletContext());

		renderRequest.setAttribute(
			BookmarksWebKeys.BOOKMARKS_FOLDER, bookmarksFolder);

		renderRequest.setAttribute(
			BookmarksWebKeys.BOOKMARKS_PORTLET_TOOLBAR_CONTRIBUTOR,
			_bookmarksPortletToolbarContributor);

		renderRequest.setAttribute(
			"bookmarksResultRowSplitter", new BookmarksResultRowSplitter());

		long scopeGroupId = themeDisplay.getScopeGroupId();

		long configRootFolderId =
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		boolean emailEntryAddedEnabled = false;
		boolean emailEntryUpdatedEnabled = false;
		boolean showFoldersSearch = false;

		try {
			BookmarksGroupServiceOverriddenConfiguration
				bookmarksGroupServiceOverriddenConfiguration =
					ConfigurationProviderUtil.getConfiguration(
						BookmarksGroupServiceOverriddenConfiguration.class,
						new GroupServiceSettingsLocator(
							scopeGroupId, BookmarksConstants.SERVICE_NAME));

			configRootFolderId =
				bookmarksGroupServiceOverriddenConfiguration.rootFolderId();

			emailEntryAddedEnabled =
				bookmarksGroupServiceOverriddenConfiguration.
					emailEntryAddedEnabled();

			emailEntryUpdatedEnabled =
				bookmarksGroupServiceOverriddenConfiguration.
					emailEntryUpdatedEnabled();

			showFoldersSearch =
				bookmarksGroupServiceOverriddenConfiguration.
					showFoldersSearch();
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to obtain overridden configuration for group " +
					scopeGroupId,
				configurationException);
		}

		long folderId = _getFolderId(
			bookmarksFolder, configRootFolderId, scopeGroupId);

		BookmarksSearchContainer bookmarksSearchContainer =
			_bookmarksSearchContainerFactory.getBookmarksSearchContainer(
				folderId, renderRequest, renderResponse, scopeGroupId,
				themeDisplay.isSignedIn());

		renderRequest.setAttribute(
			"bookmarksSearchContainer", bookmarksSearchContainer);

		renderRequest.setAttribute(
			"currentUser",
			_currentUserFactory.getCurrentUser(
				emailEntryAddedEnabled, emailEntryUpdatedEnabled,
				themeDisplay));

		renderRequest.setAttribute("htmlUtil", new HtmlUtil());

		String navigation = searchContainerViewState.getNavigation();

		if (Validator.isNotNull(searchContainerViewState.getKeywords())) {
			SearchContext searchContext = SearchContextFactory.getInstance(
				httpServletRequest);

			_populateSearchContainerFromHits(
				folderId, scopeGroupId, bookmarksSearchContainer,
				searchContainerViewState, searchContext);
		}
		else if (Objects.equals(navigation, "mine") ||
				 Objects.equals(navigation, "recent")) {

			_populateSearchContainerByGroup(
				scopeGroupId, bookmarksSearchContainer,
				searchContainerViewState, themeDisplay.isSignedIn(),
				themeDisplay.getUserId());
		}
		else {
			RenderParameters renderParameters =
				renderRequest.getRenderParameters();

			long assetCategoryId = GetterUtil.getLong(
				renderParameters.getValue("categoryId"));
			String assetTagName = GetterUtil.getString(
				renderParameters.getValue("tag"));

			if ((assetCategoryId > 0) || Validator.isNotNull(assetTagName)) {
				_populateSearchContainerByAssetEntryQuery(
					scopeGroupId, bookmarksSearchContainer,
					searchContainerViewState);
			}
			else {
				_populateSearchContainerByFoldersAndEntries(
					folderId, scopeGroupId, bookmarksSearchContainer,
					searchContainerViewState);
			}
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(renderRequest);

		ManagementToolbarViewState managementToolbarViewState =
			_managementToolbarViewStateFactory.create(
				LanguageUtil.get(resourceBundle, "add-bookmark"),
				portalPreferences.getValue(
					BookmarksPortletKeys.BOOKMARKS, "display-style",
					"descriptive"),
				"all", "title", "asc", renderRequest, renderResponse, false,
				false, true, true);

		boolean trashEnabled = false;

		if (themeDisplay.isSignedIn() &&
			_trashHelper.isTrashEnabled(scopeGroupId)) {

			trashEnabled = true;
		}

		renderRequest.setAttribute(
			"managementToolbarViewState",
			new ManagementToolbarViewStateDisplayContextAdapter(
				new BookmarksManagementToolbarViewState(
					folderId, httpServletRequest,
					bookmarksSearchContainer.getTotal(),
					managementToolbarViewState, renderResponse.getNamespace(),
					renderRequest, renderResponse, showFoldersSearch,
					themeDisplay, trashEnabled)));

		try {
			BookmarksUtil.addPortletBreadcrumbEntries(
				bookmarksFolder, renderRequest, renderResponse,
				searchContainerViewState);
		}
		catch (Exception exception) {
			_log.error(exception.getMessage(), exception);
		}

		renderRequest.setAttribute("pathMain", themeDisplay.getPathMain());

		renderRequest.setAttribute("trashEnabled", trashEnabled);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		boolean defaultFolderView = false;

		if ((bookmarksFolder == null) &&
			(folderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			defaultFolderView = true;
		}

		if (navigation.equals("all") && !defaultFolderView &&
			(bookmarksFolder != null) &&
			(portletName.equals(BookmarksPortletKeys.BOOKMARKS) ||
			 portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN))) {

			_portal.setPageSubtitle(
				bookmarksFolder.getName(), themeDisplay.getRequest());
			_portal.setPageDescription(
				bookmarksFolder.getDescription(), themeDisplay.getRequest());
		}
		else {
			Layout layout = themeDisplay.getLayout();

			if (!layout.isTypeControlPanel()) {
				_portal.addPortletBreadcrumbEntry(
					themeDisplay.getRequest(),
					LanguageUtil.get(resourceBundle, navigation),
					themeDisplay.getURLCurrent());
			}

			_portal.setPageSubtitle(
				LanguageUtil.get(
					resourceBundle,
					StringUtil.replace(
						navigation, CharPool.UNDERLINE, CharPool.DASH)),
				themeDisplay.getRequest());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);

	@Reference
	private AssetEntryService _assetEntryService;

	@Reference
	private BookmarksEntryService _bookmarksEntryService;

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private BookmarksPortletToolbarContributor
		_bookmarksPortletToolbarContributor;

	@Reference
	private BookmarksSearchContainerFactory _bookmarksSearchContainerFactory;

	@Reference
	private CurrentUserFactory _currentUserFactory;

	@Reference
	private ManagementToolbarViewStateFactory
		_managementToolbarViewStateFactory;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}