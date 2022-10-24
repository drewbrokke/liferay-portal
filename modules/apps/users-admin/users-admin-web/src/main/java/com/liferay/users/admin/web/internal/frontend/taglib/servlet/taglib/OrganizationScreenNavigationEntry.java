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

package com.liferay.users.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;
import com.liferay.users.admin.web.internal.display.context.OrganizationScreenNavigationDisplayContext;

import java.io.IOException;

import java.util.Locale;
import java.util.function.BiFunction;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Drew Brokke
 */
public class OrganizationScreenNavigationEntry
	implements ScreenNavigationEntry<Organization> {

	public static OrganizationScreenNavigationEntry of(Mod... mods) {
		Args args = new Args();

		for (Mod mod : mods) {
			mod.modify(args);
		}

		return new OrganizationScreenNavigationEntry(
			args.jspRenderer, args.organizationService, args.entryKey,
			args.categoryKey, args.jspPath, args.mvcActionCommandName,
			args.showControls, args.showTitle, args.visibleBiFunction);
	}

	@Override
	public String getCategoryKey() {
		return _categoryKey;
	}

	@Override
	public String getEntryKey() {
		return _entryKey;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, _entryKey);
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_ORGANIZATIONS;
	}

	@Override
	public boolean isVisible(User user, Organization organization) {
		return _visibleBiFunction.apply(user, organization);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		OrganizationScreenNavigationDisplayContext
			organizationScreenNavigationDisplayContext =
				new OrganizationScreenNavigationDisplayContext();

		organizationScreenNavigationDisplayContext.setActionName(
			_mvcActionCommandName);

		String backURL = ParamUtil.getString(httpServletRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"toolbarItem", "view-all-organizations"
			).setParameter(
				"usersListView", UserConstants.LIST_VIEW_FLAT_ORGANIZATIONS
			).buildString();
		}

		organizationScreenNavigationDisplayContext.setBackURL(backURL);

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/users_admin/edit_organization"
			).setBackURL(
				backURL
			).setParameter(
				"organizationId",
				ParamUtil.getString(httpServletRequest, "organizationId")
			).setParameter(
				"screenNavigationCategoryKey",
				ParamUtil.getString(
					httpServletRequest, "screenNavigationCategoryKey",
					UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL)
			).setParameter(
				"screenNavigationEntryKey",
				ParamUtil.getString(
					httpServletRequest, "screenNavigationEntryKey")
			).buildString();
		}

		organizationScreenNavigationDisplayContext.setRedirect(redirect);

		organizationScreenNavigationDisplayContext.setFormLabel(
			getLabel(httpServletRequest.getLocale()));
		organizationScreenNavigationDisplayContext.setJspPath(_jspPath);

		long organizationId = ParamUtil.getLong(
			httpServletRequest, "organizationId");

		Organization organization = null;

		try {
			organization = _organizationService.fetchOrganization(
				organizationId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		organizationScreenNavigationDisplayContext.setOrganization(
			organization);
		organizationScreenNavigationDisplayContext.setOrganizationId(
			organizationId);
		organizationScreenNavigationDisplayContext.setShowControls(
			_showControls);
		organizationScreenNavigationDisplayContext.setShowTitle(_showTitle);

		httpServletRequest.setAttribute(
			UsersAdminWebKeys.ORGANIZATION_SCREEN_NAVIGATION_DISPLAY_CONTEXT,
			organizationScreenNavigationDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/edit_organization_navigation.jsp");
	}

	public static class Args {

		public static final Mod HIDE_CONTROLS =
			args -> args.showControls = false;

		public static final Mod HIDE_TITLE = args -> args.showTitle = false;

		public static void hideControls(Args args) {
			args.showControls = false;
		}

		public static Mod withCategoryKey(String categoryKey) {
			return args -> args.categoryKey = categoryKey;
		}

		public static Mod withEntryKey(String entryKey) {
			return args -> args.entryKey = entryKey;
		}

		public static Mod withJspPath(String jspPath) {
			return args -> args.jspPath = jspPath;
		}

		public static Mod withMVCActionCommandName(
			String mvcActionCommandName) {

			return args -> args.mvcActionCommandName = mvcActionCommandName;
		}

		public static Mod withVisibleBiFunction(
			BiFunction<User, Organization, Boolean> visibleBiFunction) {

			return args -> args.visibleBiFunction = visibleBiFunction;
		}

		public String categoryKey;
		public String entryKey;
		public String jspPath;
		public JSPRenderer jspRenderer;
		public String mvcActionCommandName;
		public OrganizationService organizationService;
		public boolean showControls = true;
		public boolean showTitle = true;

		public BiFunction<User, Organization, Boolean> visibleBiFunction =
			(user, organization) -> {
				if (organization == null) {
					return false;
				}

				return true;
			};

	}

	@FunctionalInterface
	public interface Mod {

		public static Mod combine(Mod... mods) {
			return args -> {
				for (Mod mod : mods) {
					mod.modify(args);
				}
			};
		}

		public default Mod and(Mod... mods) {
			return args -> {
				modify(args);

				for (Mod mod : mods) {
					mod.modify(args);
				}
			};
		}

		public void modify(Args args);

	}

	private OrganizationScreenNavigationEntry(
		JSPRenderer jspRenderer, OrganizationService organizationService,
		String entryKey, String categoryKey, String jspPath,
		String mvcActionCommandName, boolean showControls, boolean showTitle,
		BiFunction<User, Organization, Boolean> visibleBiFunction) {

		_jspRenderer = jspRenderer;
		_organizationService = organizationService;
		_entryKey = entryKey;
		_categoryKey = categoryKey;
		_jspPath = jspPath;
		_mvcActionCommandName = mvcActionCommandName;
		_showControls = showControls;
		_showTitle = showTitle;
		_visibleBiFunction = visibleBiFunction;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationScreenNavigationEntry.class);

	private final String _categoryKey;
	private final String _entryKey;
	private final String _jspPath;
	private final JSPRenderer _jspRenderer;
	private final String _mvcActionCommandName;
	private final OrganizationService _organizationService;
	private final boolean _showControls;
	private final boolean _showTitle;
	private final BiFunction<User, Organization, Boolean> _visibleBiFunction;

}