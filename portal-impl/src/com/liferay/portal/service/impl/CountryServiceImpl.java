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

import com.liferay.portal.kernel.exception.CountryA2Exception;
import com.liferay.portal.kernel.exception.CountryA3Exception;
import com.liferay.portal.kernel.exception.CountryIddException;
import com.liferay.portal.kernel.exception.CountryNameException;
import com.liferay.portal.kernel.exception.CountryNumberException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.CountryServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class CountryServiceImpl extends CountryServiceBaseImpl {

	@Override
	public Country addCountry(
			String name, String a2, String a3, String number, String idd,
			boolean active)
		throws PortalException {

		if (!getPermissionChecker().isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(
				getPermissionChecker());
		}

		if (Validator.isNull(name)) {
			throw new CountryNameException();
		}

		if (Validator.isNull(a2)) {
			throw new CountryA2Exception();
		}

		if (Validator.isNull(a3)) {
			throw new CountryA3Exception();
		}

		if (Validator.isNull(number)) {
			throw new CountryNumberException();
		}

		if (Validator.isNull(idd)) {
			throw new CountryIddException();
		}

		long countryId = counterLocalService.increment();

		Country country = countryPersistence.create(countryId);

		country.setName(name);
		country.setA2(a2);
		country.setA3(a3);
		country.setNumber(number);
		country.setIdd(idd);
		country.setActive(active);

		return countryPersistence.update(country);
	}

	@Override
	public Country fetchCountry(long countryId) {
		return countryPersistence.fetchByPrimaryKey(countryId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country fetchCountryByA2(String a2) {
		return countryPersistence.fetchByA2(a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country fetchCountryByA3(String a3) {
		return countryPersistence.fetchByA3(a3);
	}

	@Override
	public Country fetchCountryByCompanyIdAndA2(long companyId, String a2) {
		return countryPersistence.fetchByC_A2(companyId, a2);
	}

	@Override
	public Country fetchCountryByCompanyIdAndA3(long companyId, String a3) {
		return countryPersistence.fetchByC_A3(companyId, a3);
	}

	@Override
	public List<Country> getCountries() {
		return countryPersistence.findAll();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@AccessControlled(guestAccessEnabled = true)
	@Deprecated
	@Override
	public List<Country> getCountries(boolean active) {
		return countryPersistence.findByActive(active);
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<Country> getCountriesByCompanyIdAndActive(
		long companyId, boolean active) {

		return countryPersistence.findByC_Active(companyId, active);
	}

	@Override
	public Country getCountry(long countryId) throws PortalException {
		return countryPersistence.findByPrimaryKey(countryId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByA2(String a2) throws PortalException {
		return countryPersistence.findByA2(a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByA3(String a3) throws PortalException {
		return countryPersistence.findByA3(a3);
	}

	@Override
	public Country getCountryByCompanyIdAndA2(long companyId, String a2)
		throws NoSuchCountryException {

		return countryPersistence.findByC_A2(companyId, a2);
	}

	@Override
	public Country getCountryByCompanyIdAndA3(long companyId, String a3)
		throws NoSuchCountryException {

		return countryPersistence.findByC_A3(companyId, a3);
	}

	@Override
	public Country getCountryByCompanyIdAndName(long companyId, String name)
		throws NoSuchCountryException {

		return countryPersistence.findByC_Name(companyId, name);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByName(String name) throws PortalException {
		return countryPersistence.findByName(name);
	}

}