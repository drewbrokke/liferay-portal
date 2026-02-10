/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.feature.flag.listener;

import com.liferay.portal.feature.flag.FeatureFlagsBag;
import com.liferay.portal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.List;

/**
 * @author Drew Brokke
 * @author Thiago Buarque
 */
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) throws ModelListenerException {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_processDeprecationFeatureFlags(company.getCompanyId());

				return null;
			});
	}

	protected void afterPropertiesSet() {
		_companyLocalService.forEachCompanyId(
			this::_processDeprecationFeatureFlags);
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private void _processDeprecationFeatureFlags(long companyId) {
		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		boolean processed = GetterUtil.getBoolean(
			portalPreferences.getValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE,
				FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED));

		if (processed) {
			return;
		}

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		List<FeatureFlag> deprecationFeatureFlags =
			featureFlagsBag.getFeatureFlags(
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag deprecationFeatureFlag : deprecationFeatureFlags) {
			_featureFlagsBagProvider.setEnabled(
				companyId, deprecationFeatureFlag.getKey(), false);
		}

		portalPreferences = _getPortalPreferences(companyId);

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED, "true");

		_portalPreferencesLocalService.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, portalPreferences);
	}

	@BeanReference(type = CompanyLocalService.class)
	private CompanyLocalService _companyLocalService;

	@BeanReference(type = FeatureFlagsBagProvider.class)
	private FeatureFlagsBagProvider _featureFlagsBagProvider;

	@BeanReference(type = PortalPreferencesLocalService.class)
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}