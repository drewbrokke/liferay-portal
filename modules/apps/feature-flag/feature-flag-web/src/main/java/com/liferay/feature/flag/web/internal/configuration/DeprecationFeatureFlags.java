/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.feature.flag.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.feature.flag.web.internal.configuration.admin.category.FeatureFlagConfigurationCategory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Drew Brokke
 */
@ExtendedObjectClassDefinition(
	category = FeatureFlagConfigurationCategory.CATEGORY_KEY, generateUI = true
)
@Meta.OCD(
	id = "com.liferay.feature.flag.web.internal.configuration.DeprecationFeatureFlags"
)
public interface DeprecationFeatureFlags {

	@Meta.AD(name = "disabled-feature-flags-name", required = false)
	public String[] disabledFeatureFlags();

}