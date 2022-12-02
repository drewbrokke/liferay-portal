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

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	service = {
		CompanyFeatureFlagsProvider.class, PortalInstanceLifecycleListener.class
	}
)
public class CompanyFeatureFlagsProvider
	implements PortalInstanceLifecycleListener {

	public CompanyFeatureFlags getCompanyFeatureFlags(long companyId) {
		return _featureFlagsMap.get(companyId);
	}

	@Override
	public void portalInstanceRegistered(Company company) {
		_featureFlagsMap.put(
			company.getCompanyId(),
			new CompanyFeatureFlags(
				company.getCompanyId(), _featureFlagsPreferencesHelper,
				_language));
	}

	@Override
	public void portalInstanceUnregistered(Company company) {
		_featureFlagsMap.remove(company.getCompanyId());
	}

	public <T> T withCompanyFeatureFlags(
		long companyId,
		Function<CompanyFeatureFlags, T> companyFeatureFlagsFunction) {

		return companyFeatureFlagsFunction.apply(
			_featureFlagsMap.get(companyId));
	}

	@Activate
	protected void activate() {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			_featureFlagsMap.put(
				CompanyConstants.SYSTEM,
				new CompanyFeatureFlags(
					CompanyConstants.SYSTEM, _featureFlagsPreferencesHelper,
					_language));
		}
	}

	private final Map<Long, CompanyFeatureFlags> _featureFlagsMap =
		new ConcurrentHashMap<>();

	@Reference
	private FeatureFlagsPreferencesHelper _featureFlagsPreferencesHelper;

	@Reference
	private Language _language;

}