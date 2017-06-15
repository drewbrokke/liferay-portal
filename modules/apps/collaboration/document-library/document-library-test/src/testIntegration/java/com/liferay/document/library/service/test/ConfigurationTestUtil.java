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
public class ConfigurationTestUtil {

	public static <T> T runWithConfiguration(
			Class callingClass, Class<?> serviceClass, String pid,
			Map<String, Object> temporaryValues, Callable<T> callable)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(callingClass);

		// ----------------Service------------------

		ServiceTracker<?, ?> serviceServiceTracker = ServiceTrackerFactory.open(
			bundle, serviceClass);

		Object service = serviceServiceTracker.waitForService(5000);

		Bundle serviceBundle = FrameworkUtil.getBundle(service.getClass());

		BundleContext serviceBundleContext = serviceBundle.getBundleContext();

		// ----------------Configuration Admin------------------

		ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>
			configurationAdminServiceTracker = ServiceTrackerFactory.open(
				bundle, ConfigurationAdmin.class);

		ConfigurationAdmin configurationAdmin =
			configurationAdminServiceTracker.waitForService(5000);

		Configuration configuration = configurationAdmin.getConfiguration(
			pid, StringPool.QUESTION);

		// ----------------Service Listener------------------

		ConfigurationServiceListener serviceListener = _setUpConfiguration(
			service, serviceBundleContext, configuration, temporaryValues);

		try {
			return callable.call();
		}
		finally {
			_tearDownConfiguration(
				serviceListener, serviceBundleContext, serviceServiceTracker,
				configurationAdminServiceTracker, configuration);
		}
	}

	private static ConfigurationServiceListener _setUpConfiguration(
			Object service, BundleContext serviceBundleContext,
			Configuration configuration, Map<String, Object> temporaryValues)
		throws Exception {

		ConfigurationServiceListener serviceListener =
			new ConfigurationServiceListener(service, serviceBundleContext);

		serviceBundleContext.addServiceListener(serviceListener);

		serviceListener._countDownLatch = new CountDownLatch(1);

		// Store properties

		Dictionary<String, Object> properties = new HashMapDictionary();

		for (String key : temporaryValues.keySet()) {
			properties.put(key, temporaryValues.get(key));
		}

		configuration.update(properties);

		serviceListener._countDownLatch.await(2L, TimeUnit.MINUTES);

		return serviceListener;
	}

	private static void _tearDownConfiguration(
			ConfigurationServiceListener serviceListener,
			BundleContext serviceBundleContext,
			ServiceTracker<?, ?> serviceServiceTracker,
			ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>
				configurationAdminServiceTracker, Configuration configuration)
		throws Exception {

		serviceListener._countDownLatch = new CountDownLatch(1);

		try {
			configuration.delete();

			serviceListener._countDownLatch.await(2L, TimeUnit.MINUTES);
		}
		finally {
			serviceBundleContext.removeServiceListener(serviceListener);

			serviceServiceTracker.close();
			configurationAdminServiceTracker.close();
		}
	}

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