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

import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	service = {
		FeatureFlagsProvider.class, PortalInstanceLifecycleListener.class
	}
)
public class FeatureFlagsProvider implements PortalInstanceLifecycleListener {

	public FeatureFlags getFeatureFlags(long companyId) {
		return _featureFlagsMap.get(companyId);
	}

	public boolean isEnabled(long companyId, String key) {
		FeatureFlags featureFlags = getFeatureFlags(companyId);

		return featureFlags.isEnabled(key);
	}

	public boolean isEnabled(String key) {
		return isEnabled(CompanyThreadLocal.getCompanyId(), key);
	}

	@Override
	public void portalInstanceRegistered(Company company) {
		_featureFlagsMap.put(
			company.getCompanyId(), new FeatureFlags(company.getCompanyId()));
	}

	@Override
	public void portalInstanceUnregistered(Company company) {
		_featureFlagsMap.remove(company.getCompanyId());
	}

	private final Map<Long, FeatureFlags> _featureFlagsMap =
		new ConcurrentHashMap<>();

}