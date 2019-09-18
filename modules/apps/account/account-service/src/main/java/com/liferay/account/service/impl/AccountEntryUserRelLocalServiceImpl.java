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

package com.liferay.account.service.impl;

import com.liferay.account.exception.DuplicateAccountEntryUserRelException;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.base.AccountEntryUserRelLocalServiceBaseImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountEntryUserRel",
	service = AopService.class
)
public class AccountEntryUserRelLocalServiceImpl
	extends AccountEntryUserRelLocalServiceBaseImpl {

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		AccountEntryUserRel accountEntryUserRel =
			accountEntryUserRelPersistence.fetchByAEI_AUI(
				accountEntryId, accountUserId);

		if (accountEntryUserRel != null) {
			throw new DuplicateAccountEntryUserRelException();
		}

		accountEntryLocalService.getAccountEntry(accountEntryId);
		userLocalService.getUser(accountUserId);

		accountEntryUserRel = createAccountEntryUserRel(
			counterLocalService.increment());

		accountEntryUserRel.setAccountEntryId(accountEntryId);
		accountEntryUserRel.setAccountUserId(accountUserId);

		return addAccountEntryUserRel(accountEntryUserRel);
	}

	@Override
	public List<User> searchAccountEntryUsers(
		long accountEntryId, String keywords, int start, int end,
		OrderByComparator<User> orderByComparator) {

		DynamicQuery userDynamicQuery = userLocalService.dynamicQuery();

		DynamicQuery accountEntryUserRelDynamicQuery =
			accountEntryUserRelLocalService.dynamicQuery();

		accountEntryUserRelDynamicQuery.add(
			RestrictionsFactoryUtil.eq("accountEntryId", accountEntryId));
		accountEntryUserRelDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("accountUserId"));

		List<Long> userIds = accountEntryUserRelLocalService.dynamicQuery(
			accountEntryUserRelDynamicQuery);

		Property userIdProperty = PropertyFactoryUtil.forName("userId");

		userDynamicQuery.add(userIdProperty.in(userIds));

		if (Validator.isNotNull(keywords)) {
			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			for (String keyword : StringUtil.split(keywords, CharPool.SPACE)) {
				keyword = CharPool.PERCENT + keyword + CharPool.PERCENT;

				disjunction.add(
					RestrictionsFactoryUtil.ilike("firstName", keyword));
				disjunction.add(
					RestrictionsFactoryUtil.ilike("middleName", keyword));
				disjunction.add(
					RestrictionsFactoryUtil.ilike("lastName", keyword));
				disjunction.add(
					RestrictionsFactoryUtil.ilike("screenName", keyword));
				disjunction.add(
					RestrictionsFactoryUtil.ilike("emailAddress", keyword));
			}

			userDynamicQuery.add(disjunction);
		}

		return userLocalService.dynamicQuery(
			userDynamicQuery, start, end, orderByComparator);
	}

	@Reference
	protected AccountEntryLocalService accountEntryLocalService;

}