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

import com.liferay.configuration.admin.portlet.action.ConfigurationRenderCommand;
import com.liferay.configuration.admin.portlet.action.FactoryInstancesListRenderCommand;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Pei-Jung Lan
 */
public class ConfigurationRenderCommandUtil {

	public static ConfigurationRenderCommand getConfigurationRenderCommand(
		String pid) {

		return _configurationRenderCommandMap.getService(pid);
	}

	public static FactoryInstancesListRenderCommand
		getFactoryInstancesListRenderCommand(String factoryPid) {

		return _factoryInstancesListRenderCommandMap.getService(factoryPid);
	}

	private static final ServiceTrackerMap<String, ConfigurationRenderCommand>
		_configurationRenderCommandMap;
	private static final
		ServiceTrackerMap<String, FactoryInstancesListRenderCommand>
			_factoryInstancesListRenderCommandMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationRenderCommandUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_configurationRenderCommandMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ConfigurationRenderCommand.class,
				"configurationPid");

		_factoryInstancesListRenderCommandMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, FactoryInstancesListRenderCommand.class,
				"configurationPid");
	}

}