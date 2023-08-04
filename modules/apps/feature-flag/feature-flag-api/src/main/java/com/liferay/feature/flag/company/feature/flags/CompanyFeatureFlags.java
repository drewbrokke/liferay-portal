/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.company.feature.flags;

import com.liferay.feature.flag.model.FeatureFlag;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Thiago Buarque
 */
public interface CompanyFeatureFlags {

	public List<FeatureFlag> getFeatureFlags(Predicate<FeatureFlag> predicate);

	public String getJSON();

	public boolean isEnabled(String key);

	public void setEnabled(String key, boolean enabled);

}