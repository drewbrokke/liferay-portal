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

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Drew Brokke
 */
@Component(
	configurationPid = "com.liferay.feature.flag.web.internal.configuration.DeprecationFeatureFlags",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = ModelListener.class
)
public class DeprecationFeatureFlagsCompanyModelListener
	extends BaseModelListener<Company> {

	@Activate
	protected void activate(Map<String, Object> properties) {
		System.out.println("hello activate");
	}

	@Deactivate
	protected void deactivate() {
		System.out.println("goodbye deactivate");
	}

}