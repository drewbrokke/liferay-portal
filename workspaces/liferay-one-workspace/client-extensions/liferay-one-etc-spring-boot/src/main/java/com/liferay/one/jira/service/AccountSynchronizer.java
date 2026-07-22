/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.EntitlementConverter;
import com.liferay.one.jira.converter.ExternalLinkConverter;
import com.liferay.one.jira.converter.PostalAddressConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.exception.AccountNotFoundException;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.model.JiraBusinessEvent;
import com.liferay.one.model.AccountSupportInfo;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.model.Project;
import com.liferay.one.model.Property;
import com.liferay.one.service.AccountService;
import com.liferay.one.service.CommerceOrderService;
import com.liferay.one.service.EntitlementService;
import com.liferay.one.service.OrganizationService;
import com.liferay.one.service.ProjectService;
import com.liferay.one.service.PropertyService;
import com.liferay.one.service.UserAccountService;
import com.liferay.one.util.role.EmployeeRoles;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class AccountSynchronizer {

	public void syncAccount(Account account) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing account " + account.getExternalReferenceCode() +
					" to JSM");
		}

		List<UserAccount> accountUserAccounts =
			_userAccountService.getAccountUserAccounts(account.getId());

		Map<String, Object> attributeValues = _getAccountAttributeValues(
			account, accountUserAccounts);

		_syncAccountAsset(
			account, attributeValues, account.getExternalReferenceCode(),
			account.getName());

		_syncContactRoleAssignments(account, accountUserAccounts);

		for (Project project : _projectService.getProjects(account.getId())) {
			try {
				_syncAccountAsset(
					account, attributeValues,
					project.getExternalReferenceCode(), project.getName());
			}
			catch (Exception exception) {
				_log.error(
					"Unable to sync project " +
						project.getExternalReferenceCode(),
					exception);
			}
		}
	}

	public void syncProject(Project project) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing project " + project.getExternalReferenceCode() +
					" to JSM");
		}

		String accountExternalReferenceCode =
			project.getAccountExternalReferenceCode();

		if (Validator.isNull(accountExternalReferenceCode)) {
			throw new AccountNotFoundException(
				"Project " + project.getExternalReferenceCode() +
					" has no account external reference code");
		}

		Account account = _accountService.getAccount(
			accountExternalReferenceCode);

		_syncAccountAsset(
			account,
			_getAccountAttributeValues(
				account,
				_userAccountService.getAccountUserAccounts(account.getId())),
			project.getExternalReferenceCode(), project.getName());
	}

	private void _addJiraBusinessEventLine(
		List<String> lines, String fieldName, String value) {

		if (Validator.isNull(value)) {
			return;
		}

		lines.add(fieldName + ": " + value);
	}

	private <T> T _findFirst(List<T> list, Predicate<T> predicate) {
		if (ListUtil.isEmpty(list)) {
			return null;
		}

		for (T t : list) {
			if (predicate.test(t)) {
				return t;
			}
		}

		return null;
	}

	private <T> T _findFirst(T[] arr, Predicate<T> predicate) {
		if (arr == null) {
			return null;
		}

		return _findFirst(Arrays.asList(arr), predicate);
	}

	private Map<String, Object> _getAccountAttributeValues(
			Account account, List<UserAccount> accountUserAccounts)
		throws Exception {

		AccountUserAccountBucket accountUserAccountBucket =
			_getAccountUserAccountBucket(account, accountUserAccounts);

		AccountSupportInfo accountSupportInfo =
			_commerceOrderService.getAccountSupportInfo(
				account.getId(), account.getDefaultBillingAddressId());

		return LinkedHashMapBuilder.<String, Object>put(
			AccountConstants.ATTRIBUTE_NAME_ASSIGNED_TEAMS,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_teamConverter,
				_organizationService.getAccountOrganizations(account.getId()),
				Organization::getExternalReferenceCode)
		).put(
			AccountConstants.ATTRIBUTE_NAME_BUSINESS_EVENTS,
			_toBusinessEventsFieldValue(account)
		).put(
			AccountConstants.ATTRIBUTE_NAME_CUSTOMER_CONTACTS,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_contactConverter,
				accountUserAccountBucket.getCustomerUserAccounts(),
				UserAccount::getExternalReferenceCode)
		).put(
			AccountConstants.ATTRIBUTE_NAME_ENTITLEMENTS,
			_assetReferenceObjectService.getOrCreateReferenceObjectIds(
				_entitlementConverter,
				_entitlementService.getActiveEntitlementDefinitions(
					account.getId()),
				EntitlementDefinition::getDisplayName,
				_entitlementConverter::toAssetObject)
		).put(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_assetReferenceObjectService.getOrCreateReferenceObjectIds(
				_externalLinkConverter, _getExternalLinkProperties(account),
				Property::getExternalReferenceCode,
				_externalLinkConverter::toAssetObject)
		).put(
			AccountConstants.ATTRIBUTE_NAME_LANGUAGE,
			GetterUtil.getString(accountSupportInfo.getSupportLanguage())
		).put(
			AccountConstants.ATTRIBUTE_NAME_POSTAL_ADDRESSES,
			() -> {
				AccountContactInformation accountContactInformation =
					account.getAccountContactInformation();

				if (accountContactInformation == null) {
					return null;
				}

				return _assetReferenceObjectService.
					getOrCreateReferenceObjectIds(
						_postalAddressConverter,
						ListUtil.fromArray(
							accountContactInformation.getPostalAddresses()),
						postalAddress -> String.valueOf(postalAddress.getId()),
						_postalAddressConverter::toAssetObject);
			}
		).put(
			AccountConstants.ATTRIBUTE_NAME_SUPPORT_REGION,
			GetterUtil.getString(accountSupportInfo.getSupportRegion())
		).put(
			AccountConstants.ATTRIBUTE_NAME_WORKER_CONTACTS,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_contactConverter,
				accountUserAccountBucket.getWorkerUserAccounts(),
				UserAccount::getExternalReferenceCode)
		).build();
	}

	private AccountUserAccountBucket _getAccountUserAccountBucket(
		Account account, List<UserAccount> accountUserAccounts) {

		AccountUserAccountBucket accountUserAccountBucket =
			new AccountUserAccountBucket();

		for (UserAccount accountUserAccount : accountUserAccounts) {
			AccountBrief accountBrief = _findFirst(
				Arrays.asList(accountUserAccount.getAccountBriefs()),
				accountBrief1 -> Objects.equals(
					account.getExternalReferenceCode(),
					accountBrief1.getExternalReferenceCode()));

			if (accountBrief == null) {
				_log.error(
					"accountBrief is null for user account = " +
						accountUserAccount);

				continue;
			}

			RoleBrief roleBrief = _findFirst(
				accountBrief.getRoleBriefs(),
				roleBrief1 -> _employeeRoleNames.contains(
					roleBrief1.getName()));

			if (roleBrief != null) {
				accountUserAccountBucket.addWorkerUserAccount(
					accountUserAccount);
			}
			else {
				accountUserAccountBucket.addCustomerUserAccount(
					accountUserAccount);
			}
		}

		return accountUserAccountBucket;
	}

	private List<Property> _getExternalLinkProperties(Account account)
		throws Exception {

		List<Property> externalLinkProperties = new ArrayList<>();

		List<Property> properties = _propertyService.getAccountProperties(
			account.getId());

		for (Property property : properties) {
			if (_externalLinkConverter.isExternalLinkProperty(property)) {
				externalLinkProperties.add(property);
			}
		}

		return externalLinkProperties;
	}

	private void _syncAccountAsset(
			Account account, Map<String, Object> attributeValues,
			String externalKey, String name)
		throws Exception {

		JiraAssetObject assetObject = _accountConverter.toAssetObject(
			account, externalKey, name);

		for (Map.Entry<String, Object> entry : attributeValues.entrySet()) {
			assetObject.setAttributeValue(entry.getKey(), entry.getValue());
		}

		_assetObjectUpsertService.upsert(_accountConverter, assetObject);
	}

	private void _syncContactRoleAssignments(
		Account account, List<UserAccount> accountUserAccounts) {

		for (UserAccount accountUserAccount : accountUserAccounts) {
			AccountBrief accountBrief = _findFirst(
				Arrays.asList(accountUserAccount.getAccountBriefs()),
				accountBrief1 -> Objects.equals(
					account.getExternalReferenceCode(),
					accountBrief1.getExternalReferenceCode()));

			if (accountBrief == null) {
				continue;
			}

			RoleBrief[] roleBriefs = accountBrief.getRoleBriefs();

			if (roleBriefs == null) {
				continue;
			}

			for (RoleBrief roleBrief : roleBriefs) {
				try {
					_accountContactRoleAssignmentSynchronizer.
						syncAssignContactRole(
							roleBrief.getExternalReferenceCode(),
							accountUserAccount.getExternalReferenceCode(),
							account.getExternalReferenceCode());
				}
				catch (Exception exception) {
					_log.error(
						StringBundler.concat(
							"Unable to sync account contact role assignment ",
							"for role ", roleBrief.getExternalReferenceCode()),
						exception);
				}
			}
		}
	}

	private String _toBusinessEventFieldValuePart(
		JiraBusinessEvent jiraBusinessEvent) {

		List<String> lines = new ArrayList<>();

		_addJiraBusinessEventLine(lines, "name", jiraBusinessEvent.getName());
		_addJiraBusinessEventLine(
			lines, "targetGoLiveDateTime",
			jiraBusinessEvent.getPlannedEventDate());
		_addJiraBusinessEventLine(
			lines, "description", jiraBusinessEvent.getDescription());
		_addJiraBusinessEventLine(
			lines, "type", jiraBusinessEvent.getEventTypeName());
		_addJiraBusinessEventLine(
			lines, "currentVersion",
			jiraBusinessEvent.getCurrentLiferayVersionName());
		_addJiraBusinessEventLine(
			lines, "newVersion", jiraBusinessEvent.getNewLiferayVersionName());

		return StringUtil.merge(lines, ",\n");
	}

	private String _toBusinessEventsFieldValue(Account account)
		throws Exception {

		List<String> parts = new ArrayList<>();

		List<JiraBusinessEvent> jiraBusinessEvents =
			_jiraBusinessEventService.getJiraBusinessEvents(
				account.getExternalReferenceCode());

		for (JiraBusinessEvent businessEvent : jiraBusinessEvents) {
			String part = _toBusinessEventFieldValuePart(businessEvent);

			if (Validator.isNotNull(part)) {
				parts.add(part);
			}
		}

		return StringUtil.merge(parts, "\n\n");
	}

	private static final Log _log = LogFactory.getLog(
		AccountSynchronizer.class);

	@Autowired
	private AccountContactRoleAssignmentSynchronizer
		_accountContactRoleAssignmentSynchronizer;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetObjectUpsertService _assetObjectUpsertService;

	@Autowired
	private AssetReferenceObjectService _assetReferenceObjectService;

	@Autowired
	private CommerceOrderService _commerceOrderService;

	@Autowired
	private ContactConverter _contactConverter;

	private final List<String> _employeeRoleNames = EmployeeRoles.getNames();

	@Autowired
	private EntitlementConverter _entitlementConverter;

	@Autowired
	private EntitlementService _entitlementService;

	@Autowired
	private ExternalLinkConverter _externalLinkConverter;

	@Autowired
	private JiraBusinessEventService _jiraBusinessEventService;

	@Autowired
	private OrganizationService _organizationService;

	@Autowired
	private PostalAddressConverter _postalAddressConverter;

	@Autowired
	private ProjectService _projectService;

	@Autowired
	private PropertyService _propertyService;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private UserAccountService _userAccountService;

	private static class AccountUserAccountBucket {

		public void addCustomerUserAccount(UserAccount userAccount) {
			_customerUserAccounts.add(userAccount);
		}

		public void addWorkerUserAccount(UserAccount userAccount) {
			_workerUserAccounts.add(userAccount);
		}

		public List<UserAccount> getCustomerUserAccounts() {
			return _customerUserAccounts;
		}

		public List<UserAccount> getWorkerUserAccounts() {
			return _workerUserAccounts;
		}

		private final List<UserAccount> _customerUserAccounts =
			new ArrayList<>();
		private final List<UserAccount> _workerUserAccounts = new ArrayList<>();

	}

}