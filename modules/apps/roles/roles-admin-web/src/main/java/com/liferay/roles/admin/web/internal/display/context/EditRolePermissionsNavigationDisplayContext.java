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

package com.liferay.roles.admin.web.internal.display.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.PanelCategoryRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.PortletCategoryConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.AdministratorControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.OmniadminControlPanelEntry;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;
import com.liferay.portal.util.WebAppPool;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.portlet.RenderResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Evan Thibodeau
 */
public class EditRolePermissionsNavigationDisplayContext {

	public EditRolePermissionsNavigationDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse,
		Role role, Boolean accountRoleGroupScope) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
		_role = role;
		_accountRoleGroupScope = accountRoleGroupScope;

		_panelCategoryRegistry =
			(PanelCategoryRegistry)httpServletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_CATEGORY_REGISTRY);
		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);
		_personalMenuEntryHelper =
			(PersonalMenuEntryHelper)httpServletRequest.getAttribute(
				ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER);
		_locale = httpServletRequest.getLocale();
		_servletContext = httpServletRequest.getServletContext();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() {
		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.convertValue(_getTopLevelNavItem(), Map.class);
	}

	private NavItem _getApplicationsNavItem() {
		Set<String> hiddenPortletIds = Collections.emptySet();

		PortletCategory portletCategory = (PortletCategory)WebAppPool.get(
			_themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory hiddenPortletCategory = portletCategory.getCategory(
			PortletCategoryConstants.NAME_HIDDEN);

		if (hiddenPortletCategory != null) {
			hiddenPortletIds = hiddenPortletCategory.getPortletIds();
		}

		List<NavItem> applicationNavItems = new ArrayList<>();

		boolean includeSystemPortlets = false;

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets(
			_themeDisplay.getCompanyId(), includeSystemPortlets, false);

		portlets = ListUtil.sort(
			portlets, new PortletTitleComparator(_servletContext, _locale));

		for (Portlet portlet : portlets) {
			String portletId = portlet.getPortletId();

			if (Validator.isNull(portletId) ||
				hiddenPortletIds.contains(portletId)) {

				continue;
			}

			String label = PortalUtil.getPortletLongTitle(
				portlet, _servletContext, _locale);

			applicationNavItems.add(
				NavItem.create(
					label, _getPortletResourceNavItemConsumer(portletId)));
		}

		return NavItem.create(
			LanguageUtil.get(_locale, "applications"),
			navItem -> navItem.addItems(applicationNavItems));
	}

	private String _getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		return _backURL;
	}

	private String _getEditPermissionsResourceURL(String portletResource) {
		return ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setMVCPath(
			"/view_resources.jsp"
		).setCMD(
			Constants.EDIT
		).setBackURL(
			_getBackURL()
		).setPortletResource(
			portletResource
		).setTabs2(
			"roles"
		).setParameter(
			"accountRoleGroupScope", _accountRoleGroupScope
		).setParameter(
			"roleId", _role.getRoleId()
		).setParameter(
			"p_p_isolated", "true"
		).buildString();
	}

	private NavItem _getPanelCategoryNavItem(
		PanelCategory panelCategory, String[] excludedPanelAppKeys) {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			panelCategory);

		if (panelApps.isEmpty()) {
			return null;
		}

		List<NavItem> navItems = new ArrayList<>();

		for (PanelApp panelApp : panelApps) {
			Portlet panelAppPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), panelApp.getPortletId());

			if (_isPortletType(
					AdministratorControlPanelEntry.class, panelAppPortlet) ||
				_isPortletType(
					OmniadminControlPanelEntry.class, panelAppPortlet) ||
				ArrayUtil.contains(
					excludedPanelAppKeys, panelApp.getPortletId())) {

				continue;
			}

			String label = PortalUtil.getPortletLongTitle(
				panelAppPortlet, _servletContext, _locale);
			String portletResource = panelAppPortlet.getPortletId();

			navItems.add(
				NavItem.create(
					label,
					_getPortletResourceNavItemConsumer(portletResource)));
		}

		return NavItem.create(
			panelCategory.getLabel(_locale),
			navItem -> navItem.addItems(navItems));
	}

	private List<NavItem> _getPanelCategoryNavItems(String panelCategoryKey) {
		List<NavItem> items = new ArrayList<>();

		for (PanelCategory panelCategory :
				_panelCategoryRegistry.getChildPanelCategories(
					panelCategoryKey)) {

			NavItem panelCategoryNavItem = _getPanelCategoryNavItem(
				panelCategory, new String[0]);

			if (panelCategoryNavItem != null) {
				items.add(panelCategoryNavItem);
			}
		}

		return items;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private Consumer<NavItem> _getPortletResourceNavItemConsumer(
		String portletResource) {

		return navItem -> {
			navItem.setActive(_portletResource.equals(portletResource));
			navItem.put(
				"resourceURL", _getEditPermissionsResourceURL(portletResource));
		};
	}

	private List<NavItem> _getSiteAdministrationPanelCategoryNavItems() {
		List<NavItem> siteAdministrationPanelCategoryNavItems =
			new ArrayList<>();

		for (PanelCategory panelCategory :
				_panelCategoryRegistry.getChildPanelCategories(
					PanelCategoryKeys.SITE_ADMINISTRATION)) {

			NavItem panelCategoryNavItem = _getUnfilteredPanelCategoryNavItem(
				panelCategory);

			if (panelCategoryNavItem != null) {
				siteAdministrationPanelCategoryNavItems.add(
					panelCategoryNavItem);
			}
		}

		return siteAdministrationPanelCategoryNavItems;
	}

	private NavItem _getSummaryNavItem() {
		return NavItem.create(
			LanguageUtil.get(_locale, "summary"),
			navItem -> {
				navItem.setActive(Validator.isNull(_getPortletResource()));
				navItem.put("className", "mb-4");
				navItem.put("ignoreFilter", true);
				navItem.put(
					"resourceURL",
					ResourceURLBuilder.createResourceURL(
						_renderResponse
					).setMVCPath(
						"/view_resources.jsp"
					).setCMD(
						Constants.VIEW
					).setBackURL(
						_getBackURL()
					).setTabs1(
						"roles"
					).setParameter(
						"accountRoleGroupScope", _accountRoleGroupScope
					).setParameter(
						"roleId", _role.getRoleId()
					).setParameter(
						"p_p_isolated", "true"
					).buildString());
			});
	}

	private NavItem _getTopLevelNavItem() {
		NavItem topLevelNavItem = new NavItem(null);

		topLevelNavItem.addItems(_getSummaryNavItem());

		int roleType = _role.getType();

		if (roleType == RoleConstants.TYPE_ORGANIZATION) {
			topLevelNavItem.addItems(_getUsersAndOrganizationsNavItem());
		}
		else if (roleType == RoleConstants.TYPE_REGULAR) {
			topLevelNavItem.addItems(
				NavItem.create(
					LanguageUtil.get(_locale, "control-panel"),
					navItem -> {
						navItem.addItems(
							NavItem.create(
								LanguageUtil.get(
									_locale, "general-permissions"),
								_getPortletResourceNavItemConsumer(
									PortletKeys.PORTAL)));
						navItem.addItems(
							_getPanelCategoryNavItems(
								PanelCategoryKeys.CONTROL_PANEL));
						navItem.setInitialExpanded(true);
					}),
				NavItem.create(
					LanguageUtil.get(_locale, "commerce"),
					navItem -> {
						navItem.addItems(
							_getPanelCategoryNavItems(
								PanelCategoryKeys.COMMERCE));
						navItem.setInitialExpanded(true);
					}),
				NavItem.create(
					LanguageUtil.get(_locale, "applications-menu"),
					navItem -> {
						navItem.addItems(
							_getPanelCategoryNavItems(
								PanelCategoryKeys.
									APPLICATIONS_MENU_APPLICATIONS));
						navItem.setInitialExpanded(true);
					}));
		}

		if (!_accountRoleGroupScope) {
			String[] excludedPanelAppKeys =
				(String[])_httpServletRequest.getAttribute(
					RolesAdminWebKeys.EXCLUDED_PANEL_APP_KEYS);

			for (String panelCategoryKey :
					(String[])_httpServletRequest.getAttribute(
						RolesAdminWebKeys.PANEL_CATEGORY_KEYS)) {

				NavItem panelCategoryNavItem = _getPanelCategoryNavItem(
					_panelCategoryRegistry.getPanelCategory(panelCategoryKey),
					excludedPanelAppKeys);

				if (panelCategoryNavItem != null) {
					topLevelNavItem.addItems(panelCategoryNavItem);
				}
			}
		}

		topLevelNavItem.addItems(
			NavItem.create(
				LanguageUtil.get(
					_locale, "site-and-asset-library-administration"),
				navItem -> {
					navItem.addItems(
						_getSiteAdministrationPanelCategoryNavItems());
					navItem.addItems(_getApplicationsNavItem());
				}));

		if (roleType == RoleConstants.TYPE_REGULAR) {
			topLevelNavItem.addItems(
				NavItem.create(
					LanguageUtil.get(_locale, "user"),
					navItem -> navItem.addItems(_getUserNavItemsJSONArray())));

			List<PanelCategory> panelCategories = new ArrayList<>();

			panelCategories.addAll(
				_panelCategoryRegistry.getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU));
			panelCategories.addAll(
				_panelCategoryRegistry.getChildPanelCategories(
					PanelCategoryKeys.ROOT));

			for (PanelCategory panelCategory : panelCategories) {
				if (ListUtil.isNotEmpty(
						_panelAppRegistry.getPanelApps(panelCategory))) {

					NavItem panelCategoryNavItem =
						_getUnfilteredPanelCategoryNavItem(panelCategory);

					if (panelCategoryNavItem != null) {
						topLevelNavItem.addItems(panelCategoryNavItem);
					}
				}
			}
		}

		return topLevelNavItem;
	}

	private NavItem _getUnfilteredPanelCategoryNavItem(
		PanelCategory panelCategory) {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			panelCategory);

		if (panelApps.isEmpty()) {
			return null;
		}

		return NavItem.create(
			panelCategory.getLabel(_locale),
			navItem -> {
				for (PanelApp panelApp : panelApps) {
					Portlet panelAppPortlet =
						PortletLocalServiceUtil.getPortletById(
							_themeDisplay.getCompanyId(),
							panelApp.getPortletId());

					String label = PortalUtil.getPortletLongTitle(
						panelAppPortlet, _servletContext, _locale);
					String portletResource = panelAppPortlet.getPortletId();

					navItem.addItems(
						NavItem.create(
							label,
							_getPortletResourceNavItemConsumer(
								portletResource)));
				}
			});
	}

	private List<NavItem> _getUserNavItemsJSONArray() {
		List<NavItem> userNavItems = new ArrayList<>();

		for (BasePersonalMenuEntry basePersonalMenuEntry :
				_personalMenuEntryHelper.getBasePersonalMenuEntries()) {

			Portlet personalPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(),
				basePersonalMenuEntry.getPortletId());

			String label = PortalUtil.getPortletLongTitle(
				personalPortlet, _servletContext, _locale);
			String portletResource = personalPortlet.getPortletId();

			userNavItems.add(
				NavItem.create(
					label,
					_getPortletResourceNavItemConsumer(portletResource)));
		}

		return userNavItems;
	}

	private NavItem _getUsersAndOrganizationsNavItem() {
		Portlet usersAdminPortlet = PortletLocalServiceUtil.getPortletById(
			_themeDisplay.getCompanyId(),
			PortletProviderUtil.getPortletId(
				User.class.getName(), PortletProvider.Action.VIEW));

		String label = PortalUtil.getPortletLongTitle(
			usersAdminPortlet, _servletContext, _locale);
		String portletResource = usersAdminPortlet.getPortletId();

		return NavItem.create(
			label, _getPortletResourceNavItemConsumer(portletResource));
	}

	private boolean _isPortletType(
		Class<? extends ControlPanelEntry> controlPanelEntryClass,
		Portlet portlet) {

		if (Objects.equals(
				controlPanelEntryClass.getName(),
				portlet.getControlPanelEntryClass())) {

			return true;
		}

		ControlPanelEntry controlPanelEntryInstance =
			portlet.getControlPanelEntryInstance();

		if (controlPanelEntryClass.isAssignableFrom(
				controlPanelEntryInstance.getClass())) {

			return true;
		}

		return false;
	}

	private final Boolean _accountRoleGroupScope;
	private String _backURL;
	private final HttpServletRequest _httpServletRequest;
	private final Locale _locale;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategoryRegistry _panelCategoryRegistry;
	private final PersonalMenuEntryHelper _personalMenuEntryHelper;
	private String _portletResource;
	private final RenderResponse _renderResponse;
	private final Role _role;
	private final ServletContext _servletContext;
	private final ThemeDisplay _themeDisplay;

}