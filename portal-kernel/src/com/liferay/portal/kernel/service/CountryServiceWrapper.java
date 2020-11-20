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

package com.liferay.portal.kernel.service;

/**
 * Provides a wrapper for {@link CountryService}.
 *
 * @author Brian Wing Shun Chan
 * @see CountryService
 * @generated
 */
public class CountryServiceWrapper
	implements CountryService, ServiceWrapper<CountryService> {

	public CountryServiceWrapper(CountryService countryService) {
		_countryService = countryService;
	}

	@Override
	public com.liferay.portal.kernel.model.Country addCountry(
			java.lang.String a2, java.lang.String a3, boolean active,
			boolean billingAllowed, java.lang.String idd, java.lang.String name,
			java.lang.String number, double position, boolean shippingAllowed,
			boolean subjectToVAT, boolean zipRequired,
			java.util.Map<java.lang.String, java.lang.String> titleMap,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.addCountry(
			a2, a3, active, billingAllowed, idd, name, number, position,
			shippingAllowed, subjectToVAT, zipRequired, titleMap,
			serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.Country addCountry(
			java.lang.String name, java.lang.String a2, java.lang.String a3,
			java.lang.String number, java.lang.String idd, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.addCountry(name, a2, a3, number, idd, active);
	}

	@Override
	public void deleteCountry(long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_countryService.deleteCountry(countryId);
	}

	@Override
	public com.liferay.portal.kernel.model.Country fetchCountry(
		long countryId) {

		return _countryService.fetchCountry(countryId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public com.liferay.portal.kernel.model.Country fetchCountryByA2(
		java.lang.String a2) {

		return _countryService.fetchCountryByA2(a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public com.liferay.portal.kernel.model.Country fetchCountryByA3(
		java.lang.String a3) {

		return _countryService.fetchCountryByA3(a3);
	}

	@Override
	public com.liferay.portal.kernel.model.Country fetchCountryByCompanyIdAndA2(
		long companyId, java.lang.String a2) {

		return _countryService.fetchCountryByCompanyIdAndA2(companyId, a2);
	}

	@Override
	public com.liferay.portal.kernel.model.Country fetchCountryByCompanyIdAndA3(
		long companyId, java.lang.String a3) {

		return _countryService.fetchCountryByCompanyIdAndA3(companyId, a3);
	}

	@Override
	public int getCountByCompanyIdAndActive(long companyId, boolean active) {
		return _countryService.getCountByCompanyIdAndActive(companyId, active);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country>
		getCountries() {

		return _countryService.getCountries();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country> getCountries(
		boolean active) {

		return _countryService.getCountries(active);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country>
		getCountriesByCompanyId(long companyId) {

		return _countryService.getCountriesByCompanyId(companyId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country>
		getCountriesByCompanyId(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Country> orderByComparator) {

		return _countryService.getCountriesByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country>
		getCountriesByCompanyIdAndActive(long companyId, boolean active) {

		return _countryService.getCountriesByCompanyIdAndActive(
			companyId, active);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Country>
		getCountriesByCompanyIdAndActive(
			long companyId, boolean active, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Country> orderByComparator) {

		return _countryService.getCountriesByCompanyIdAndActive(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public int getCountriesCountByCompanyId(long companyId) {
		return _countryService.getCountriesCountByCompanyId(companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Country getCountry(long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.getCountry(countryId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public com.liferay.portal.kernel.model.Country getCountryByA2(
			java.lang.String a2)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.getCountryByA2(a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public com.liferay.portal.kernel.model.Country getCountryByA3(
			java.lang.String a3)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.getCountryByA3(a3);
	}

	@Override
	public com.liferay.portal.kernel.model.Country getCountryByCompanyIdAndA2(
			long companyId, java.lang.String a2)
		throws com.liferay.portal.kernel.exception.NoSuchCountryException {

		return _countryService.getCountryByCompanyIdAndA2(companyId, a2);
	}

	@Override
	public com.liferay.portal.kernel.model.Country getCountryByCompanyIdAndA3(
			long companyId, java.lang.String a3)
		throws com.liferay.portal.kernel.exception.NoSuchCountryException {

		return _countryService.getCountryByCompanyIdAndA3(companyId, a3);
	}

	@Override
	public com.liferay.portal.kernel.model.Country getCountryByCompanyIdAndName(
			long companyId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.NoSuchCountryException {

		return _countryService.getCountryByCompanyIdAndName(companyId, name);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public com.liferay.portal.kernel.model.Country getCountryByName(
			java.lang.String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.getCountryByName(name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _countryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.Country setActive(
			long countryId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.setActive(countryId, active);
	}

	@Override
	public com.liferay.portal.kernel.model.Country updateCountry(
			long countryId, java.lang.String a2, java.lang.String a3,
			boolean active, boolean billingAllowed, java.lang.String idd,
			java.lang.String name, java.lang.String number, double position,
			boolean shippingAllowed, boolean subjectToVAT,
			java.util.Map<java.lang.String, java.lang.String> titleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.updateCountry(
			countryId, a2, a3, active, billingAllowed, idd, name, number,
			position, shippingAllowed, subjectToVAT, titleMap);
	}

	@Override
	public com.liferay.portal.kernel.model.Country updateCountryGroupFilter(
			long countryId, boolean groupFilterEnabled)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryService.updateCountryGroupFilter(
			countryId, groupFilterEnabled);
	}

	@Override
	public CountryService getWrappedService() {
		return _countryService;
	}

	@Override
	public void setWrappedService(CountryService countryService) {
		_countryService = countryService;
	}

	private CountryService _countryService;

}