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

package com.liferay.commerce.account.service;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for CommerceAccountGroup. This utility wraps
 * <code>com.liferay.commerce.account.service.impl.CommerceAccountGroupLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceAccountGroupLocalService
 * @generated
 */
public class CommerceAccountGroupLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.account.service.impl.CommerceAccountGroupLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @bridged
	 */
	public static com.liferay.commerce.account.model.CommerceAccountGroup
			addCommerceAccountGroup(
				long companyId, String name, int type, boolean system,
				String externalReferenceCode,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addCommerceAccountGroup(
			companyId, name, type, system, externalReferenceCode,
			serviceContext);
	}

	/**
	 * @bridged
	 */
	public static void checkGuestCommerceAccountGroup(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().checkGuestCommerceAccountGroup(companyId);
	}

	/**
	 * @bridged
	 */
	public static com.liferay.commerce.account.model.CommerceAccountGroup
			deleteCommerceAccountGroup(
				com.liferay.commerce.account.model.CommerceAccountGroup
					commerceAccountGroup)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteCommerceAccountGroup(commerceAccountGroup);
	}

	/**
	 * @bridged
	 */
	public static com.liferay.commerce.account.model.CommerceAccountGroup
			deleteCommerceAccountGroup(long commerceAccountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteCommerceAccountGroup(commerceAccountGroupId);
	}

	/**
	 * @bridged
	 */
	public static com.liferay.commerce.account.model.CommerceAccountGroup
		fetchByExternalReferenceCode(
			long companyId, String externalReferenceCode) {

		return getService().fetchByExternalReferenceCode(
			companyId, externalReferenceCode);
	}

	public static com.liferay.commerce.account.model.CommerceAccountGroup
		fetchCommerceAccountGroup(long commerceAccountGroupId) {

		return getService().fetchCommerceAccountGroup(commerceAccountGroupId);
	}

	public static com.liferay.commerce.account.model.CommerceAccountGroup
			getCommerceAccountGroup(long commerceAccountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getCommerceAccountGroup(commerceAccountGroupId);
	}

	/**
	 * @bridged
	 */
	public static java.util.List
		<com.liferay.commerce.account.model.CommerceAccountGroup>
			getCommerceAccountGroups(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.account.model.CommerceAccountGroup>
						orderByComparator) {

		return getService().getCommerceAccountGroups(
			companyId, start, end, orderByComparator);
	}

	/**
	 * @bridged
	 */
	public static java.util.List
		<com.liferay.commerce.account.model.CommerceAccountGroup>
			getCommerceAccountGroupsByCommerceAccountId(
				long commerceAccountId) {

		return getService().getCommerceAccountGroupsByCommerceAccountId(
			commerceAccountId);
	}

	/**
	 * @bridged
	 */
	public static int getCommerceAccountGroupsCount(long companyId) {
		return getService().getCommerceAccountGroupsCount(companyId);
	}

	/**
	 * @bridged
	 */
	public static java.util.List<Long>
		getCommerceAccountUserIdsFromAccountGroupIds(
			long[] commerceAccountGroupIds, int start, int end) {

		return getService().getCommerceAccountUserIdsFromAccountGroupIds(
			commerceAccountGroupIds, start, end);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @bridged
	 */
	public static java.util.List
		<com.liferay.commerce.account.model.CommerceAccountGroup>
				searchCommerceAccountGroups(
					long companyId, String keywords, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().searchCommerceAccountGroups(
			companyId, keywords, start, end, sort);
	}

	/**
	 * @bridged
	 */
	public static int searchCommerceAccountsGroupCount(
			long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().searchCommerceAccountsGroupCount(
			companyId, keywords);
	}

	/**
	 * @bridged
	 */
	public static com.liferay.commerce.account.model.CommerceAccountGroup
			updateCommerceAccountGroup(
				long commerceAccountGroupId, String name,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateCommerceAccountGroup(
			commerceAccountGroupId, name, serviceContext);
	}

	public static CommerceAccountGroupLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<CommerceAccountGroupLocalService, CommerceAccountGroupLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			CommerceAccountGroupLocalService.class);

		ServiceTracker
			<CommerceAccountGroupLocalService, CommerceAccountGroupLocalService>
				serviceTracker =
					new ServiceTracker
						<CommerceAccountGroupLocalService,
						 CommerceAccountGroupLocalService>(
							 bundle.getBundleContext(),
							 CommerceAccountGroupLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}