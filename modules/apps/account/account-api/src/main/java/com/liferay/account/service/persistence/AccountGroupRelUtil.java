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

package com.liferay.account.service.persistence;

import com.liferay.account.model.AccountGroupRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the account group rel service. This utility wraps <code>com.liferay.account.service.persistence.impl.AccountGroupRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupRelPersistence
 * @generated
 */
public class AccountGroupRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(AccountGroupRel accountGroupRel) {
		getPersistence().clearCache(accountGroupRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, AccountGroupRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AccountGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AccountGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AccountGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AccountGroupRel update(AccountGroupRel accountGroupRel) {
		return getPersistence().update(accountGroupRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AccountGroupRel update(
		AccountGroupRel accountGroupRel, ServiceContext serviceContext) {

		return getPersistence().update(accountGroupRel, serviceContext);
	}

	/**
	 * Returns all the account group rels where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @return the matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountGroupId(
		long accountGroupId) {

		return getPersistence().findByAccountGroupId(accountGroupId);
	}

	/**
	 * Returns a range of all the account group rels where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @return the range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountGroupId(
		long accountGroupId, int start, int end) {

		return getPersistence().findByAccountGroupId(
			accountGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the account group rels where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().findByAccountGroupId(
			accountGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account group rels where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAccountGroupId(
			accountGroupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	public static AccountGroupRel findByAccountGroupId_First(
			long accountGroupId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountGroupId_First(
			accountGroupId, orderByComparator);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAccountGroupId_First(
		long accountGroupId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().fetchByAccountGroupId_First(
			accountGroupId, orderByComparator);
	}

	/**
	 * Returns the last account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	public static AccountGroupRel findByAccountGroupId_Last(
			long accountGroupId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountGroupId_Last(
			accountGroupId, orderByComparator);
	}

	/**
	 * Returns the last account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAccountGroupId_Last(
		long accountGroupId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().fetchByAccountGroupId_Last(
			accountGroupId, orderByComparator);
	}

	/**
	 * Returns the account group rels before and after the current account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param AccountGroupRelId the primary key of the current account group rel
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account group rel
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	public static AccountGroupRel[] findByAccountGroupId_PrevAndNext(
			long AccountGroupRelId, long accountGroupId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountGroupId_PrevAndNext(
			AccountGroupRelId, accountGroupId, orderByComparator);
	}

	/**
	 * Removes all the account group rels where accountGroupId = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 */
	public static void removeByAccountGroupId(long accountGroupId) {
		getPersistence().removeByAccountGroupId(accountGroupId);
	}

	/**
	 * Returns the number of account group rels where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @return the number of matching account group rels
	 */
	public static int countByAccountGroupId(long accountGroupId) {
		return getPersistence().countByAccountGroupId(accountGroupId);
	}

	/**
	 * Returns all the account group rels where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountEntryId(
		long accountEntryId) {

		return getPersistence().findByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns a range of all the account group rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @return the range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountEntryId(
		long accountEntryId, int start, int end) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the account group rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account group rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account group rels
	 */
	public static List<AccountGroupRel> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	public static AccountGroupRel findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountEntryId_First(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAccountEntryId_First(
		long accountEntryId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().fetchByAccountEntryId_First(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns the last account group rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	public static AccountGroupRel findByAccountEntryId_Last(
			long accountEntryId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountEntryId_Last(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns the last account group rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAccountEntryId_Last(
		long accountEntryId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().fetchByAccountEntryId_Last(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns the account group rels before and after the current account group rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param AccountGroupRelId the primary key of the current account group rel
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account group rel
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	public static AccountGroupRel[] findByAccountEntryId_PrevAndNext(
			long AccountGroupRelId, long accountEntryId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAccountEntryId_PrevAndNext(
			AccountGroupRelId, accountEntryId, orderByComparator);
	}

	/**
	 * Removes all the account group rels where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	public static void removeByAccountEntryId(long accountEntryId) {
		getPersistence().removeByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of account group rels where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account group rels
	 */
	public static int countByAccountEntryId(long accountEntryId) {
		return getPersistence().countByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the account group rel where accountGroupId = &#63; and accountEntryId = &#63; or throws a <code>NoSuchGroupRelException</code> if it could not be found.
	 *
	 * @param accountGroupId the account group ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	public static AccountGroupRel findByAGI_AEI(
			long accountGroupId, long accountEntryId)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByAGI_AEI(accountGroupId, accountEntryId);
	}

	/**
	 * Returns the account group rel where accountGroupId = &#63; and accountEntryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param accountGroupId the account group ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAGI_AEI(
		long accountGroupId, long accountEntryId) {

		return getPersistence().fetchByAGI_AEI(accountGroupId, accountEntryId);
	}

	/**
	 * Returns the account group rel where accountGroupId = &#63; and accountEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountGroupId the account group ID
	 * @param accountEntryId the account entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	public static AccountGroupRel fetchByAGI_AEI(
		long accountGroupId, long accountEntryId, boolean useFinderCache) {

		return getPersistence().fetchByAGI_AEI(
			accountGroupId, accountEntryId, useFinderCache);
	}

	/**
	 * Removes the account group rel where accountGroupId = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 * @param accountEntryId the account entry ID
	 * @return the account group rel that was removed
	 */
	public static AccountGroupRel removeByAGI_AEI(
			long accountGroupId, long accountEntryId)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().removeByAGI_AEI(accountGroupId, accountEntryId);
	}

	/**
	 * Returns the number of account group rels where accountGroupId = &#63; and accountEntryId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account group rels
	 */
	public static int countByAGI_AEI(long accountGroupId, long accountEntryId) {
		return getPersistence().countByAGI_AEI(accountGroupId, accountEntryId);
	}

	/**
	 * Caches the account group rel in the entity cache if it is enabled.
	 *
	 * @param accountGroupRel the account group rel
	 */
	public static void cacheResult(AccountGroupRel accountGroupRel) {
		getPersistence().cacheResult(accountGroupRel);
	}

	/**
	 * Caches the account group rels in the entity cache if it is enabled.
	 *
	 * @param accountGroupRels the account group rels
	 */
	public static void cacheResult(List<AccountGroupRel> accountGroupRels) {
		getPersistence().cacheResult(accountGroupRels);
	}

	/**
	 * Creates a new account group rel with the primary key. Does not add the account group rel to the database.
	 *
	 * @param AccountGroupRelId the primary key for the new account group rel
	 * @return the new account group rel
	 */
	public static AccountGroupRel create(long AccountGroupRelId) {
		return getPersistence().create(AccountGroupRelId);
	}

	/**
	 * Removes the account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param AccountGroupRelId the primary key of the account group rel
	 * @return the account group rel that was removed
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	public static AccountGroupRel remove(long AccountGroupRelId)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().remove(AccountGroupRelId);
	}

	public static AccountGroupRel updateImpl(AccountGroupRel accountGroupRel) {
		return getPersistence().updateImpl(accountGroupRel);
	}

	/**
	 * Returns the account group rel with the primary key or throws a <code>NoSuchGroupRelException</code> if it could not be found.
	 *
	 * @param AccountGroupRelId the primary key of the account group rel
	 * @return the account group rel
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	public static AccountGroupRel findByPrimaryKey(long AccountGroupRelId)
		throws com.liferay.account.exception.NoSuchGroupRelException {

		return getPersistence().findByPrimaryKey(AccountGroupRelId);
	}

	/**
	 * Returns the account group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param AccountGroupRelId the primary key of the account group rel
	 * @return the account group rel, or <code>null</code> if a account group rel with the primary key could not be found
	 */
	public static AccountGroupRel fetchByPrimaryKey(long AccountGroupRelId) {
		return getPersistence().fetchByPrimaryKey(AccountGroupRelId);
	}

	/**
	 * Returns all the account group rels.
	 *
	 * @return the account group rels
	 */
	public static List<AccountGroupRel> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @return the range of account group rels
	 */
	public static List<AccountGroupRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of account group rels
	 */
	public static List<AccountGroupRel> findAll(
		int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of account group rels
	 */
	public static List<AccountGroupRel> findAll(
		int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the account group rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of account group rels.
	 *
	 * @return the number of account group rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static AccountGroupRelPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<AccountGroupRelPersistence, AccountGroupRelPersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			AccountGroupRelPersistence.class);

		ServiceTracker<AccountGroupRelPersistence, AccountGroupRelPersistence>
			serviceTracker =
				new ServiceTracker
					<AccountGroupRelPersistence, AccountGroupRelPersistence>(
						bundle.getBundleContext(),
						AccountGroupRelPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}