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

package com.liferay.commerce.account.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.account.constants.CommerceAccountConstants;
import com.liferay.commerce.account.exception.CommerceAccountNameException;
import com.liferay.commerce.account.exception.CommerceAccountOrdersException;
import com.liferay.commerce.account.exception.DuplicateCommerceAccountException;
import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.model.impl.CommerceAccountImpl;
import com.liferay.commerce.account.service.base.CommerceAccountLocalServiceBaseImpl;
import com.liferay.commerce.account.util.CommerceAccountRoleHelper;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountLocalServiceImpl
	extends CommerceAccountLocalServiceBaseImpl {

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount addBusinessCommerceAccount(
			String name, long parentCommerceAccountId, String email,
			String taxId, boolean active, String externalReferenceCode,
			long[] userIds, String[] emailAddresses,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		// Commerce Account

		CommerceAccount commerceAccount =
			commerceAccountLocalService.addCommerceAccount(
				name, parentCommerceAccountId, email, taxId,
				CommerceAccountConstants.ACCOUNT_TYPE_BUSINESS, active,
				externalReferenceCode, serviceContext);

		// Check commerce account roles

		_commerceAccountRoleHelper.checkCommerceAccountRoles(serviceContext);

		Role role = roleLocalService.getRole(
			serviceContext.getCompanyId(),
			CommerceAccountConstants.ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		// Commerce account user rels

		commerceAccountUserRelLocalService.addCommerceAccountUserRels(
			commerceAccount.getCommerceAccountId(), userIds, emailAddresses,
			new long[] {role.getRoleId()}, serviceContext);

		return commerceAccount;
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount addCommerceAccount(CommerceAccount commerceAccount) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount addCommerceAccount(
			String name, long parentCommerceAccountId, String email,
			String taxId, int type, boolean active,
			String externalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		// Commerce Account

		User user = userLocalService.getUser(serviceContext.getUserId());

		parentCommerceAccountId = getParentCommerceAccountId(
			serviceContext.getCompanyId(), parentCommerceAccountId);

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		validate(serviceContext.getCompanyId(), 0, name, externalReferenceCode);

		AccountEntry accountEntry = accountEntryLocalService.addAccountEntry(
			user.getUserId(), parentCommerceAccountId, name, null, null, email,
			null, taxId, CommerceAccountImpl.toAccountEntryType(type),
			CommerceAccountImpl.toAccountEntryStatus(active), serviceContext);

		if (externalReferenceCode == null) {
			accountEntry.setExternalReferenceCode(externalReferenceCode);

			accountEntry = accountEntryLocalService.updateAccountEntry(
				accountEntry);
		}

		return CommerceAccountImpl.fromAccountEntry(accountEntry);
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount addPersonalCommerceAccount(
			long userId, String taxId, String externalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		User user = userLocalService.getUser(userId);

		serviceContext.setUserId(userId);

		// Commerce account

		CommerceAccount commerceAccount =
			commerceAccountLocalService.addCommerceAccount(
				user.getFullName(),
				CommerceAccountConstants.DEFAULT_PARENT_ACCOUNT_ID,
				user.getEmailAddress(), taxId,
				CommerceAccountConstants.ACCOUNT_TYPE_PERSONAL, true,
				externalReferenceCode, serviceContext);

		// Commerce account user rel

		commerceAccountUserRelLocalService.addCommerceAccountUserRel(
			commerceAccount.getCommerceAccountId(), userId, serviceContext);

		return commerceAccount;
	}

	@Override
	public CommerceAccount createCommerceAccount(long commerceAccountId) {
		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.createAccountEntry(commerceAccountId));
	}

	@Override
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return super.createPersistedModel(primaryKeyObj);
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceAccount deleteCommerceAccount(
			CommerceAccount commerceAccount)
		throws PortalException {

		long commerceAccountId = commerceAccount.getCommerceAccountId();

		// Commerce account organization rels

		commerceAccountOrganizationRelLocalService.
			deleteCommerceAccountOrganizationRelsByCommerceAccountId(
				commerceAccountId);

		// Commerce account user rels

		commerceAccountUserRelLocalService.
			deleteCommerceAccountUserRelsByCommerceAccountId(commerceAccountId);

		Group commerceAccountGroup =
			commerceAccountLocalService.getCommerceAccountGroup(
				commerceAccountId);

		// Commerce account user roles

		userGroupRoleLocalService.deleteUserGroupRolesByGroupId(
			commerceAccountGroup.getGroupId());

		// Commerce account

		try {
			accountEntryLocalService.deleteAccountEntry(commerceAccountId);
		}
		catch (ModelListenerException modelListenerException) {
			throw new CommerceAccountOrdersException(modelListenerException);
		}

		// Resources

		//		@todo check permissions
		resourceLocalService.deleteResource(
			commerceAccount, ResourceConstants.SCOPE_INDIVIDUAL);

		// Expando

		expandoRowLocalService.deleteRows(commerceAccountId);

		return commerceAccount;
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount deleteCommerceAccount(long commerceAccountId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.deleteAccountEntry(commerceAccountId));
	}

	/**
	 * @bridged
	 */
	@Override
	public void deleteCommerceAccounts(long companyId) throws PortalException {
		accountEntryLocalService.deleteAccountEntriesByCompanyId(companyId);
	}

	/**
	 * @bridged
	 */
	@Override
	public void deleteLogo(long commerceAccountId) throws PortalException {
		AccountEntry accountEntry = accountEntryLocalService.getAccountEntry(
			commerceAccountId);

		_portal.updateImageId(accountEntry, false, null, "logoId", 0, 0, 0);
	}

	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return accountEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(DSLQuery dslQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DynamicQuery dynamicQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		throw new UnsupportedOperationException();
	}

	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection) {

		throw new UnsupportedOperationException();
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount fetchByExternalReferenceCode(
		long companyId, String externalReferenceCode) {

		if (Validator.isBlank(externalReferenceCode)) {
			return null;
		}

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchAccountEntryByReferenceCode(
				companyId, externalReferenceCode));
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount fetchCommerceAccount(long commerceAccountId) {
		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchAccountEntry(commerceAccountId));
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount fetchCommerceAccountByReferenceCode(
		long companyId, String externalReferenceCode) {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchAccountEntryByReferenceCode(
				companyId, externalReferenceCode));
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommerceAccount getCommerceAccount(long commerceAccountId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.getAccountEntry(commerceAccountId));
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount getCommerceAccount(
			long userId, long commerceAccountId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchUserAccountEntry(
				userId, commerceAccountId));
	}

	/**
	 * @bridged
	 */
	@Override
	public Group getCommerceAccountGroup(long commerceAccountId)
		throws PortalException {

		AccountEntry accountEntry = accountEntryLocalService.getAccountEntry(
			commerceAccountId);

		Group group = accountEntry.getAccountEntryGroup();

		if (group != null) {
			return group;
		}

		throw new PortalException();
	}

	@Override
	public List<CommerceAccount> getCommerceAccounts(int start, int end) {
		return TransformUtil.transform(
			accountEntryLocalService.getAccountEntries(start, end),
			CommerceAccountImpl::fromAccountEntry);
	}

	@Override
	public int getCommerceAccountsCount() {
		return accountEntryLocalService.getAccountEntriesCount();
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount getGuestCommerceAccount(long companyId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.getGuestAccountEntry(companyId));
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {
		//		@todo remove usages

		throw new UnsupportedOperationException();
	}

	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return accountEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount getPersonalCommerceAccount(long userId)
		throws PortalException {

		CommerceAccount commerceAccount = CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchPersonAccountEntry(userId));

		if (commerceAccount != null) {
			return commerceAccount;
		}

		User user = userLocalService.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setUserId(userId);

		return commerceAccountLocalService.addPersonalCommerceAccount(
			userId, StringPool.BLANK, StringPool.BLANK, serviceContext);
	}

	/**
	 * @bridged
	 */
	@Override
	public List<CommerceAccount> getUserCommerceAccounts(
			long userId, Long parentCommerceAccountId, int commerceSiteType,
			String keywords, Boolean active, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			accountEntryLocalService.getUserAccountEntries(
				userId, parentCommerceAccountId, keywords,
				new String[] {
					CommerceAccountImpl.toAccountEntryType(commerceSiteType)
				},
				CommerceAccountImpl.toAccountEntryStatus(active), start, end),
			CommerceAccountImpl::fromAccountEntry);
	}

	/**
	 * @bridged
	 */
	@Override
	public List<CommerceAccount> getUserCommerceAccounts(
			long userId, Long parentCommerceAccountId, int commerceSiteType,
			String keywords, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			accountEntryLocalService.getUserAccountEntries(
				userId, parentCommerceAccountId, keywords,
				new String[] {
					CommerceAccountImpl.toAccountEntryType(commerceSiteType)
				},
				start, end),
			CommerceAccountImpl::fromAccountEntry);
	}

	/**
	 * @bridged
	 */
	@Override
	public int getUserCommerceAccountsCount(
			long userId, Long parentCommerceAccountId, int commerceSiteType,
			String keywords)
		throws PortalException {

		return commerceAccountLocalService.getUserCommerceAccountsCount(
			userId, parentCommerceAccountId, commerceSiteType, keywords, null);
	}

	/**
	 * @bridged
	 */
	@Override
	public int getUserCommerceAccountsCount(
			long userId, Long parentCommerceAccountId, int commerceSiteType,
			String keywords, Boolean active)
		throws PortalException {

		return accountEntryLocalService.getUserAccountEntriesCount(
			userId, parentCommerceAccountId, keywords,
			new String[] {
				CommerceAccountImpl.toAccountEntryType(commerceSiteType)
			},
			CommerceAccountImpl.toAccountEntryStatus(active));
	}

	/**
	 * @bridged
	 */
	@Override
	public List<CommerceAccount> searchCommerceAccounts(
			long companyId, long parentCommerceAccountId, String keywords,
			int type, Boolean active, int start, int end, Sort sort)
		throws PortalException {

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"type", CommerceAccountImpl.toAccountEntryType(type)
			).put(
				"status", () -> CommerceAccountImpl.toAccountEntryStatus(active)
			).build();

		String fieldName = null;
		boolean reverse = false;

		if (sort != null) {
			fieldName = sort.getFieldName();
			reverse = sort.isReverse();
		}

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			accountEntryLocalService.search(
				companyId, keywords, params, start, end - start, fieldName,
				reverse);

		return TransformUtil.transform(
			baseModelSearchResult.getBaseModels(),
			CommerceAccountImpl::fromAccountEntry);
	}

	/**
	 * @bridged
	 */
	@Override
	public int searchCommerceAccountsCount(
			long companyId, long parentCommerceAccountId, String keywords,
			int type, Boolean active)
		throws PortalException {

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"type", CommerceAccountImpl.toAccountEntryType(type)
			).put(
				"status", () -> CommerceAccountImpl.toAccountEntryStatus(active)
			).build();

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			accountEntryLocalService.search(
				companyId, keywords, params, QueryUtil.ALL_POS, 0, null, false);

		return baseModelSearchResult.getLength();
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount setActive(long commerceAccountId, boolean active)
		throws PortalException {

		AccountEntry accountEntry = accountEntryLocalService.getAccountEntry(
			commerceAccountId);

		accountEntry.setStatus(
			CommerceAccountImpl.toAccountEntryStatus(active));

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.updateAccountEntry(accountEntry));
	}

	@Override
	public CommerceAccount updateCommerceAccount(
		CommerceAccount commerceAccount) {

		throw new UnsupportedOperationException();
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount updateCommerceAccount(
			long commerceAccountId, String name, boolean logo, byte[] logoBytes,
			String email, String taxId, boolean active,
			long defaultBillingAddressId, long defaultShippingAddressId,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceAccountLocalService.updateCommerceAccount(
			commerceAccountId, name, logo, logoBytes, email, taxId, active,
			defaultBillingAddressId, defaultShippingAddressId, null,
			serviceContext);
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateCommerceAccount(
			long commerceAccountId, String name, boolean logo, byte[] logoBytes,
			String email, String taxId, boolean active,
			long defaultBillingAddressId, long defaultShippingAddressId,
			String externalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		AccountEntry accountEntry = accountEntryLocalService.getAccountEntry(
			commerceAccountId);

		if (defaultBillingAddressId == -1) {
			defaultBillingAddressId = accountEntry.getDefaultBillingAddressId();
		}

		if (defaultShippingAddressId == -1) {
			defaultShippingAddressId =
				accountEntry.getDefaultShippingAddressId();
		}

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		// Using this method will skip default address validation.
		// Use updateDefault*Address if you want validation

		validate(
			serviceContext.getCompanyId(), accountEntry.getAccountEntryId(),
			name, accountEntry.getExternalReferenceCode());

		accountEntry = accountEntryLocalService.updateAccountEntry(
			accountEntry.getAccountEntryId(),
			accountEntry.getParentAccountEntryId(), name,
			accountEntry.getDescription(), !logo,
			accountEntry.getDomainsStringArray(), email, logoBytes, taxId,
			CommerceAccountImpl.toAccountEntryStatus(active), serviceContext);

		accountEntry.setDefaultBillingAddressId(defaultBillingAddressId);
		accountEntry.setDefaultShippingAddressId(defaultShippingAddressId);

		if (Validator.isNotNull(externalReferenceCode)) {
			accountEntry.setExternalReferenceCode(externalReferenceCode);
		}

		accountEntry.setExpandoBridgeAttributes(serviceContext);

		accountEntry = accountEntryLocalService.updateAccountEntry(
			accountEntry);

		return CommerceAccountImpl.fromAccountEntry(accountEntry);
	}

	/**
	 * @bridged
	 * @deprecated As of Mueller (7.2.x), pass Default Billing/Shipping Ids
	 */
	@Deprecated
	@Override
	public CommerceAccount updateCommerceAccount(
			long commerceAccountId, String name, boolean logo, byte[] logoBytes,
			String email, String taxId, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return updateCommerceAccount(
			commerceAccountId, name, logo, logoBytes, email, taxId, active, -1,
			-1, serviceContext);
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateDefaultBillingAddress(
			long commerceAccountId, long commerceAddressId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.updateDefaultBillingAddressId(
				commerceAccountId, commerceAddressId));
	}

	/**
	 * @bridged
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateDefaultShippingAddress(
			long commerceAccountId, long commerceAddressId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.updateDefaultShippingAddressId(
				commerceAccountId, commerceAddressId));
	}

	/**
	 * @bridged
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateStatus(
			long userId, long commerceAccountId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.updateStatus(commerceAccountId, status));
	}

	/**
	 * @bridged
	 */
	@Override
	public CommerceAccount upsertCommerceAccount(
			String name, long parentCommerceAccountId, boolean logo,
			byte[] logoBytes, String email, String taxId, int type,
			boolean active, String externalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}
		else {
			CommerceAccount commerceAccount =
				CommerceAccountImpl.fromAccountEntry(
					accountEntryLocalService.fetchAccountEntryByReferenceCode(
						serviceContext.getCompanyId(), externalReferenceCode));

			if (commerceAccount != null) {
				return commerceAccountLocalService.updateCommerceAccount(
					commerceAccount.getCommerceAccountId(), name, logo,
					logoBytes, email, taxId, active, serviceContext);
			}
		}

		return commerceAccountLocalService.addCommerceAccount(
			name, parentCommerceAccountId, email, taxId, type, active,
			externalReferenceCode, serviceContext);
	}

	/**
	 * @bridged
	 */
	protected long getParentCommerceAccountId(
		long companyId, long parentCommerceAccountId) {

		if (parentCommerceAccountId !=
				CommerceAccountConstants.DEFAULT_PARENT_ACCOUNT_ID) {

			// Ensure parent account exists and belongs to the proper
			// company

			CommerceAccount parentCommerceAccount =
				CommerceAccountImpl.fromAccountEntry(
					accountEntryLocalService.fetchAccountEntry(
						parentCommerceAccountId));

			if ((parentCommerceAccount == null) ||
				(companyId != parentCommerceAccount.getCompanyId())) {

				parentCommerceAccountId =
					CommerceAccountConstants.DEFAULT_PARENT_ACCOUNT_ID;
			}
		}

		return parentCommerceAccountId;
	}

	/**
	 * @bridged
	 */
	protected void validate(
			long companyId, long commerceAccountId, String name,
			String externalReferenceCode)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new CommerceAccountNameException();
		}

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		CommerceAccount commerceAccount = CommerceAccountImpl.fromAccountEntry(
			accountEntryLocalService.fetchAccountEntryByReferenceCode(
				companyId, externalReferenceCode));

		if ((commerceAccount != null) &&
			(commerceAccount.getCommerceAccountId() != commerceAccountId)) {

			throw new DuplicateCommerceAccountException(
				"There is another commerce account with external reference " +
					"code " + externalReferenceCode);
		}
	}

	@ServiceReference(type = CommerceAccountRoleHelper.class)
	private CommerceAccountRoleHelper _commerceAccountRoleHelper;

	@ServiceReference(type = Portal.class)
	private Portal _portal;

}