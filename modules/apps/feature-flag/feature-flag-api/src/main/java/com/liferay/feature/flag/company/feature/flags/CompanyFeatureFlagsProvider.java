/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.company.feature.flags;

import java.util.function.Function;

/**
 * @author Thiago Buarque
 */
public interface CompanyFeatureFlagsProvider {

	public CompanyFeatureFlags getOrCreateCompanyFeatureFlags(long companyId);

	public void setEnabled(long companyId, String key, boolean enabled);

	public <T> T withCompanyFeatureFlags(
		long companyId, Function<CompanyFeatureFlags, T> function);

}