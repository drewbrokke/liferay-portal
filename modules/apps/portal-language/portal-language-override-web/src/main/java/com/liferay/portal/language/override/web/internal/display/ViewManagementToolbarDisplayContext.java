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

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Drew Brokke
 */
public class ViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					getPortletURL(), "mvcPath", "/edit.jsp", "backURL",
					String.valueOf(getPortletURL()), "key", StringPool.BLANK);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-language-key"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> Objects.equals(getNavigation(), "override"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "override"));
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(searchContainer.getIteratorURL());
	}

	@Override
	public List<ViewTypeItem> getViewTypeItems() {
		ViewTypeItemList viewTypeItemList = new ViewTypeItemList();

		String selectedLanguage = ParamUtil.getString(
			liferayPortletRequest, "selectedLanguage",
			LanguageUtil.getLanguageId(
				PortalUtil.getLocale(liferayPortletRequest)));

		Set<Locale> companyAvailableLocales =
			LanguageUtil.getCompanyAvailableLocales(
				PortalUtil.getCompanyId(liferayPortletRequest));

		for (Locale locale : companyAvailableLocales) {
			String languageId = LanguageUtil.getLanguageId(locale);

			String icon = StringUtil.toLowerCase(
				TextFormatter.format(languageId, TextFormatter.O));

			viewTypeItemList.add(
				viewTypeItem -> {
					viewTypeItem.setActive(
						Objects.equals(selectedLanguage, languageId));
					viewTypeItem.setHref(
						HttpUtil.setParameter(
							String.valueOf(getPortletURL()),
							liferayPortletResponse.getNamespace() +
								"selectedLanguage",
							languageId));
					viewTypeItem.setIcon(icon);
					viewTypeItem.setLabel(languageId);
					viewTypeItem.put("symbolLeft", icon);
				});
		}

		return viewTypeItemList;
	}

	@Override
	public Boolean isDisabled() {
		return false;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"override"};
	}

}