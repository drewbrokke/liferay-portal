/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.manager;

/**
 * @author Thiago Buarque
 */
public interface FeatureFlagPreferencesManager {

	public Boolean isEnabled(long companyId, String key);

	public void setEnabled(long companyId, String key, boolean enabled);

}