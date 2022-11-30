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

package com.liferay.feature.flag;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Drew Brokke
 */
public class FeatureFlagManagerUtil {

	public static boolean isEnabled(long companyId, String featureFlagKey) {
		FeatureFlagManager featureFlagManager = _serviceTracker.getService();

		return featureFlagManager.isEnabled(companyId, featureFlagKey);
	}

	public static boolean isEnabled(String featureFlagKey) {
		FeatureFlagManager featureFlagManager = _serviceTracker.getService();

		return featureFlagManager.isEnabled(featureFlagKey);
	}

	private static final ServiceTracker<FeatureFlagManager, FeatureFlagManager>
		_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(FeatureFlagManagerUtil.class);

		ServiceTracker<FeatureFlagManager, FeatureFlagManager> serviceTracker =
			new ServiceTracker<>(
				bundle.getBundleContext(), FeatureFlagManager.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}