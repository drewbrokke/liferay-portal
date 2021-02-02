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

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CountryFinderBaseImpl extends BasePersistenceImpl<Country> {

	public CountryFinderBaseImpl() {
		setModelClass(Country.class);

		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("idd", "idd_");
		dbColumnNames.put("number", "number_");

		setDBColumnNames(dbColumnNames);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return getCountryPersistence().getBadColumnNames();
	}

	/**
	 * Returns the country persistence.
	 *
	 * @return the country persistence
	 */
	public CountryPersistence getCountryPersistence() {
		return countryPersistence;
	}

	/**
	 * Sets the country persistence.
	 *
	 * @param countryPersistence the country persistence
	 */
	public void setCountryPersistence(CountryPersistence countryPersistence) {
		this.countryPersistence = countryPersistence;
	}

	@BeanReference(type = CountryPersistence.class)
	protected CountryPersistence countryPersistence;

	private static final Log _log = LogFactoryUtil.getLog(
		CountryFinderBaseImpl.class);

}