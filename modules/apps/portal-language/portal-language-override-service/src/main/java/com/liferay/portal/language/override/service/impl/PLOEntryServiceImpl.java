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

package com.liferay.portal.language.override.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.base.PLOEntryServiceBaseImpl;

import org.osgi.service.component.annotations.Component;

import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=portallanguageoverride",
		"json.web.service.context.path=PLOEntry"
	},
	service = AopService.class
)
public class PLOEntryServiceImpl extends PLOEntryServiceBaseImpl {

	@Override
	public PLOEntry deletePLOEntry(
		long companyId, String key, String languageId) {

		return ploEntryLocalService.deletePLOEntry(companyId, key, languageId);
	}

	@Override
	public void deletePLOEntries(long companyId, String key) {
		ploEntryLocalService.deletePLOEntries(companyId, key);
	}

	@Override
	public void setPLOEntries(
		long companyId, long userId, String key, Map<Locale, String> localizationMap)
		throws PortalException {

		ploEntryLocalService.setPLOEntries(companyId, userId, key, localizationMap);
	}
}