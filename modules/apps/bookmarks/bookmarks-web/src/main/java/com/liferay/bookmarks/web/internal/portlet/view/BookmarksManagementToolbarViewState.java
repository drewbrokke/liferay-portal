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

package com.liferay.bookmarks.web.internal.portlet.view;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.bookmarks.web.internal.portlet.toolbar.contributor.BookmarksPortletToolbarContributor;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksFolderPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.frontend.taglib.clay.view.state.ManagementToolbarViewState;
import com.liferay.frontend.taglib.clay.view.state.ManagementToolbarViewStateWrapper;
import com.liferay.petra.portlet.url.builder.ActionURLBuilder;
import com.liferay.petra.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.List;
import java.util.Map;

import javax.portlet.MutableRenderParameters;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.RenderURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Neil Griffin
 */
public class BookmarksManagementToolbarViewState
	extends ManagementToolbarViewStateWrapper {

	public BookmarksManagementToolbarViewState(
		long folderId, HttpServletRequest httpServletRequest, int itemsTotal,
		ManagementToolbarViewState managementToolbarViewState, String namespace,
		RenderRequest renderRequest, RenderResponse renderResponse,
		boolean showFoldersSearch, ThemeDisplay themeDisplay,
		boolean trashEnabled) {

		super(managementToolbarViewState);

		_folderId = folderId;
		_httpServletRequest = httpServletRequest;
		_itemsTotal = itemsTotal;
		_namespace = namespace;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_showFoldersSearch = showFoldersSearch;
		_themeDisplay = themeDisplay;
		_trashEnabled = trashEnabled;

		RenderParameters renderParameters = renderRequest.getRenderParameters();

		_bookmarkId = GetterUtil.getString(
			renderParameters.getValue("bookmarkId"));

		_mvcRenderCommandName = GetterUtil.getString(
			renderParameters.getValue("mvcRenderCommandName"),
			"~bookmarks~view_bookmarks");

		_navigation = GetterUtil.getString(
			renderParameters.getValue("navigation"), "all");
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");

				if (_trashEnabled) {
					dropdownItem.setIcon("trash");
					dropdownItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest, "move-to-recycle-bin"));
				}
				else {
					dropdownItem.setIcon("times-circle");
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "delete"));
				}

				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public RenderURL getAddEntryURL() {
		if (_addEntryURL == null) {
			_addEntryURL = super.getAddEntryURL();

			MutableRenderParameters mutableRenderParameters =
				_addEntryURL.getRenderParameters();

			mutableRenderParameters.setValue("bookmarkId", "0");
			mutableRenderParameters.setValue(
				"mvcRenderCommandName", "~bookmarks~add_entry");
		}

		return _addEntryURL;
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteEntriesURL",
			() -> ActionURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"~bookmarks~edit_entry"
			).buildString()
		).put(
			"inputId", Constants.CMD
		).put(
			"inputValue",
			() -> {
				if (_trashEnabled) {
					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).put(
			"trashEnabled", _trashEnabled
		).build();
	}

	public RenderURL getClearResultsURL() {
		if (_clearResultsURL == null) {
			_clearResultsURL = RenderURLBuilder.createRenderURL(
				super.getClearResultsURL()
			).setMVCRenderCommandName(
				_mvcRenderCommandName, false
			).setNavigation(
				"all"
			).setParameter(
				"bookmarkId", _bookmarkId, false
			).buildRenderURL();
		}

		return _clearResultsURL;
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!isShowCreationMenu()) {
			return null;
		}

		BookmarksPortletToolbarContributor bookmarksPortletToolbarContributor =
			(BookmarksPortletToolbarContributor)
				_httpServletRequest.getAttribute(
					BookmarksWebKeys.BOOKMARKS_PORTLET_TOOLBAR_CONTRIBUTOR);

		List<Menu> menus =
			bookmarksPortletToolbarContributor.getPortletTitleMenus(
				_renderRequest, _renderResponse);

		if (menus.isEmpty()) {
			return null;
		}

		CreationMenu creationMenu = new CreationMenu();

		for (Menu menu : menus) {
			List<URLMenuItem> urlMenuItems =
				(List<URLMenuItem>)(List<?>)menu.getMenuItems();

			for (URLMenuItem urlMenuItem : urlMenuItems) {
				creationMenu.addDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(urlMenuItem.getURL());
						dropdownItem.setLabel(urlMenuItem.getLabel());
					});
			}
		}

		return creationMenu;
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "filter-by-navigation"));
			}
		).build();
	}

	public List<LabelItem> getFilterLabelItems() {
		String removeNavLabelURL = _getRemoveNavigationLabelURL();

		return LabelItemListBuilder.add(
			() -> _navigation.equals("mine"),
			labelItem -> {
				labelItem.putData("renderURL", removeNavLabelURL);

				labelItem.setDismissible(true);

				User user = _themeDisplay.getUser();

				String label = String.format(
					"%s: %s", LanguageUtil.get(_httpServletRequest, "owner"),
					user.getFullName());

				labelItem.setLabel(label);
			}
		).add(
			() -> _navigation.equals("recent"),
			labelItem -> {
				labelItem.putData("renderURL", removeNavLabelURL);

				labelItem.setCloseable(true);

				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "recent"));
			}
		).build();
	}

	@Override
	public int getItemsTotal() {
		return _itemsTotal;
	}

	@Override
	public String getNamespace() {
		return _namespace;
	}

	@Override
	public RenderURL getSearchActionURL() {
		return getSearchURL();
	}

	@Override
	public RenderURL getSortingURL() {
		if (_sortingURL == null) {
			_sortingURL = RenderURLBuilder.createRenderURL(
				super.getSortingURL()
			).setMVCRenderCommandName(
				_mvcRenderCommandName, false
			).setParameter(
				"bookmarkId", _bookmarkId, false
			).buildRenderURL();
		}

		return _sortingURL;
	}

	@Override
	public RenderURL getSortingURLCurrent() {
		if (_sortingURLCurrent == null) {
			_sortingURLCurrent = RenderURLBuilder.createRenderURL(
				super.getSortingURLCurrent()
			).setMVCRenderCommandName(
				_mvcRenderCommandName, false
			).setParameter(
				"bookmarkId", _bookmarkId, false
			).buildRenderURL();
		}

		return _sortingURLCurrent;
	}

	@Override
	public RenderURL getSortingURLReverse() {
		if (_sortingURLReverse == null) {
			_sortingURLReverse = RenderURLBuilder.createRenderURL(
				super.getSortingURLReverse()
			).setMVCRenderCommandName(
				_mvcRenderCommandName, false
			).setParameter(
				"bookmarkId", _bookmarkId, false
			).buildRenderURL();
		}

		return _sortingURLReverse;
	}

	@Override
	public boolean isDisabled() {
		int foldersAndEntriesCount =
			BookmarksFolderServiceUtil.getFoldersAndEntriesCount(
				_themeDisplay.getScopeGroupId(), _folderId);

		if ((foldersAndEntriesCount == 0) && _navigation.equals("all")) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isShowCreationMenu() {
		if (_showCreationMenu == null) {
			_showCreationMenu = false;

			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			String portletName = portletDisplay.getPortletName();

			if (portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN) &&
				(BookmarksFolderPermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), _folderId,
					ActionKeys.ADD_ENTRY) ||
				 BookmarksFolderPermission.contains(
					 _themeDisplay.getPermissionChecker(),
					 _themeDisplay.getScopeGroupId(), _folderId,
					 ActionKeys.ADD_FOLDER))) {

				_showCreationMenu = true;
			}
		}

		return _showCreationMenu;
	}

	@Override
	public boolean isShowInfoButton() {
		return true;
	}

	@Override
	public boolean isShowSearch() {
		return _showFoldersSearch;
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				String[] navigationKeys = null;

				if (_themeDisplay.isSignedIn()) {
					navigationKeys = new String[] {"all", "recent", "mine"};
				}
				else {
					navigationKeys = new String[] {"all", "recent"};
				}

				for (String navigationKey : navigationKeys) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								navigationKey.equals(_navigation));

							dropdownItem.setHref(
								RenderURLBuilder.createRenderURL(
									(RenderURL)PortletURLUtil.clone(
										getSortingURLCurrent(), _renderResponse)
								).setMVCRenderCommandName(
									_mvcRenderCommandName, false
								).setNavigation(
									navigationKey
								).setParameter(
									"bookmarkId", _bookmarkId, false
								).buildRenderURL());

							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, navigationKey));
						});
				}
			}
		};
	}

	private String _getRemoveNavigationLabelURL() {
		if (_removeNavigationLabelURL == null) {
			RenderURL renderURL = super.getSortingURLCurrent();

			MutableRenderParameters mutableRenderParameters =
				renderURL.getRenderParameters();

			mutableRenderParameters.setValue("navigation", "all");

			_removeNavigationLabelURL = renderURL.toString();
		}

		return _removeNavigationLabelURL;
	}

	private RenderURL _addEntryURL;
	private final String _bookmarkId;
	private RenderURL _clearResultsURL;
	private final long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private final int _itemsTotal;
	private final String _mvcRenderCommandName;
	private final String _namespace;
	private final String _navigation;
	private String _removeNavigationLabelURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Boolean _showCreationMenu;
	private final boolean _showFoldersSearch;
	private RenderURL _sortingURL;
	private RenderURL _sortingURLCurrent;
	private RenderURL _sortingURLReverse;
	private final ThemeDisplay _themeDisplay;
	private final boolean _trashEnabled;

}