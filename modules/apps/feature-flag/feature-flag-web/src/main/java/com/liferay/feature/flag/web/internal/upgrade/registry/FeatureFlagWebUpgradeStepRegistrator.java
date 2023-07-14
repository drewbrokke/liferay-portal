/**
 *
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

package com.liferay.feature.flag.web.internal.upgrade.registry;

import com.liferay.feature.flag.web.internal.company.feature.flags.CompanyFeatureFlags;
import com.liferay.feature.flag.web.internal.company.feature.flags.CompanyFeatureFlagsProvider;
import com.liferay.feature.flag.web.internal.model.FeatureFlag;
import com.liferay.feature.flag.web.internal.model.FeatureFlagType;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = UpgradeStepRegistrator.class)
public class FeatureFlagWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			() -> {
				if (_disableDeprecatedFeatures()) {
					CompanyFeatureFlags systemFeatureFlags =
						_companyFeatureFlagsProvider.
							getOrCreateCompanyFeatureFlags(
								CompanyConstants.SYSTEM);

					List<FeatureFlag> deprecationFeatureFlags =
						systemFeatureFlags.getFeatureFlags(
							FeatureFlagType.DEPRECATION.getPredicate());

					_companyLocalService.forEachCompanyId(
						companyId -> {
							CompanyFeatureFlags companyFeatureFlags =
								_companyFeatureFlagsProvider.
									getOrCreateCompanyFeatureFlags(companyId);

							for (FeatureFlag deprecationFeatureFlag :
									deprecationFeatureFlags) {

								companyFeatureFlags.setEnabled(
									deprecationFeatureFlag.getKey(), false);
							}
						});
				}
			});
	}

	private boolean _disableDeprecatedFeatures() {
		if (ReleaseInfo.isDXP() && !StartupHelperUtil.isDBNew() &&
			StartupHelperUtil.isUpgrading()) {

			return false;
		}

		return true;
	}

	@Reference
	private CompanyFeatureFlagsProvider _companyFeatureFlagsProvider;

	@Reference
	private CompanyLocalService _companyLocalService;

}