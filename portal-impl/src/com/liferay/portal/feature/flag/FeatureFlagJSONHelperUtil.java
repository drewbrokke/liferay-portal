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

package com.liferay.portal.feature.flag;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.util.PropsValues;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Drew Brokke
 */
public class FeatureFlagJSONHelperUtil {

	public static String getFeatureFlagJSON(long companyId) {
		FeatureFlagJSONHelper featureFlagJSONHelper = _serviceTracker.getService();

		if (featureFlagJSONHelper != null) {
			return featureFlagJSONHelper.getFeatureFlagJSON(companyId);
		}
		
		return PropsValues.FEATURE_FLAGS_JSON;
	}

	private static final ServiceTracker<FeatureFlagJSONHelper, FeatureFlagJSONHelper>
		_serviceTracker;

	static {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceTracker =
			new ServiceTracker<>(bundleContext, FeatureFlagJSONHelper.class,
				null);
	}

}