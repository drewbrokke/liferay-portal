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

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.CountryLocalServiceBaseImpl;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @see CountryLocalServiceBaseImpl
 */
public class CountryLocalServiceImpl extends CountryLocalServiceBaseImpl {

	@Override
	public Country addCountry(
		boolean active, String a2, String a3, boolean billingAllowed, String idd, String name,
		String number, double position, boolean shippingAllowed, boolean subjectToVAT,
		Map<String, String> titleMap, ServiceContext serviceContext) {

		return null;
	}

	@Override
	public void deleteCountries(long companyId) {
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Country deleteCountry(Country country) {
		return null;
	}

	@Override
	public Country deleteCountry(long countryId) {
		return null;
	}

	@Override
	public Country fetchCountryByC_A2(long countryId, String a2) {
		return null;
	}

	@Override
	public Country fetchCountryByC_N(long companyId, String number) {
		return null;
	}

	@Override
	public List<Country> getCountries(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return null;
	}

	@Override
	public Country getCountry(long companyId, String a2) {

		return null;
	}

	@Override
	public Country updateCountry(
			long countryId, boolean active,
			String a2, String a3, boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			Map<String, String> titleMap) {

		return null;
	}

}