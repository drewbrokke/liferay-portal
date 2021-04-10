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

package com.liferay.configuration.admin.web.internal.configuration.admin.display;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurablePortalPropertyProvider.class)
public class ConfigurablePortalPropertyProvider {

	public List<ConfigurablePortalProperty> getConfigurablePortalProperties() {
		return ListUtil.fromArray(
			new ConfigurablePortalProperty(
				PropsKeys.REDIRECT_URL_DOMAINS_ALLOWED));
	}

}