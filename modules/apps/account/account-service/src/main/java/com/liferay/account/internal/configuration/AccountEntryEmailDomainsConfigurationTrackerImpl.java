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

package com.liferay.account.internal.configuration;

import com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration;
import com.liferay.account.configuration.AccountEntryEmailDomainsConfigurationTracker;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Pei-Jung Lan
 */
@Component(
	configurationPid = "com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration",
	immediate = true,
	property = Constants.SERVICE_PID + "=com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration.scoped",
	service = {
		AccountEntryEmailDomainsConfigurationTracker.class,
		ManagedServiceFactory.class
	}
)
public class AccountEntryEmailDomainsConfigurationTrackerImpl
	implements AccountEntryEmailDomainsConfigurationTracker,
			   ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	@Override
	public AccountEntryEmailDomainsConfiguration
		getAccountEntryEmailDomainsConfiguration(long companyId) {

		if (_companyConfigurationBeans.containsKey(companyId)) {
			return _companyConfigurationBeans.get(companyId);
		}

		return _systemPersonalMenuConfiguration;
	}

	@Override
	public String getName() {
		return "com.liferay.account.configuration." +
			"AccountEntryEmailDomainsConfiguration.scoped";
	}

	@Override
	public void updated(String pid, Dictionary dictionary)
		throws ConfigurationException {

		_unmapPid(pid);

		long companyId = GetterUtil.getLong(
			dictionary.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId != CompanyConstants.SYSTEM) {
			_pidCompanyIdMapping.put(pid, companyId);

			_companyConfigurationBeans.put(
				companyId,
				ConfigurableUtil.createConfigurable(
					AccountEntryEmailDomainsConfiguration.class, dictionary));
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemPersonalMenuConfiguration = ConfigurableUtil.createConfigurable(
			AccountEntryEmailDomainsConfiguration.class, properties);
	}

	private void _unmapPid(String pid) {
		if (_pidCompanyIdMapping.containsKey(pid)) {
			long companyId = _pidCompanyIdMapping.remove(pid);

			_companyConfigurationBeans.remove(companyId);
		}
	}

	private final Map<Long, AccountEntryEmailDomainsConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _pidCompanyIdMapping =
		new ConcurrentHashMap<>();
	private volatile AccountEntryEmailDomainsConfiguration
		_systemPersonalMenuConfiguration;

}