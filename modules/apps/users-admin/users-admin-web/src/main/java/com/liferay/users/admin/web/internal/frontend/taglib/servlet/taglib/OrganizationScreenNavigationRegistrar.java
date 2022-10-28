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

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.permission.GroupPermission;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class OrganizationScreenNavigationRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_registerService(
			ScreenNavigationCategory.class, 10,
			new OrganizationScreenNavigationCategory(
				UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL));

		_registerService(
			ScreenNavigationCategory.class, 20,
			new OrganizationScreenNavigationCategory(
				UserScreenNavigationEntryConstants.CATEGORY_KEY_CONTACT));

		OrganizationScreenNavigationEntry.Mod dependenciesMod = args -> {
			args.organizationService = _organizationService;
			args.jspRenderer = _jspRenderer;
		};

		OrganizationScreenNavigationEntry.Mod categoryGeneralMod =
			dependenciesMod.and(
				OrganizationScreenNavigationEntry.Args.withCategoryKey(
					UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL));

		_registerService(
			ScreenNavigationEntry.class, 10,
			OrganizationScreenNavigationEntry.of(
				categoryGeneralMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"information"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/information.jsp"),
				OrganizationScreenNavigationEntry.Args.withMVCActionCommandName(
					"/users_admin/edit_organization"),
				OrganizationScreenNavigationEntry.Args.withVisibleBiFunction(
					(user, organization) -> true)));
		_registerService(
			ScreenNavigationEntry.class, 20,
			OrganizationScreenNavigationEntry.of(
				categoryGeneralMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"organization-site"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/organization_site.jsp"),
				OrganizationScreenNavigationEntry.Args.withMVCActionCommandName(
					"/users_admin/update_organization_organization_site"),
				OrganizationScreenNavigationEntry.Args::hideControls,
				OrganizationScreenNavigationEntry.Args.withVisibleBiFunction(
					(user, organization) -> {
						if (organization == null) {
							return false;
						}

						try {
							if (!_groupPermission.contains(
									PermissionThreadLocal.
										getPermissionChecker(),
									organization.getGroup(),
									ActionKeys.UPDATE)) {

								return false;
							}
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}

							return false;
						}

						return true;
					})));
		_registerService(
			ScreenNavigationEntry.class, 30,
			OrganizationScreenNavigationEntry.of(
				categoryGeneralMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"security-questions"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/reminder_queries.jsp"),
				OrganizationScreenNavigationEntry.Args.withMVCActionCommandName(
					"/users_admin/update_organization_reminder_queries")));

		OrganizationScreenNavigationEntry.Mod categoryContactMod =
			OrganizationScreenNavigationEntry.Mod.combine(
				dependenciesMod,
				OrganizationScreenNavigationEntry.Args.withCategoryKey(
					UserScreenNavigationEntryConstants.CATEGORY_KEY_CONTACT),
				OrganizationScreenNavigationEntry.Args.withMVCActionCommandName(
					"/users_admin/update_contact_information"),
				OrganizationScreenNavigationEntry.Args.HIDE_CONTROLS);

		_registerService(
			ScreenNavigationEntry.class, 10,
			OrganizationScreenNavigationEntry.of(
				categoryContactMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"addresses"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/addresses.jsp"),
				OrganizationScreenNavigationEntry.Args.HIDE_TITLE));
		_registerService(
			ScreenNavigationEntry.class, 20,
			OrganizationScreenNavigationEntry.of(
				categoryContactMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"contact-information"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/contact_information.jsp")));
		_registerService(
			ScreenNavigationEntry.class, 30,
			OrganizationScreenNavigationEntry.of(
				categoryContactMod,
				OrganizationScreenNavigationEntry.Args.withEntryKey(
					"opening-hours"),
				OrganizationScreenNavigationEntry.Args.withJspPath(
					"/organization/opening_hours.jsp"),
				OrganizationScreenNavigationEntry.Args.HIDE_TITLE));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	private <T> void _registerService(
		Class<T> clazz, int order, T serviceObject) {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				clazz, serviceObject,
				HashMapDictionaryBuilder.<String, Object>put(
					"screen.navigation.category.order", order
				).put(
					"screen.navigation.entry.order", order
				).build()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationScreenNavigationRegistrar.class);

	private BundleContext _bundleContext;

	@Reference
	private GroupPermission _groupPermission;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private OrganizationService _organizationService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}