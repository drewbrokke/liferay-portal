package com.liferay.feature.flag.web.internal;

import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import org.osgi.service.component.annotations.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Drew Brokke
 */
@Component(service = {FeatureFlagsProvider.class, PortalInstanceLifecycleListener.class})
public class FeatureFlagsProvider implements PortalInstanceLifecycleListener {

	public boolean isEnabled(String key) {
		return isEnabled(CompanyThreadLocal.getCompanyId(), key);
	}

	public boolean isEnabled(long companyId, String key) {
		FeatureFlags featureFlags = getFeatureFlags(companyId);

		return featureFlags.isEnabled(key);
	}

	protected FeatureFlags getFeatureFlags(long companyId) {
		return _featureFlagsMap.get(companyId);
	}

	@Override
	public void portalInstanceRegistered(Company company) {
		_featureFlagsMap.put(company.getCompanyId(), new FeatureFlags(company.getCompanyId()));
	}

	@Override
	public void portalInstanceUnregistered(Company company) {
		_featureFlagsMap.remove(company.getCompanyId());
	}

	private final Map<Long, FeatureFlags> _featureFlagsMap = new ConcurrentHashMap<>();
}
