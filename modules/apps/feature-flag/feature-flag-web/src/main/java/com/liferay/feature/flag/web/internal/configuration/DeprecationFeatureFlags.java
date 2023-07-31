/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.feature.flag.web.internal.configuration.admin.category.FeatureFlagConfigurationCategory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Drew Brokke
 * @author Thiago Buarque
 */
@ExtendedObjectClassDefinition(
	category = FeatureFlagConfigurationCategory.CATEGORY_KEY
)
@Meta.OCD(
	id = "com.liferay.feature.flag.web.internal.configuration.DeprecationFeatureFlags"
)
public interface DeprecationFeatureFlags {

	@Meta.AD(name = "disabled-deprecated-feature-flags", required = false)
	public String[] disabledFeatureFlags();

}