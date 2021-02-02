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

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.CountryFinder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.CountryImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class CountryFinderImpl
	extends CountryFinderBaseImpl implements CountryFinder {

	public static final String FIND_BY_COMMERCE_WAREHOUSES =
		CountryFinder.class.getName() + ".findByCommerceInventoryWarehouses";

	public static final String FIND_BY_COMMERCE_CHANNEL =
		CountryFinder.class.getName() + ".findByCommerceChannel";

	@Override
	public List<Country> findByCommerceInventoryWarehouses(
		long companyId, boolean all) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_COMMERCE_WAREHOUSES);

			if (all) {
				sql = StringUtil.removeSubstring(sql, _ALL_SQL);
			}
			else {
				sql = StringUtil.replace(sql, _ALL_SQL, _ACTIVE_SQL);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Country", CountryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<Country>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Country> findByCommerceChannel(
		long commerceChannelId, boolean billingAllowed, boolean shippingAllowed,
		int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_COMMERCE_CHANNEL);

			if (billingAllowed) {
				sql = StringUtil.replace(
					sql, _BILLING_SQL, _BILLING_ALLOWED_SQL);
			}
			else {
				sql = StringUtil.removeSubstring(sql, _BILLING_SQL);
			}

			if (shippingAllowed) {
				sql = StringUtil.replace(
					sql, _SHIPPING_SQL, _SHIPPING_ALLOWED_SQL);
			}
			else {
				sql = StringUtil.removeSubstring(sql, _SHIPPING_SQL);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Country", CountryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				ClassNameLocalServiceUtil.getClassNameId(Country.class));
			queryPos.add(commerceChannelId);

			return (List<Country>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _ACTIVE_SQL =
		"AND (CIWarehouse.active_ = [$TRUE$])";

	private static final String _ALL_SQL = "[$ALL$]";

	private static final String _BILLING_ALLOWED_SQL =
		"AND (CommerceCountry.billingAllowed = [$TRUE$])";

	private static final String _BILLING_SQL = "[$BILLING$]";

	private static final String _SHIPPING_ALLOWED_SQL =
		"AND (CommerceCountry.shippingAllowed = [$TRUE$])";

	private static final String _SHIPPING_SQL = "[$SHIPPING$]";

}