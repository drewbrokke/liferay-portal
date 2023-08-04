/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.configuration.admin.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.feature.flag.constants.FeatureFlagConstants;
import com.liferay.feature.flag.model.FeatureFlagType;
import com.liferay.feature.flag.web.internal.configuration.admin.display.FeatureFlagConfigurationScreen;
import com.liferay.feature.flag.web.internal.display.FeatureFlagsDisplayContextFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationCategory.class)
public class FeatureFlagConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "flag-full";
	}

	@Override
	public String getCategoryKey() {
		return FeatureFlagConstants.CONFIGURATION_CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		for (FeatureFlagType featureFlagType : FeatureFlagType.values()) {
			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagConfigurationScreen(
						featureFlagType, _featureFlagsDisplayContextFactory,
						_servletContext),
					new HashMapDictionary<>()));
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	@Reference
	private FeatureFlagsDisplayContextFactory
		_featureFlagsDisplayContextFactory;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

}