/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.feature.flag;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.internal.instance.lifecycle.DepotRolesPortalInstanceLifecycleListener;
import com.liferay.depot.internal.util.DepotRoleUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "feature.flag.key=LPD-17564", service = FeatureFlagListener.class
)
public class CMSFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled || !Objects.equals(featureFlagKey, "LPD-17564")) {
			return;
		}

		for (String name : DepotRoleUtil.DEPOT_ROLE_NAMES) {
			Role role = _roleLocalService.fetchRole(companyId, name);

			if (role == null) {
				continue;
			}

			Map<Locale, String> titleMap = _getTitleMap(_language, name);

			if (Objects.equals(titleMap, role.getTitleMap())) {
				continue;
			}

			role.setTitleMap(titleMap);

			_roleLocalService.updateRole(role);
		}
	}

	private String _getTitle(Locale locale, String name) {
		String title = _titleKeys.get(name);

		if (Validator.isNull(title)) {
			return name;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, DepotRolesPortalInstanceLifecycleListener.class);

		return ResourceBundleUtil.getString(resourceBundle, title);
	}

	private Map<Locale, String> _getTitleMap(Language language, String name) {
		Map<Locale, String> titleMap = new HashMap<>();

		for (Locale locale : language.getAvailableLocales()) {
			String title = _getTitle(locale, name);

			if (title != null) {
				titleMap.put(locale, title);
			}
		}

		return titleMap;
	}

	private static final Map<String, String> _titleKeys;

	static {
		_titleKeys = Collections.unmodifiableMap(
			HashMapBuilder.put(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				"space-administrator"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
				"space-connected-site-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				"space-content-reviewer"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER, "space-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_OWNER, "space-owner"
			).build());
	}

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}