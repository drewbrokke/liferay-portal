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

package com.liferay.portal.language.override.web.internal.display;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = EditDisplayContextFactory.class)
public class EditDisplayContextFactory {

	public EditDisplayContext create(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		String key = ParamUtil.getString(renderRequest, "key");

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap();
		LocalizedValuesMap originalValuesLocalizedValuesMap =
			new LocalizedValuesMap();

		_populateLocalizedValuesMap(
			_portal.getCompanyId(renderRequest), key, localizedValuesMap,
			originalValuesLocalizedValuesMap);

		return new EditDisplayContext(
			ParamUtil.getString(renderRequest, "backURL"),
			key, localizedValuesMap, originalValuesLocalizedValuesMap,
			ParamUtil.getString(
				renderRequest, "selectedLanguage",
				LocaleUtil.toLanguageId(_portal.getLocale(renderRequest))));
	}

	private void _populateLocalizedValuesMap(
		long companyId, String key,
		LocalizedValuesMap localizedValuesMap,
		LocalizedValuesMap originalValuesLocalizedValuesMap) {

		if ((key == null) || key.equals(StringPool.BLANK)) {
			return;
		}

		for (Locale locale :
			LanguageUtil.getCompanyAvailableLocales(companyId)) {

			String languageId = LocaleUtil.toLanguageId(locale);

			PLOEntry ploEntry = _ploEntryLocalService.fetchPLOEntry(
				companyId, key, languageId);

			if (ploEntry != null) {
				originalValuesLocalizedValuesMap.put(locale,
					ploEntry.getOriginalValue());

				localizedValuesMap.put(locale, ploEntry.getValue());

				continue;
			}

			originalValuesLocalizedValuesMap.put(
				locale, LanguageUtil.get(locale, key));

		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

}