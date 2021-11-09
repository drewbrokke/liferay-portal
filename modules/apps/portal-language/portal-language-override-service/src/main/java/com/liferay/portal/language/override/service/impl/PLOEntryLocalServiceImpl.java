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

import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.language.override.service.base.PLOEntryLocalServiceBaseImpl;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Drew Brokke
 */
@Component(
	property = "model.class.name=com.liferay.portal.language.override.model.PLOEntry",
	service = AopService.class
)
public class PLOEntryLocalServiceImpl extends PLOEntryLocalServiceBaseImpl {

	public PLOEntry addOrUpdatePLOEntry(
			long companyId, String key, String languageId, String value)
		throws PortalException {

		PLOEntry ploEntry = fetchPLOEntry(companyId, key, languageId);

		if (ploEntry == null) {
			return addPLOEntry(companyId, key, languageId, value);
		}

		return updatePLOEntry(ploEntry.getPloEntryId(), value);
	}

	public PLOEntry addPLOEntry(
			long companyId, String key, String languageId, String value)
		throws PortalException {

		PLOEntry ploEntry = createPLOEntry(counterLocalService.increment());

		User user = GuestOrUserUtil.getGuestOrUser();

		ploEntry.setUserId(user.getUserId());
		ploEntry.setUserName(user.getUserUuid());
		ploEntry.setKey(key);
		ploEntry.setLanguageId(languageId);
		ploEntry.setValue(value);

		return addPLOEntry(ploEntry);
	}

	public PLOEntry fetchPLOEntry(long companyId, String key, String languageId)
		throws PortalException {

		return ploEntryPersistence.fetchByC_K_L(companyId, key, languageId);
	}

	@Override
	public List<PLOEntry> getPLOEntries(long companyId) {
		return ploEntryPersistence.findByCompanyId(companyId);
	}

	public List<PLOEntry> getPLOEntriesByKey(long companyId, String key) {
		return ploEntryPersistence.findByC_K(companyId, key);
	}

	@Override
	public List<PLOEntry> getPLOEntriesByLanguageId(
		long companyId, String languageId) {

		return ploEntryPersistence.findByC_L(companyId, languageId);
	}

	public PLOEntry getPLOEntry(long companyId, String key, String languageId)
		throws PortalException {

		return ploEntryPersistence.findByC_K_L(companyId, key, languageId);
	}

	public BaseModelSearchResult<PLOEntry> searchPLOEntries(
		long companyId, String keywords, int cur, int delta,
		String orderByField, boolean reverse) {

		if (orderByField == null) {
			orderByField = "key";
		}

		DSLQuery orderByStep = getGroupByStep(
			companyId, keywords,
			DSLQueryFactoryUtil.select(PLOEntryTable.INSTANCE)
		).orderBy(
			PLOEntryTable.INSTANCE,
			OrderByComparatorFactoryUtil.<PLOEntry>create(
				"PLOEntry", orderByField, !reverse)
		);

		if (!(cur == QueryUtil.ALL_POS || delta == QueryUtil.ALL_POS)) {
			int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
				cur, delta);

			orderByStep = ((LimitStep)orderByStep).limit(
				startAndEnd[0], startAndEnd[1]
			);
		}

		return new BaseModelSearchResult<>(
			dslQuery(orderByStep),
			dslQueryCount(
				getGroupByStep(
					companyId, keywords,
					DSLQueryFactoryUtil.countDistinct(
						PLOEntryTable.INSTANCE.ploEntryId))));
	}

	public void setPLOEntries(
			long companyId, String key, Map<String, String> valueMap)
		throws PortalException {

		for (String languageId : valueMap.keySet()) {
			addOrUpdatePLOEntry(
				companyId, key, languageId, valueMap.get(languageId));
		}

		for (PLOEntry ploEntry : getPLOEntriesByKey(companyId, key)) {
			if (Validator.isNull(valueMap.get(ploEntry.getLanguageId()))) {
				deletePLOEntry(ploEntry);
			}
		}
	}

	public PLOEntry updatePLOEntry(long ploEntryId, String value)
		throws PortalException {

		PLOEntry ploEntry = getPLOEntry(ploEntryId);

		ploEntry.setValue(value);

		return ploEntryPersistence.update(ploEntry);
	}

	private GroupByStep getGroupByStep(
		long companyId, String keywords, FromStep fromStep) {

		return fromStep.from(
			PLOEntryTable.INSTANCE
		).where(
			PLOEntryTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (Validator.isNull(keywords)) {
						return null;
					}

					String[] keywordsArray = _customSQL.keywords(keywords);

					return Predicate.withParentheses(
						Predicate.or(
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									PLOEntryTable.INSTANCE.key),
								keywordsArray),
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									DSLFunctionFactoryUtil.castClobText(
										PLOEntryTable.INSTANCE.value)),
								keywordsArray)));
				}
			)
		);
	}

	@Reference
	private CustomSQL _customSQL;

}