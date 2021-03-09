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

package com.liferay.commerce.account.model.internal.search.spi.model.permission.contributor;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.spi.model.permission.SearchPermissionFilterContributor;

import org.osgi.service.component.annotations.Component;

import java.util.List;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = SearchPermissionFilterContributor.class
)
public class AccountEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (className.equals(AccountEntry.class.getName())) {
//			List<Role> roles = _resourcePermissionLocalService.getRoles(
//				companyId, permissionName, ResourceConstants.SCOPE_INDIVIDUAL,
//				String.valueOf(classPK), viewActionId);
		}

//		for (BooleanClause<Filter> clause :
//				booleanFilter.getShouldBooleanClauses()) {
//
//			if (clause.getClause() instanceof TermsFilter) {
//				TermsFilter termsFilter = (TermsFilter)clause.getClause();
//
//				String field = termsFilter.getField();
//
//				if (field.equals(Field.ROLE_ID)) {
//					TermsFilter roleIdsTermsFilter = new TermsFilter(
//						Field.ROLE_IDS);
//
//					roleIdsTermsFilter.addValues(termsFilter.getValues());
//
//					booleanFilter.add(roleIdsTermsFilter);
//
//					break;
//				}
//			}
//		}
	}

}