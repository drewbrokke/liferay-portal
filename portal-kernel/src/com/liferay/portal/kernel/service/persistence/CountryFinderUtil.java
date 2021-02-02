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

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CountryFinderUtil {

	public static java.util.List<com.liferay.portal.kernel.model.Country>
		findByCommerceInventoryWarehouses(long companyId, boolean all) {

		return getFinder().findByCommerceInventoryWarehouses(companyId, all);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Country>
		findByCommerceChannel(
			long commerceChannelId, boolean billingAllowed,
			boolean shippingAllowed, int start, int end) {

		return getFinder().findByCommerceChannel(
			commerceChannelId, billingAllowed, shippingAllowed, start, end);
	}

	public static CountryFinder getFinder() {
		if (_finder == null) {
			_finder = (CountryFinder)PortalBeanLocatorUtil.locate(
				CountryFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(CountryFinder finder) {
		_finder = finder;
	}

	private static CountryFinder _finder;

}