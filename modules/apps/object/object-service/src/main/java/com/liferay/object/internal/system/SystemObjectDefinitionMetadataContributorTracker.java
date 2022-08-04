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

package com.liferay.object.internal.system;

import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.system.SystemObjectDefinitionMetadata;
import com.liferay.object.system.SystemObjectDefinitionMetadataContributor;
import com.liferay.object.system.model.listener.SystemObjectDefinitionMetadataModelListener;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Drew Brokke
 */
public class SystemObjectDefinitionMetadataContributorTracker {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, SystemObjectDefinitionMetadataContributor.class,
			null, new ContributorServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	class ContributorServiceTrackerCustomizer implements ServiceTrackerCustomizer<SystemObjectDefinitionMetadataContributor, SystemObjectDefinitionMetadataContributor> {

		public ContributorServiceTrackerCustomizer(
			BundleContext bundleContext) {
			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

		@Override
		public SystemObjectDefinitionMetadataContributor addingService(
			ServiceReference<SystemObjectDefinitionMetadataContributor> serviceReference) {

			SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor =
				_bundleContext.getService(serviceReference);

			registerRelatedServices(systemObjectDefinitionMetadataContributor);

			return systemObjectDefinitionMetadataContributor;
		}

		private void registerRelatedServices(
			SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor) {
			List<ServiceRegistration<?>> serviceReferences = new ArrayList<>(2);

			serviceReferences.add(
				_bundleContext.registerService(
					ModelListener.class.getName(),
					new SystemObjectDefinitionMetadataModelListener(
						dtoConverterRegistry, jsonFactory,
						systemObjectDefinitionMetadataContributor.getModelClass(),
						objectActionEngine, objectDefinitionLocalService,
						objectEntryLocalService, objectValidationRuleLocalService,
						userLocalService),
					null));
			serviceReferences.add(
				_bundleContext.registerService(
					SystemObjectDefinitionMetadata.class.getName(),
					new DefaultSystemObjectDefinitionMetadata(
						systemObjectDefinitionMetadataContributor),
					null));

			_serviceRegistrationsMap.put(
				systemObjectDefinitionMetadataContributor.getModelClass(), serviceReferences);
		}

		@Override
		public void modifiedService(
			ServiceReference<SystemObjectDefinitionMetadataContributor> serviceReference,
			SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor) {

			unregisterRelatedServices(systemObjectDefinitionMetadataContributor);

			registerRelatedServices(systemObjectDefinitionMetadataContributor);
		}

		@Override
		public void removedService(
			ServiceReference<SystemObjectDefinitionMetadataContributor> serviceReference,
			SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor) {

			unregisterRelatedServices(systemObjectDefinitionMetadataContributor);
		}

		private void unregisterRelatedServices(
			SystemObjectDefinitionMetadataContributor systemObjectDefinitionMetadataContributor) {
			List<ServiceRegistration<?>> serviceRegistrations = _serviceRegistrationsMap.remove(
				systemObjectDefinitionMetadataContributor.getModelClass());

			for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
				serviceRegistration.unregister();
			}
		}

		Map<Class<?>, List<ServiceRegistration<?>>> _serviceRegistrationsMap = new ConcurrentHashMap<>();
	}

	private ServiceTrackerList<SystemObjectDefinitionMetadataContributor> _serviceTrackerList;

	@Reference
	protected DTOConverterRegistry dtoConverterRegistry;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected ObjectActionEngine objectActionEngine;

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	@Reference
	protected ObjectValidationRuleLocalService objectValidationRuleLocalService;

	@Reference
	protected UserLocalService userLocalService;

}
