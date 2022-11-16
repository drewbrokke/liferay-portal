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

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Drew Brokke
 */
@Component(
	property = "frontend.data.set.name=" + FeatureFlagFDSConstants.FRONTEND_DATA_SET_KEY,
	service = FDSFilter.class
)
public class FeatureFlagsStatusSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.COLLECTION;
	}

	@Override
	public String getId() {
		return "status";
	}

	@Override
	public String getLabel() {
		return "status";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return Arrays.stream(FeatureFlag.Status.values()).map(status -> new SelectionFDSFilterItem(status.toString(), status.toString())).collect(
			Collectors.toList());
	}

}