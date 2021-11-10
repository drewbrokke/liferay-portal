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

package com.liferay.portal.language.override.web.internal.portlet;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.InheritableMap;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.language.LanguageMapWrapper;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryModel;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageMapWrapper.class)
public class PLOLanguageMapWrapper implements LanguageMapWrapper {

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Override
	public String get(String key, Locale locale) {
		PLOEntry ploEntry = _ploEntryLocalService.fetchPLOEntry(
			_getCompanyId(), key, LanguageUtil.getLanguageId(locale));

		if (ploEntry == null) {
			return null;
		}

		return ploEntry.getValue();
	}

	@Override
	public Set<String> keySet(Locale locale) {
		List<PLOEntry> ploEntries =
			_ploEntryLocalService.getPLOEntriesByLanguageId(
				_getCompanyId(), LanguageUtil.getLanguageId(locale));

		if (ListUtil.isEmpty(ploEntries)) {
			return Collections.emptySet();
		}

		Stream<PLOEntry> ploEntriesStream = ploEntries.stream();

		return ploEntriesStream.map(
			PLOEntryModel::getKey
		).collect(Collectors.toSet());
	}

	private long _getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}
}
