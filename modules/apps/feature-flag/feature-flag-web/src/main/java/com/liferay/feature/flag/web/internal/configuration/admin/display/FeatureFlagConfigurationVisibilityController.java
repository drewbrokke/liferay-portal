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

package com.liferay.feature.flag.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.io.Serializable;

/**
 * @author Drew Brokke
 */
public class FeatureFlagConfigurationVisibilityController
	implements ConfigurationVisibilityController {

	public FeatureFlagConfigurationVisibilityController(
		FeatureFlagManager featureFlagManager,
		GroupLocalService groupLocalService, String key) {

		_featureFlagManager = featureFlagManager;
		_groupLocalService = groupLocalService;
		_key = key;
	}

	@Override
	public boolean isVisible(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY)) {
			return _featureFlagManager.isEnabled((long)scopePK, _key);
		}
		else if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			Group group = _groupLocalService.fetchGroup((long)scopePK);

			if (group != null) {
				return _featureFlagManager.isEnabled(
					group.getCompanyId(), _key);
			}
		}

		return _featureFlagManager.isEnabled(_key);
	}

	private final FeatureFlagManager _featureFlagManager;
	private final GroupLocalService _groupLocalService;
	private final String _key;

}