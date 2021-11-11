/*
 * *
 *  * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package com.liferay.portal.language.override.service.internal;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.language.LanguageMapWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageMapWrapper.class)
public class PLOLanguageMapWrapper implements LanguageMapWrapper {

	@Override
	public String get(String key, Locale locale) {
		Map<String, String> overrideMap =
			_ploLanguageOverrideCache.getOverrideMap(_getCompanyId(), locale);

		return overrideMap.getOrDefault(key, null);
	}

	@Override
	public Set<String> keySet(Locale locale) {
		Map<String, String> overrideMap =
			_ploLanguageOverrideCache.getOverrideMap(_getCompanyId(), locale);

		return overrideMap.keySet();
	}

	@Reference
	private PLOLanguageOverrideCache _ploLanguageOverrideCache;

	private long _getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}
}
