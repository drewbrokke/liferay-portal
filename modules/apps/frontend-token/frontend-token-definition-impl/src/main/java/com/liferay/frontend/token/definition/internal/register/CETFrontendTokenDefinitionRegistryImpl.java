package com.liferay.frontend.token.definition.internal.register;

import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.frontend.token.definition.register.CETFrontendTokenDefinitionRegistry;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(service = CETFrontendTokenDefinitionRegistry.class)
public class CETFrontendTokenDefinitionRegistryImpl
	implements CETFrontendTokenDefinitionRegistry {

	@Override
	public FrontendTokenDefinition getTokenDefinition(
		long companyId, String externalReferenceCode) {

		Map<String, FrontendTokenDefinition> frontendTokenDefinitions =
			_getFrontendTokenDefinitionsMap(companyId);

		return frontendTokenDefinitions.get(externalReferenceCode);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_frontendTokenDefinitionsMap = new ConcurrentHashMap<>();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ThemeCSSCET.class, "external.reference.code",
			new EagerServiceTrackerCustomizer<ThemeCSSCET, ThemeCSSCET>() {

				@Override
				public ThemeCSSCET addingService(
					ServiceReference<ThemeCSSCET> serviceReference) {

					ThemeCSSCET themeCSSCET = bundleContext.getService(
						serviceReference);

					System.out.println(
						"ADDED: " + themeCSSCET.getExternalReferenceCode());

					try {
						FrontendTokenDefinitionImpl frontendTokenDefinition =
							new FrontendTokenDefinitionImpl(
								jsonFactory.createJSONObject(
									themeCSSCET.getFrontendTokenDefinition()),
								jsonFactory,
								ResourceBundleLoaderUtil.
									getPortalResourceBundleLoader(),
								themeCSSCET.getExternalReferenceCode());

						Map<String, FrontendTokenDefinition>
							frontendTokenDefinitionsMap =
								_getFrontendTokenDefinitionsMap(
									themeCSSCET.getCompanyId());

						frontendTokenDefinitionsMap.put(
							themeCSSCET.getExternalReferenceCode(),
							frontendTokenDefinition);
					}
					catch (JSONException e) {
						throw new RuntimeException(e);
					}

					return themeCSSCET;
				}

				@Override
				public void modifiedService(
					ServiceReference<ThemeCSSCET> serviceReference,
					ThemeCSSCET themeCSSCET) {
				}

				@Override
				public void removedService(
					ServiceReference<ThemeCSSCET> serviceReference,
					ThemeCSSCET themeCSSCET) {

					System.out.println(
						"REMOVED: " + themeCSSCET.getExternalReferenceCode());

					Map<String, FrontendTokenDefinition>
						frontendTokenDefinitionsMap = _getFrontendTokenDefinitionsMap(
							themeCSSCET.getCompanyId());

					frontendTokenDefinitionsMap.remove(
						themeCSSCET.getExternalReferenceCode());

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	protected JSONFactory jsonFactory;

	private Map<String, FrontendTokenDefinition> _getFrontendTokenDefinitionsMap(
		long companyId) {

		Map<String, FrontendTokenDefinition> frontendTokenDefinitionsMap =
			_frontendTokenDefinitionsMap.get(companyId);

		if (frontendTokenDefinitionsMap == null) {
			frontendTokenDefinitionsMap = new ConcurrentHashMap<>();

			_frontendTokenDefinitionsMap.put(companyId, frontendTokenDefinitionsMap);
		}

		return frontendTokenDefinitionsMap;
	}

	private Map<Long, Map<String, FrontendTokenDefinition>>
		_frontendTokenDefinitionsMap;
	private ServiceTrackerMap<String, ThemeCSSCET> _serviceTrackerMap;

}