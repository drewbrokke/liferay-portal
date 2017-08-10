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

package com.liferay.portal.configuration.settings.internal;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition.Scope;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.settings.ConfigurationBeanSettings;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.settings.PropertiesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PrefsPropsUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import javax.portlet.PortletPreferences;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Iván Zaera
 * @author Jorge Ferrer
 * @author Shuyang Zhou
 */
@Component(
	immediate = true,
	service = {ConfigurationListener.class, SettingsLocatorHelper.class}
)
@DoPrivileged
public class SettingsLocatorHelperImpl
	implements SettingsLocatorHelper, ConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		if (event.getType() != ConfigurationEvent.CM_UPDATED) {
			return;
		}

		String pid = event.getFactoryPid();

		if (pid == null) {
			pid = event.getPid();
		}

		String[] splitPid = pid.split("_", 2);

		if ((splitPid.length < 2) ||
			!_configurationBeanClasses.containsKey(splitPid[0])) {

			return;
		}

		ScopeKey scopeKey = _parseScopedPid(splitPid[0], splitPid[1]);

		if (scopeKey.getScope() == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_log.error(
				"For system scope, simply use the PID without any suffix");
			return;
		}

		if (Validator.isNull(scopeKey.getScopePrimKey())) {
			Stream<Scope> stream = Arrays.stream(
				ExtendedObjectClassDefinition.Scope.values());

			String[] scopeOptions = stream.map(
				Enum::name
			).toArray(
				String[]::new
			);

			StringBundler sb = new StringBundler();

			sb.append("Could not parse ");
			sb.append(pid);
			sb.append(". Correct format is PID followed by '_' ");
			sb.append("followed by one of ");
			sb.append(Arrays.asList(scopeOptions));
			sb.append(" followed by '_' followed by scope instance identifier");

			_log.error(sb.toString());
			return;
		}

		if (_scopedConfigurationBeans.containsKey(scopeKey)) {
			return;
		}

		ConfigurationBeanManagedService configurationBeanManagedService =
			new ConfigurationBeanManagedService(
				_bundleContext, scopeKey.getObjectClass(),
				(configurationBean) -> {
					if ((configurationBean == null) &&
						_scopedConfigurationBeans.containsKey(scopeKey)) {

						_scopedConfigurationBeans.remove(scopeKey);
						return;
					}

					_scopedConfigurationBeans.put(scopeKey, configurationBean);
				},
				scopeKey.getScope(), scopeKey.getScopePrimKey());

		configurationBeanManagedService.register();
	}

	public PortletPreferences getCompanyPortletPreferences(
		long companyId, String settingsId) {

		return _portletPreferencesLocalService.getStrictPreferences(
			companyId, companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0,
			settingsId);
	}

	@Override
	public Settings getCompanyPortletPreferencesSettings(
		long companyId, String settingsId, Settings parentSettings) {

		Settings portletPreferencesSettings = new PortletPreferencesSettings(
			getCompanyPortletPreferences(companyId, settingsId),
			parentSettings);

		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			settingsId);

		if (configurationBeanClass == null) {
			return portletPreferencesSettings;
		}

		ScopeKey scopeKey = new ScopeKey(
			configurationBeanClass, ExtendedObjectClassDefinition.Scope.COMPANY,
			Long.toString(companyId));

		return _getConfigurationBeanSettings(
			configurationBeanClass, scopeKey, portletPreferencesSettings);
	}

	@Override
	public Settings getConfigurationBeanSettings(String configurationPid) {
		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			configurationPid);

		if (configurationBeanClass == null) {
			return _portalPropertiesSettings;
		}

		Settings configurationBeanSettings = _configurationBeanSettings.get(
			configurationBeanClass);

		if (configurationBeanSettings == null) {
			return _portalPropertiesSettings;
		}

		return configurationBeanSettings;
	}

	/**
	 * @deprecated As of 2.0.0, replaced by {@link
	 *             #getConfigurationBeanSettings(String)}
	 */
	@Deprecated
	@Override
	public Settings getConfigurationBeanSettings(
		String configurationPid, Settings parentSettings) {

		return getConfigurationBeanSettings(configurationPid);
	}

	public PortletPreferences getGroupPortletPreferences(
		long groupId, String settingsId) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			return _portletPreferencesLocalService.getStrictPreferences(
				group.getCompanyId(), groupId,
				PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, settingsId);
		}
		catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	@Override
	public Settings getGroupPortletPreferencesSettings(
		long groupId, String settingsId, Settings parentSettings) {

		PortletPreferencesSettings portletPreferencesSettings =
			new PortletPreferencesSettings(
				getGroupPortletPreferences(groupId, settingsId),
				parentSettings);

		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			settingsId);

		if (configurationBeanClass == null) {
			return portletPreferencesSettings;
		}

		ScopeKey scopeKey = new ScopeKey(
			configurationBeanClass, ExtendedObjectClassDefinition.Scope.GROUP,
			Long.toString(groupId));

		return _getConfigurationBeanSettings(
			configurationBeanClass, scopeKey, portletPreferencesSettings);
	}

	@Override
	public Settings getPortalPreferencesSettings(
		long companyId, Settings parentSettings) {

		return new PortletPreferencesSettings(
			PrefsPropsUtil.getPreferences(companyId), parentSettings);
	}

	/**
	 * @deprecated As of 2.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public Settings getPortalPropertiesSettings() {
		return _portalPropertiesSettings;
	}

	public PortletPreferences getPortletInstancePortletPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		if (PortletIdCodec.hasUserId(portletId)) {
			ownerId = PortletIdCodec.decodeUserId(portletId);
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		}

		return _portletPreferencesLocalService.getStrictPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	public PortletPreferences getPortletInstancePortletPreferences(
		long companyId, long plid, String portletId) {

		return getPortletInstancePortletPreferences(
			companyId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId);
	}

	@Override
	public Settings getPortletInstancePortletPreferencesSettings(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, Settings parentSettings) {

		PortletPreferencesSettings portletPreferencesSettings =
			new PortletPreferencesSettings(
				getPortletInstancePortletPreferences(
					companyId, ownerId, ownerType, plid, portletId),
				parentSettings);

		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			portletId);

		if (configurationBeanClass == null) {
			return portletPreferencesSettings;
		}

		ScopeKey scopeKey = new ScopeKey(
			configurationBeanClass,
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE, portletId);

		return _getConfigurationBeanSettings(
			configurationBeanClass, scopeKey, portletPreferencesSettings);
	}

	@Override
	public Settings getPortletInstancePortletPreferencesSettings(
		long companyId, long plid, String portletId, Settings parentSettings) {

		PortletPreferencesSettings portletPreferencesSettings =
			new PortletPreferencesSettings(
				getPortletInstancePortletPreferences(
					companyId, plid, portletId),
				parentSettings);

		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			portletId);

		if (configurationBeanClass == null) {
			return portletPreferencesSettings;
		}

		ScopeKey scopeKey = new ScopeKey(
			configurationBeanClass,
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE, portletId);

		return _getConfigurationBeanSettings(
			configurationBeanClass, scopeKey, portletPreferencesSettings);
	}

	@Override
	public Settings getServerSettings(String settingsId) {
		return getConfigurationBeanSettings(settingsId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_configurationBeanDeclarationServiceTracker =
			new ConfigurationBeanDeclarationServiceTracker(bundleContext);

		_configurationBeanDeclarationServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_configurationBeanDeclarationServiceTracker.close();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	protected void setConfigurationPidMapping(
		ConfigurationPidMapping configurationPidMapping) {

		_configurationBeanClasses.put(
			configurationPidMapping.getConfigurationPid(),
			configurationPidMapping.getConfigurationBeanClass());
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {
	}

	@Reference(unbind = "-")
	protected void setPortletLocalService(
		PortletLocalService portletLocalService) {
	}

	@Reference(unbind = "-")
	protected void setPortletPreferencesLocalService(
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		_portalPropertiesSettings = new PropertiesSettings(
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					PortalClassLoaderUtil.getClassLoader()),
				this),
			props.getProperties());
	}

	protected void unsetConfigurationPidMapping(
		ConfigurationPidMapping configurationPidMapping) {

		_configurationBeanClasses.remove(
			configurationPidMapping.getConfigurationPid());
	}

	private ScopeKey _parseScopedPid(String pid, String scopeNotation) {
		ExtendedObjectClassDefinition.Scope settingsScope = null;
		String scopePrimKey = null;

		for (ExtendedObjectClassDefinition.Scope scope :
				ExtendedObjectClassDefinition.Scope.values()) {

			String scopeName = scope.name();

			if (scopeNotation.startsWith(scopeName)) {
				if ((scopeNotation.length() > scopeName.length()) &&
					(scopeNotation.charAt(scopeName.length()) == '_')) {

					settingsScope = scope;
					scopePrimKey = scopeNotation.substring(
						scopeName.length() + 1, scopeNotation.length());
					break;
				}
				else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
					settingsScope = scope;
					break;
				}
			}
		}

		Class<?> configurationBeanClass = _configurationBeanClasses.get(pid);

		ScopeKey scopeKey = new ScopeKey(
			configurationBeanClass, settingsScope, scopePrimKey);

		return scopeKey;
	}
	
	private Settings _getConfigurationBeanSettings(
		Class<?> configurationBeanClass, ScopeKey scopeKey,
		Settings parentSettings) {

		Object configurationBean = _scopedConfigurationBeans.get(scopeKey);

		if (configurationBean == null) {
			return parentSettings;
		}

		LocationVariableResolver locationVariableResolver =
			_configurationBeanLocationVariableResolver.get(
				configurationBeanClass);

		return new ConfigurationBeanSettings(
			locationVariableResolver, configurationBean, parentSettings);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SettingsLocatorHelperImpl.class);

	private BundleContext _bundleContext;
	private final ConcurrentMap<String, Class<?>> _configurationBeanClasses =
		new ConcurrentHashMap<>();
	private ServiceTracker
		<ConfigurationBeanDeclaration, ConfigurationBeanManagedService>
			_configurationBeanDeclarationServiceTracker;
	private final Map<Class<?>, LocationVariableResolver>
		_configurationBeanLocationVariableResolver = new ConcurrentHashMap<>();
	private final Map<Class<?>, Settings> _configurationBeanSettings =
		new ConcurrentHashMap<>();
	private GroupLocalService _groupLocalService;
	private Settings _portalPropertiesSettings;
	private PortletPreferencesLocalService _portletPreferencesLocalService;
	private final Map<ScopeKey, Object> _scopedConfigurationBeans =
		new ConcurrentHashMap<>();

	private class ConfigurationBeanDeclarationServiceTracker
		extends ServiceTracker
			<ConfigurationBeanDeclaration, ConfigurationBeanManagedService> {

		@Override
		public ConfigurationBeanManagedService addingService(
			ServiceReference<ConfigurationBeanDeclaration> serviceReference) {

			ConfigurationBeanDeclaration configurationBeanDeclaration =
				context.getService(serviceReference);

			Class<?> configurationBeanClass =
				configurationBeanDeclaration.getConfigurationBeanClass();

			ConfigurationBeanManagedService configurationBeanManagedService =
				new ConfigurationBeanManagedService(
					context, configurationBeanClass,
					(configurationBean) -> {
						ClassLoader classLoader =
							configurationBeanClass.getClassLoader();

						LocationVariableResolver locationVariableResolver =
							new LocationVariableResolver(
								new ClassLoaderResourceManager(classLoader),
								SettingsLocatorHelperImpl.this);

						_configurationBeanLocationVariableResolver.put(
							configurationBeanClass, locationVariableResolver);

						_configurationBeanSettings.put(
							configurationBeanClass,
							new ConfigurationBeanSettings(
								locationVariableResolver, configurationBean,
								_portalPropertiesSettings));
					},
					ExtendedObjectClassDefinition.Scope.SYSTEM, null);

			_configurationBeanClasses.put(
				configurationBeanManagedService.getConfigurationPid(),
				configurationBeanClass);

			configurationBeanManagedService.register();

			return configurationBeanManagedService;
		}

		@Override
		public void removedService(
			ServiceReference<ConfigurationBeanDeclaration> serviceReference,
			ConfigurationBeanManagedService configurationBeanManagedService) {

			context.ungetService(serviceReference);

			configurationBeanManagedService.unregister();

			Class<?> configurationBeanClass = _configurationBeanClasses.remove(
				configurationBeanManagedService.getConfigurationPid());

			_configurationBeanLocationVariableResolver.remove(
				configurationBeanClass);

			_configurationBeanSettings.remove(
				configurationBeanManagedService.getConfigurationPid());
		}

		private ConfigurationBeanDeclarationServiceTracker(
			BundleContext context) {

			super(context, ConfigurationBeanDeclaration.class, null);
		}

	}

	private class ScopeKey {

		public ScopeKey(
			Class<?> objectClass, ExtendedObjectClassDefinition.Scope scope,
			String scopePrimKey) {

			_objectClass = objectClass;
			_scope = scope;
			_scopePrimKey = scopePrimKey;
		}

		@Override
		public boolean equals(Object obj) {
			ScopeKey otherScopeKey = (ScopeKey)obj;

			if (_objectClass.equals(otherScopeKey.getObjectClass()) &&
				_scope.equals(otherScopeKey.getScope()) &&
				_scopePrimKey.equals(otherScopeKey.getScopePrimKey())) {

				return true;
			}

			return false;
		}

		public Class<?> getObjectClass() {
			return _objectClass;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public String getScopePrimKey() {
			return _scopePrimKey;
		}

		@Override
		public int hashCode() {
			return (
				_objectClass.getName() + _scope.getValue() +
					_scopePrimKey).hashCode();
		}

		private final Class<?> _objectClass;
		private final ExtendedObjectClassDefinition.Scope _scope;
		private final String _scopePrimKey;

	}

}