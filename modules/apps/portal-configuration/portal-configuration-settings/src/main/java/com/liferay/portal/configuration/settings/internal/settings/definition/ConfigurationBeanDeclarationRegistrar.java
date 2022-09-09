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

package com.liferay.portal.configuration.settings.internal.settings.definition;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Drew Brokke
 */
@Component(service = {})
public class ConfigurationBeanDeclarationRegistrar
	implements BundleTrackerCustomizer
		<List<ServiceRegistration<ConfigurationBeanDeclaration>>> {

	@Override
	public List<ServiceRegistration<ConfigurationBeanDeclaration>> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		String bundleSymbolicName = bundle.getSymbolicName();

		if (bundleSymbolicName.endsWith(".test")) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping bundle (do not check test modules): " +
						bundleSymbolicName);
			}

			return null;
		}

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (!GetterUtil.getBoolean(
				headers.get("Liferay-Auto-Register-Configuration-Beans"),
				true)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping bundle (Liferay-Auto-Register-Configuration-" +
						"Beans is false): " + bundleSymbolicName);
			}

			return null;
		}

		ExtendedMetaTypeInformation metaTypeInformation =
			_extendedMetaTypeService.getMetaTypeInformation(bundle);

		if (metaTypeInformation == null) {
			return null;
		}

		List<ServiceRegistration<ConfigurationBeanDeclaration>>
			serviceRegistrations = new ArrayList<>();

		BundleContext bundleContext = bundle.getBundleContext();

		for (String pid :
				ArrayUtil.append(
					metaTypeInformation.getPids(),
					metaTypeInformation.getFactoryPids())) {

			Class<?> configurationClass = _getConfigurationClass(bundle, pid);

			if (configurationClass == null) {
				continue;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Registering ConfigurationBeanDeclaration for class: " +
						configurationClass.getName());
			}

			serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationBeanDeclaration.class,
					new ConfigurationBeanDeclarationImpl(configurationClass),
					HashMapDictionaryBuilder.put(
						"configuration.bean.class.name",
						configurationClass.getName()
					).build()));
		}

		if (ListUtil.isEmpty(serviceRegistrations)) {
			return null;
		}

		return serviceRegistrations;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<ServiceRegistration<ConfigurationBeanDeclaration>>
			serviceRegistrations) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<ServiceRegistration<ConfigurationBeanDeclaration>>
			serviceRegistrations) {

		if (ListUtil.isEmpty(serviceRegistrations)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Un-registering ConfigurationBeanDeclarations for bundle: " +
					bundle.getSymbolicName());
		}

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceRegistration<ConfigurationBeanDeclaration>
				serviceRegistration : serviceRegistrations) {

			if (_log.isDebugEnabled()) {
				ConfigurationBeanDeclaration configurationBeanDeclaration =
					bundleContext.getService(
						serviceRegistration.getReference());

				Class<?> configurationBeanClass =
					configurationBeanDeclaration.getConfigurationBeanClass();

				_log.debug(
					"Un-registering ConfigurationBeanDeclaration for class: " +
						configurationBeanClass.getName());
			}

			serviceRegistration.unregister();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_beanDeclarationServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ConfigurationBeanDeclaration.class, null,
				(serviceReference, emitter) -> {
					ConfigurationBeanDeclaration service =
						bundleContext.getService(serviceReference);

					emitter.emit(service.getConfigurationBeanClass());

					bundleContext.ungetService(serviceReference);
				});

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_bundleTracker = null;

		_beanDeclarationServiceTrackerMap.close();

		_beanDeclarationServiceTrackerMap = null;
	}

	private Class<?> _getConfigurationClass(Bundle bundle, String pid) {
		try {
			Class<?> clazz = bundle.loadClass(pid);

			if (_beanDeclarationServiceTrackerMap.containsKey(clazz)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping registration for class (already " +
							"registered): " + clazz.getName());
				}

				return null;
			}

			if (clazz.getAnnotation(Meta.OCD.class) == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping registration for class (no Meta.OCD " +
							"annotation): " + clazz.getName());
				}

				return null;
			}

			for (Method methods : clazz.getMethods()) {
				Meta.AD annotation = methods.getAnnotation(Meta.AD.class);

				if (annotation == null) {
					continue;
				}

				if (annotation.required()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Skipping registration for class (Meta.AD has " +
								"required = true): " + clazz.getName());
					}

					return null;
				}
			}

			return clazz;
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Class not found: " + classNotFoundException.getMessage());
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationBeanDeclarationRegistrar.class);

	private ServiceTrackerMap<Object, ConfigurationBeanDeclaration>
		_beanDeclarationServiceTrackerMap;
	private BundleTracker
		<List<ServiceRegistration<ConfigurationBeanDeclaration>>>
			_bundleTracker;

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

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

}