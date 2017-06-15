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

package com.liferay.document.library.service.test;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringPool;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Drew Brokke
 */
public class ConfigurationTemporarySwapper implements AutoCloseable {

	public ConfigurationTemporarySwapper(
			Class<?> callingClass, Class<?> serviceClass, String pid,
			Dictionary<String, Object> properties)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(callingClass);

		// ----------------Service------------------

		_serviceServiceTracker = ServiceTrackerFactory.open(
			bundle, serviceClass);

		Object service = _serviceServiceTracker.waitForService(5000);

		Bundle serviceBundle = FrameworkUtil.getBundle(service.getClass());

		BundleContext serviceBundleContext = serviceBundle.getBundleContext();

		// ----------------Configuration Admin------------------

		_configurationAdminServiceTracker = ServiceTrackerFactory.open(
				bundle, ConfigurationAdmin.class);

		ConfigurationAdmin configurationAdmin =
			_configurationAdminServiceTracker.waitForService(5000);

		_configuration = configurationAdmin.getConfiguration(
			pid, StringPool.QUESTION);

		// ----------------Service Listener------------------

		_serviceListener =
			new ConfigurationServiceListener(service, serviceBundleContext);

		serviceBundleContext.addServiceListener(_serviceListener);

		_serviceListener._countDownLatch = new CountDownLatch(1);

		// Store properties

		_configuration.update(properties);

		_serviceListener._countDownLatch.await(2L, TimeUnit.MINUTES);
	}

	@Override
	public void close() throws Exception {
		_serviceListener._countDownLatch = new CountDownLatch(1);

		try {
			_configuration.delete();

			_serviceListener._countDownLatch.await(2L, TimeUnit.MINUTES);
		}
		finally {
			_serviceListener._serviceBundleContext.removeServiceListener(
				_serviceListener);

			_serviceServiceTracker.close();
			_configurationAdminServiceTracker.close();
		}
	}

	private final ConfigurationServiceListener _serviceListener;
	private final Configuration _configuration;
	private final ServiceTracker<?, ?> _serviceServiceTracker;
	private final ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> _configurationAdminServiceTracker;

	private static class ConfigurationServiceListener
		implements ServiceListener {

		public void serviceChanged(ServiceEvent serviceEvent) {
			if (serviceEvent.getType() == ServiceEvent.MODIFIED) {
				ServiceReference<?> serviceReference =
					serviceEvent.getServiceReference();

				Object service = _serviceBundleContext.getService(
					serviceReference);

				if (service == _service) {
					_countDownLatch.countDown();
				}
			}
		}

		private ConfigurationServiceListener(
			Object service, BundleContext serviceBundleContext) {

			_service = service;
			_serviceBundleContext = serviceBundleContext;
		}

		private CountDownLatch _countDownLatch;
		private final Object _service;
		private final BundleContext _serviceBundleContext;

	}

}