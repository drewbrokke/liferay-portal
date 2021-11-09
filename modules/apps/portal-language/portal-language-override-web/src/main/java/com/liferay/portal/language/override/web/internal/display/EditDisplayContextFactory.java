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

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
@Component(service = EditDisplayContextFactory.class)
public class EditDisplayContextFactory {

	public EditDisplayContext create(RenderRequest renderRequest, RenderResponse renderResponse) {
		String key = ParamUtil.getString(renderRequest, "key");

		return new EditDisplayContext(
			key,
			_getLocalizedValuesMap(_portal.getCompanyId(renderRequest), key)
		);
	}

	private LocalizedValuesMap _getLocalizedValuesMap(long companyId, String key) {
		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap();

		for (Locale locale : LanguageUtil.getCompanyAvailableLocales(companyId)) {
			localizedValuesMap.put(locale, LanguageUtil.get(locale, key));
		}

		return localizedValuesMap;
	}

	@Reference
	private Portal _portal;
}
