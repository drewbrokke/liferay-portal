/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.feature.flag.company.feature.flags.CompanyFeatureFlags;
import com.liferay.feature.flag.company.feature.flags.CompanyFeatureFlagsProvider;
import com.liferay.feature.flag.model.FeatureFlag;
import com.liferay.feature.flag.model.FeatureFlagType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class DeprecationFeatureFlagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws PortalException {
		if (_company != null) {
			CompanyLocalServiceUtil.deleteCompany(_company.getCompanyId());
		}
	}

	@Test
	public void testDisabledDeprecationFeatureFlagsForANewCompany()
		throws PortalException {

		String hostName = "hostName";

		String virtualHostname = hostName + ".xyz";

		_company = CompanyLocalServiceUtil.addCompany(
			null, hostName, virtualHostname, virtualHostname, 0, true, null,
			null, null, null, null, null);

		CompanyFeatureFlags companyFeatureFlags =
			_companyFeatureFlagsProvider.getOrCreateCompanyFeatureFlags(
				_company.getCompanyId());

		List<FeatureFlag> deprecationFeatureFlags =
			companyFeatureFlags.getFeatureFlags(
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag featureFlag : deprecationFeatureFlags) {
			Assert.assertFalse(featureFlag.isEnabled());
		}
	}

	private Company _company;

	@Inject
	private CompanyFeatureFlagsProvider _companyFeatureFlagsProvider;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}