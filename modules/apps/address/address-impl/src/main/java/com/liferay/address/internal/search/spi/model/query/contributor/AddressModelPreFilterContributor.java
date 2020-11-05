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

package com.liferay.address.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.Address",
	service = ModelPreFilterContributor.class
)
public class AddressModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByClass(booleanFilter, searchContext);
		_filterByTypeId(booleanFilter, searchContext);
	}

	private void _filterByClass(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		long classNameId = GetterUtil.getLong(
			searchContext.getAttribute(Field.CLASS_NAME_ID));

		if (classNameId > 0) {
			booleanFilter.addTerm(
				Field.CLASS_NAME_ID, String.valueOf(classNameId),
				BooleanClauseOccur.MUST);
		}

		long classPK = GetterUtil.getLong(
			searchContext.getAttribute(Field.CLASS_PK), Long.MIN_VALUE);

		if (classPK > Long.MIN_VALUE) {
			booleanFilter.addTerm(
				Field.CLASS_PK, String.valueOf(classPK),
				BooleanClauseOccur.MUST);
		}
	}

	private void _filterByTypeId(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params == null) {
			return;
		}

		long[] typeIds = (long[])params.getOrDefault("typeIds", new long[0]);
		String[] typeNames = (String[])params.get("typeNames");

		if (ArrayUtil.isNotEmpty(typeNames)) {
			long classNameId = GetterUtil.getLong(
				searchContext.getAttribute(Field.CLASS_NAME_ID));

			if (classNameId > 0) {
				ClassName className = _classNameLocalService.fetchByClassNameId(
					classNameId);

				if (className != null) {
					List<Long> listTypeIds = new ArrayList<>();

					for (String typeName : typeNames) {
						ListType listType = _listTypeLocalService.getListType(
							typeName,
							className.getClassName() +
								ListTypeConstants.ADDRESS);

						listTypeIds.add(listType.getListTypeId());
					}

					typeIds = ArrayUtil.append(
						typeIds, ArrayUtil.toLongArray(listTypeIds));

					params.put("typeIds", ArrayUtil.unique(typeIds));
				}
			}
		}

		if (ArrayUtil.isNotEmpty(typeIds)) {
			TermsFilter termsFilter = new TermsFilter("typeId");

			termsFilter.addValues(ArrayUtil.toStringArray(typeIds));

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

}