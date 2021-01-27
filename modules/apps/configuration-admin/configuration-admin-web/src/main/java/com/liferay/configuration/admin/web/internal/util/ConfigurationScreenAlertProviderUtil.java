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

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.display.ConfigurationScreenAlertProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(immediate = true, service = {})
public class ConfigurationScreenAlertProviderUtil {

	public static List<ConfigurationScreenAlertProvider.Alert>
		getConfigurationScreenAlerts(
			String configurationPid, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK) {

		if (_serviceTrackerMap == null) {
			return Collections.emptyList();
		}

		List<ConfigurationScreenAlertProvider>
			configurationScreenAlertProviders = _serviceTrackerMap.getService(
				configurationPid);

		if (ListUtil.isEmpty(configurationScreenAlertProviders)) {
			return Collections.emptyList();
		}

		Stream<ConfigurationScreenAlertProvider> stream =
			configurationScreenAlertProviders.stream();

		return stream.map(
			configurationScreenAlertProvider ->
				configurationScreenAlertProvider.getAlert(scope, scopePK)
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toList()
		);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap =
			(ServiceTrackerMap<String, List<ConfigurationScreenAlertProvider>>)
				(ServiceTrackerMap)ServiceTrackerMapFactory.openMultiValueMap(
					bundleContext, ConfigurationScreenAlertProvider.class, null,
					(serviceReference, emitter) -> {
						for (String configurationPid :
								_getPropertyValues(
									serviceReference, "configuration.pid")) {

							emitter.emit(configurationPid);
						}
					});
	}

	@Deactivate
	protected synchronized void deactivate() {
		_serviceTrackerMap.close();
	}

	private Collection<String> _getPropertyValues(
		ServiceReference<?> serviceReference, String name) {

		Object propertyValue = serviceReference.getProperty(name);

		if (propertyValue == null) {
			return Collections.emptyList();
		}

		if (propertyValue instanceof Collection) {
			return (Collection<String>)propertyValue;
		}

		if (propertyValue instanceof Object[]) {
			return Arrays.asList((String[])propertyValue);
		}

		return Arrays.asList((String)propertyValue);
	}

	private static volatile ServiceTrackerMap
		<String, List<ConfigurationScreenAlertProvider>> _serviceTrackerMap;

}