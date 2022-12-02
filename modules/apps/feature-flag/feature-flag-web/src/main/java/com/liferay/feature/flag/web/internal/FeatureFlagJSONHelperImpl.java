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

package com.liferay.feature.flag.web.internal;

import com.liferay.portal.feature.flag.FeatureFlagJSONHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagJSONHelper.class)
public class FeatureFlagJSONHelperImpl implements FeatureFlagJSONHelper {

	@Override
	public String getFeatureFlagJSON(long companyId) {
		return _companyFeatureFlagsProvider.withCompanyFeatureFlags(
			companyId, CompanyFeatureFlags::getFeatureFlagsJSON);
	}

	@Reference
	private CompanyFeatureFlagsProvider _companyFeatureFlagsProvider;

}