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
import com.liferay.commerce.account.internal.search.CommerceAccountIndexer;
import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.model.impl.CommerceAccountImpl;
import com.liferay.commerce.account.service.base.CommerceAccountLocalServiceBaseImpl;
import com.liferay.commerce.account.util.CommerceAccountRoleHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.vulcan.util.TransformUtil;
import com.liferay.users.admin.kernel.file.uploads.UserFileUploadsSettings;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		AccountEntry accountEntry =
			accountEntryLocalService.addAccountEntry(user.getUserId(),
				parentCommerceAccountId, name, null, null, email,
				null, taxId,
				CommerceAccountImpl.toAccountEntryType(type),
				CommerceAccountImpl.toAccountEntryStatus(active),
				serviceContext);

		if (externalReferenceCode == null) {
			accountEntry.setExternalReferenceCode(externalReferenceCode);

			accountEntry = accountEntryLocalService.updateAccountEntry(accountEntry);
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

	@Override
	public void deleteCommerceAccounts(long companyId) throws PortalException {
		List<CommerceAccount> commerceAccounts =
			commerceAccountPersistence.findByCompanyId(companyId);

		for (CommerceAccount commerceAccount : commerceAccounts) {
			commerceAccountLocalService.deleteCommerceAccount(commerceAccount);
		}
	}

	@Override
	public void deleteLogo(long commerceAccountId) throws PortalException {
		AccountEntry accountEntry =
			accountEntryLocalService.getAccountEntry(commerceAccountId);

		_portal.updateImageId(accountEntry, false, null, "logoId", 0, 0, 0);
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
	public CommerceAccount getCommerceAccount(
			long userId, long commerceAccountId)
		throws PortalException {

		return CommerceAccountImpl.fromAccountEntry(accountEntryLocalService.fetchUserAccountEntry(userId,
			commerceAccountId));
	}

	@Override
	public Group getCommerceAccountGroup(long commerceAccountId)
		throws PortalException {

		CommerceAccount commerceAccount =
			commerceAccountLocalService.getCommerceAccount(commerceAccountId);

		long classNameId = classNameLocalService.getClassNameId(
			CommerceAccount.class.getName());

		Group group = groupLocalService.fetchGroup(
			commerceAccount.getCompanyId(), classNameId, commerceAccountId);

		if (group != null) {
			return group;
		}

		throw new PortalException();
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

	@Override
	public List<CommerceAccount> getUserCommerceAccounts(
			long userId, Long parentCommerceAccountId, int commerceSiteType,
			String keywords, int start, int end)
		throws PortalException {

		return commerceAccountLocalService.getUserCommerceAccounts(
			userId, parentCommerceAccountId, commerceSiteType, keywords, null,
			start, end);
	}

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
				companyId, keywords, params,
				QueryUtil.ALL_POS, 0, null,
				false);

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

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateCommerceAccount(
			long commerceAccountId, String name, boolean logo, byte[] logoBytes,
			String email, String taxId, boolean active,
			long defaultBillingAddressId, long defaultShippingAddressId,
			String externalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		CommerceAccount commerceAccount =
			commerceAccountPersistence.findByPrimaryKey(commerceAccountId);

		if (defaultBillingAddressId == -1) {
			defaultBillingAddressId =
				commerceAccount.getDefaultBillingAddressId();
		}

		if (defaultShippingAddressId == -1) {
			defaultShippingAddressId =
				commerceAccount.getDefaultShippingAddressId();
		}

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		// Using this method will skip default address validation.
		// Use updateDefault*Address if you want validation

		validate(
			serviceContext.getCompanyId(),
			commerceAccount.getCommerceAccountId(), name,
			commerceAccount.getExternalReferenceCode());

		commerceAccount.setName(name);

		_portal.updateImageId(
			commerceAccount, logo, logoBytes, "logoId",
			_userFileUploadsSettings.getImageMaxSize(),
			_userFileUploadsSettings.getImageMaxHeight(),
			_userFileUploadsSettings.getImageMaxWidth());

		commerceAccount.setEmail(email);
		commerceAccount.setTaxId(taxId);
		commerceAccount.setActive(active);
		commerceAccount.setDefaultBillingAddressId(defaultBillingAddressId);
		commerceAccount.setDefaultShippingAddressId(defaultShippingAddressId);

		if (Validator.isNotNull(externalReferenceCode)) {
			commerceAccount.setExternalReferenceCode(externalReferenceCode);
		}

		commerceAccount.setExpandoBridgeAttributes(serviceContext);

		commerceAccount = commerceAccountPersistence.update(commerceAccount);

		// Workflow

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commerceAccount.getCompanyId(), WorkflowConstants.DEFAULT_GROUP_ID,
			commerceAccount.getUserId(), CommerceAccount.class.getName(),
			commerceAccountId, commerceAccount, serviceContext,
			Collections.emptyMap());
	}

	/**
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

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateDefaultBillingAddress(
			long commerceAccountId, long commerceAddressId)
		throws PortalException {

		CommerceAccount commerceAccount =
			commerceAccountPersistence.findByPrimaryKey(commerceAccountId);

		commerceAccount.setDefaultBillingAddressId(commerceAddressId);

		return commerceAccountPersistence.update(commerceAccount);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateDefaultShippingAddress(
			long commerceAccountId, long commerceAddressId)
		throws PortalException {

		CommerceAccount commerceAccount =
			commerceAccountPersistence.findByPrimaryKey(commerceAccountId);

		commerceAccount.setDefaultShippingAddressId(commerceAddressId);

		return commerceAccountPersistence.update(commerceAccount);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAccount updateStatus(
			long userId, long commerceAccountId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		Date now = new Date();

		CommerceAccount commerceAccount =
			commerceAccountPersistence.findByPrimaryKey(commerceAccountId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commerceAccount.getDisplayDate() != null) &&
			now.before(commerceAccount.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(now);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commerceAccount.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(now)) {
				commerceAccount.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commerceAccount.setExpirationDate(now);
		}

		commerceAccount.setStatus(status);

		User user = userLocalService.getUser(userId);

		commerceAccount.setStatusByUserId(user.getUserId());
		commerceAccount.setStatusByUserName(user.getFullName());

		commerceAccount.setStatusDate(modifiedDate);

		return commerceAccountPersistence.update(commerceAccount);
	}

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
				commerceAccountPersistence.fetchByC_ERC(
					serviceContext.getCompanyId(), externalReferenceCode);

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

	protected SearchContext buildSearchContext(
		long companyId, long parentCommerceAccountId, int type, Boolean active,
		int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			CommerceAccountIndexer.FIELD_PARENT_COMMERCE_ACCOUNT_ID,
			parentCommerceAccountId);

		if (active != null) {
			searchContext.setAttribute(
				CommerceAccountIndexer.FIELD_ACTIVE, active);
		}

		if (type >= 0) {
			searchContext.setAttribute(Field.TYPE, type);
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected List<CommerceAccount> getCommerceAccounts(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceAccount> commerceAccounts = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceAccountId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceAccount commerceAccount =
				commerceAccountPersistence.fetchByPrimaryKey(commerceAccountId);

			if (commerceAccount == null) {
				commerceAccounts = null;

				Indexer<CommerceAccount> indexer =
					IndexerRegistryUtil.getIndexer(CommerceAccount.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceAccount != null) {
				commerceAccounts.add(commerceAccount);
			}
		}

		return commerceAccounts;
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

	protected List<CommerceAccount> searchCommerceAccounts(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceAccount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceAccount.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceAccount> commerceAccounts = getCommerceAccounts(hits);

			if (commerceAccounts != null) {
				return commerceAccounts;
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected int searchCommerceAccountsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceAccount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceAccount.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
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

		CommerceAccount commerceAccount =
			CommerceAccountImpl.fromAccountEntry(accountEntryLocalService.fetchAccountEntryByReferenceCode(
				companyId, externalReferenceCode));

		if ((commerceAccount != null) &&
			(commerceAccount.getCommerceAccountId() != commerceAccountId)) {

			throw new DuplicateCommerceAccountException(
				"There is another commerce account with external reference " +
					"code " + externalReferenceCode);
		}
	}

	private List<Long> _getUserOrganizations(long userId)
		throws PortalException {

		List<Organization> organizations =
			organizationLocalService.getUserOrganizations(userId);

		User user = userLocalService.getUser(userId);

		ListIterator<Organization> organizationListIterator =
			organizations.listIterator();

		while (organizationListIterator.hasNext()) {
			Organization organization = organizationListIterator.next();

			for (Organization curOrganization :
					organizationLocalService.getOrganizations(
						user.getCompanyId(),
						organization.getTreePath() + "%")) {

				organizationListIterator.add(curOrganization);
			}
		}

		Stream<Organization> organizationStream = organizations.stream();

		return organizationStream.map(
			Organization::getOrganizationId
		).collect(
			Collectors.toList()
		);
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID
	};

	@ServiceReference(type = CommerceAccountRoleHelper.class)
	private CommerceAccountRoleHelper _commerceAccountRoleHelper;

	@ServiceReference(type = Portal.class)
	private Portal _portal;

	@ServiceReference(type = UserFileUploadsSettings.class)
	private UserFileUploadsSettings _userFileUploadsSettings;

}