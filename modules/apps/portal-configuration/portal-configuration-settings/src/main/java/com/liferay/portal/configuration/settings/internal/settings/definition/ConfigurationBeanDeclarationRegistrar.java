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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.settings.internal.SettingsLocatorHelperImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
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
	implements BundleTrackerCustomizer<List<AutoCloseable>> {

	@Override
	public List<AutoCloseable> addingBundle(
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

		List<AutoCloseable> autoCloseables = new ArrayList<>();

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

			AutoCloseable autoCloseable =
				_settingsLocatorHelperImpl.registerClass(configurationClass);

			if (autoCloseable != null) {
				autoCloseables.add(autoCloseable);
			}
		}

		if (ListUtil.isEmpty(autoCloseables)) {
			return null;
		}

		return autoCloseables;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<AutoCloseable> autoCloseables) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<AutoCloseable> autoCloseables) {

		if (ListUtil.isEmpty(autoCloseables)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Un-registering ConfigurationBeanDeclarations for bundle: " +
					bundle.getSymbolicName());
		}

		for (AutoCloseable serviceRegistration : autoCloseables) {
			try {
				serviceRegistration.close();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_bundleTracker = null;
	}

	private Class<?> _getConfigurationClass(Bundle bundle, String pid) {
		try {
			Class<?> clazz = bundle.loadClass(pid);

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

	private BundleTracker<List<AutoCloseable>> _bundleTracker;

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

	@Reference
	private SettingsLocatorHelperImpl _settingsLocatorHelperImpl;

}