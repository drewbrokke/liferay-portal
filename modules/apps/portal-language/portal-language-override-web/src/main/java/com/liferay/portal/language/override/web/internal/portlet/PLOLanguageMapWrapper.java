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

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.InheritableMap;
import com.liferay.portal.language.LanguageMapWrapper;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageMapWrapper.class)
public class PLOLanguageMapWrapper implements LanguageMapWrapper {

	@Override
	public Map<String, String> wrap(
		String languageId, Map<String, String> languageMap) {

		long companyId = CompanyThreadLocal.getCompanyId();

		List<PLOEntry> ploEntries =
			_ploEntryLocalService.getPLOEntriesByLanguageId(companyId,
				languageId);

		Stream<PLOEntry> ploEntryStream = ploEntries.stream();

		Map<String, String> overrideMap = ploEntryStream.collect(
			Collectors.toMap(PLOEntry::getKey, PLOEntry::getValue));

		InheritableMap<String, String> resultMap =
			new InheritableMap<>(overrideMap);

		resultMap.setParentMap(languageMap);

		return resultMap;
	}

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;
}
