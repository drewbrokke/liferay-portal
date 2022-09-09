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

package com.liferay.bookmarks.web.internal.portlet;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class ConfigurationBeanDeclarationRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_beanDeclarationServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, ConfigurationBeanDeclaration.class,
				"(!(configuration.bean.class.name=*))",
				(serviceReference, emitter) -> {
					ConfigurationBeanDeclaration service =
						_bundleContext.getService(serviceReference);

					emitter.emit(service.getConfigurationBeanClass());

					_bundleContext.ungetService(serviceReference);
				});

		for (Bundle bundle : bundleContext.getBundles()) {
			_registerConfigurationBeanDeclarations(bundle);
		}

		_bundleListener = new BundleListenerImpl();

		bundleContext.addBundleListener(_bundleListener);
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext.removeBundleListener(_bundleListener);

		_bundleListener = null;

		for (Bundle bundle : _bundleContext.getBundles()) {
			_unregisterConfigurationBeanDeclarations(bundle);
		}

		_beanDeclarationServiceTrackerMap.close();

		_beanDeclarationServiceTrackerMap = null;

		_bundleContext = null;
	}

	private Class<?> _getConfigurationClass(Bundle bundle, String pid) {
		try {
			Class<?> clazz = bundle.loadClass(pid);

			if (_beanDeclarationServiceTrackerMap.containsKey(clazz)) {
				System.out.println(
					"skipping CLASS (already registered): " + clazz.getName());

				return null;
			}

			if (clazz.getAnnotation(Meta.OCD.class) == null) {
				System.out.println(
					"skipping CLASS (no Meta.OCD): " + clazz.getName());

				return null;
			}

			for (Method methods : clazz.getMethods()) {
				Meta.AD annotation = methods.getAnnotation(Meta.AD.class);

				if (annotation == null) {
					continue;
				}

				if (annotation.required()) {
					System.out.println(
						"skipping CLASS (has required attribute): " +
							clazz.getName());

					return null;
				}
			}

			return clazz;
		}
		catch (ClassNotFoundException classNotFoundException) {
			System.out.println(
				"class not found: " + classNotFoundException.getMessage());
		}

		return null;
	}

	private void _registerConfigurationBeanDeclarations(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (!GetterUtil.getBoolean(
				headers.get("Liferay-Auto-Register-Configuration-Beans"),
				true)) {

			System.out.println(
				"skipping bundle (auto-registration turned off): " +
					bundle.getSymbolicName());

			return;
		}

		ExtendedMetaTypeInformation metaTypeInformation =
			_extendedMetaTypeService.getMetaTypeInformation(bundle);

		if (metaTypeInformation == null) {
			return;
		}

		List<ServiceRegistration<ConfigurationBeanDeclaration>>
			serviceRegistrations = new ArrayList<>();

		for (String pid :
				ArrayUtil.append(
					metaTypeInformation.getPids(),
					metaTypeInformation.getFactoryPids())) {

			Class<?> configurationClass = _getConfigurationClass(bundle, pid);

			if (configurationClass == null) {
				continue;
			}

			System.out.println(
				"registering CLASS: " + configurationClass.getName());

			serviceRegistrations.add(
				_bundleContext.registerService(
					ConfigurationBeanDeclaration.class,
					new ConfigurationBeanDeclarationImpl(configurationClass),
					HashMapDictionaryBuilder.put(
						"configuration.bean.class.name",
						configurationClass.getName()
					).build()));
		}

		_serviceRegistrationsMap.put(
			bundle.getSymbolicName(), serviceRegistrations);
	}

	private void _unregisterConfigurationBeanDeclarations(Bundle bundle) {
		List<ServiceRegistration<ConfigurationBeanDeclaration>>
			serviceRegistrations = _serviceRegistrationsMap.remove(
				bundle.getSymbolicName());

		if (serviceRegistrations == null) {
			return;
		}

		for (ServiceRegistration<ConfigurationBeanDeclaration>
				serviceRegistration : serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private ServiceTrackerMap<Object, ConfigurationBeanDeclaration>
		_beanDeclarationServiceTrackerMap;
	private BundleContext _bundleContext;
	private BundleListener _bundleListener;

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

	private final Map
		<String, List<ServiceRegistration<ConfigurationBeanDeclaration>>>
			_serviceRegistrationsMap = new ConcurrentHashMap<>();

	private static class ConfigurationBeanDeclarationImpl
		implements ConfigurationBeanDeclaration {

		public ConfigurationBeanDeclarationImpl(Class<?> clazz) {
			_class = clazz;
		}

		@Override
		public Class<?> getConfigurationBeanClass() {
			return _class;
		}

		private final Class<?> _class;

	}

	private class BundleListenerImpl implements BundleListener {

		@Override
		public void bundleChanged(BundleEvent bundleEvent) {
			int type = bundleEvent.getType();
			Bundle bundle = bundleEvent.getBundle();

			if (type == BundleEvent.STARTED) {
				_registerConfigurationBeanDeclarations(bundle);
			}
			else if (type == BundleEvent.STOPPED) {
				_unregisterConfigurationBeanDeclarations(bundle);
			}
		}

	}

}