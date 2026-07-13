/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.*;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.jira.constants.PostalAddressConstants;
import com.liferay.one.jira.converter.*;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AccountAssetService;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.model.Entitlement;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.model.Property;
import com.liferay.one.model.SiteLanguage;
import com.liferay.one.permission.AccountPermission;
import com.liferay.one.service.*;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.*;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Jenny Chen
 */
@RequestMapping("/accounts")
@RestController
public class AccountsRestController extends OneBaseRestController {

	@DeleteMapping("/{externalReferenceCode}/user-accounts/{userId}")
	public void deleteUserAccounts(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@PathVariable("userId") long userId)
		throws Exception {

		_accountService.removeAccountUserAccount(
			externalReferenceCode, jwt, userId);
	}

	@DeleteMapping(
		"/{externalReferenceCode}/user-accounts/{userId}/account-roles" +
			"/{accountRoleId}"
	)
	public void deleteUserAccountsAccountRole(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@PathVariable("userId") long userId,
			@PathVariable("accountRoleId") long accountRoleId)
		throws Exception {

		_accountPermission.check(externalReferenceCode, ActionKeys.UPDATE, jwt);

		_accountService.removeAccountUserAccountRole(
			accountRoleId, externalReferenceCode, jwt, userId);
	}

	@GetMapping("/{externalReferenceCode}/jira/object-key")
	public ResponseEntity<String> getJiraObjectKey(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_accountPermission.check(externalReferenceCode, ActionKeys.VIEW, jwt);

		return new ResponseEntity<>(
			_accountAssetService.getAccountObjectKey(externalReferenceCode),
			HttpStatus.OK);
	}

	@PostMapping("/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM() throws Exception {
		try {
			List<Account> accounts = _accountService.getAllAccounts();

			if (_log.isInfoEnabled()) {
				_log.info("Syncing " + accounts.size() + " accounts to JSM");
			}

			for (Account account : accounts) {
				_syncAccount(account);
			}

			if (_log.isInfoEnabled()) {
				_log.info("Synced " + accounts.size() + " accounts to JSM");
			}
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing the JIRA object keys",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{externalReferenceCode}/sync-to-jsm")
	public ResponseEntity<Void> postSyncToJSM(
			@PathVariable("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		try {
			Account account = _accountService.getAccount(externalReferenceCode);

			AccountUserAccountBucket accountUserAccountBucket =
				getAccountUserAccountBucket(account);

			System.out.println(
				"accountUserAccountBucket = " + accountUserAccountBucket);
		}
		catch (Exception exception) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"There was a problem synchronizing the JIRA object keys",
				exception);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{externalReferenceCode}/user-accounts/{userId}")
	public void postUserAccounts(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@PathVariable("userId") long userId)
		throws Exception {

		_accountService.addAccountUserAccount(
			externalReferenceCode, jwt, userId);
	}

	@PostMapping(
		"/{externalReferenceCode}/user-accounts/{userId}/account-roles" +
			"/{accountRoleId}"
	)
	public void postUserAccountsAccountRole(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@PathVariable("userId") long userId,
			@PathVariable("accountRoleId") long accountRoleId)
		throws Exception {

		_accountService.addAccountUserAccountRole(
			accountRoleId, externalReferenceCode, jwt, userId);
	}

	private <T> T _findFirst(List<T> list, Predicate<T> predicate) {
		if ((list == null) || list.isEmpty()) {
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
		return _findFirst(Arrays.asList(arr), predicate);
	}

	private List<Organization> _getAssignedTeamOrganizations(Account account) {
		return null;
	}

	private String _getBusinessEvents(Account account) {
		return null;
	}

	private List<EntitlementDefinition> _getEntitlementDefinitions(
		Account account) {

		try {
			List<Entitlement> entitlements =
				_entitlementService.getEntitlements(
					"(r_accountEntryToEntitlement_accountEntryId eq '" +
						account.getId() + "')");

			Set<Long> entitlementDefinitionIds = new LinkedHashSet<>();

			for (Entitlement entitlement : entitlements) {
				long entitlementDefinitionId =
					entitlement.getEntitlementDefinitionId();

				if (entitlementDefinitionId > 0) {
					entitlementDefinitionIds.add(entitlementDefinitionId);
				}
			}

			if (entitlementDefinitionIds.isEmpty()) {
				return Collections.emptyList();
			}

			StringBundler sb = new StringBundler();

			int i = 0;

			for (Long entitlementDefinitionId : entitlementDefinitionIds) {
				if (i > 0) {
					sb.append(" or ");
				}

				sb.append("(id eq '");
				sb.append(entitlementDefinitionId);
				sb.append("')");

				i++;
			}

			return _entitlementDefinitionService.getEntitlementDefinitions(
				sb.toString());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get entitlement definitions for account " +
					account.getExternalReferenceCode(),
				exception);

			return null;
		}
	}

	private List<Property> _getExternalLinkProperties(Account account) {
		List<Property> externalLinkProperties = new ArrayList<>();

		try {
			List<Property> properties =
				_propertyService.getAccountProperties(
					account.getId());


			for (Property property : properties) {
				String[] split = StringUtil.split(property.getName(), CharPool.COLON);

				if (split.length == 2) {
					externalLinkProperties.add(property);
				}
			}
		}
		catch (Exception exception) {
			_log.error("Could not find External Link", exception);
		}

		return externalLinkProperties;
	}

	private String _getLanguage(Account account) {
		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if (accountContactInformation == null) {
			return null;
		}

		PostalAddress[] postalAddresses =
			accountContactInformation.getPostalAddresses();

		if (ArrayUtil.isEmpty(postalAddresses)) {
			return null;
		}

		PostalAddress primaryPostalAddress = postalAddresses[0];

		for (PostalAddress postalAddress : postalAddresses) {
			if (Boolean.TRUE.equals(postalAddress.getPrimary())) {
				primaryPostalAddress = postalAddress;

				break;
			}
		}

		try {
			SiteLanguage siteLanguage = _getSiteLanguageByCountryName(
				primaryPostalAddress.getAddressCountry());

			if (siteLanguage == null) {
				return null;
			}

			return siteLanguage.getName();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get site language for account " +
					account.getExternalReferenceCode(),
				exception);

			return null;
		}
	}

	private Boolean _getMailing(PostalAddress postalAddress) {
		return null;
	}

	private Integer _getOrganization(Account account) {
		return null;
	}

	private SiteLanguage _getSiteLanguageByCountryName(String countryName)
		throws Exception {

		if (Validator.isNull(countryName)) {
			return null;
		}

		List<SiteLanguage> siteLanguages = ListUtil.filter(
			_siteLanguageService.getSiteLanguages(),
			siteLanguage -> StringUtil.equalsIgnoreCase(
				countryName, siteLanguage.getCountryName()));

		if (siteLanguages.isEmpty()) {
			return null;
		}

		// A country can have several site languages (e.g. Spain has "ca-ES"
		// and "es-ES"). Prefer the country's own language, where the language
		// code mirrors the country code.

		for (SiteLanguage siteLanguage : siteLanguages) {
			String[] languageIdParts = StringUtil.split(
				siteLanguage.getId(), '-');

			if ((languageIdParts.length == 2) &&
				StringUtil.equalsIgnoreCase(
					languageIdParts[0], languageIdParts[1])) {

				return siteLanguage;
			}
		}

		return siteLanguages.get(0);
	}

	private String _getSupportRegion(Account account) {
		return null;
	}

	private void _syncAccount(Account account) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing account " + account.getExternalReferenceCode() +
					" to JSM");
		}

		JiraAssetObject assetObject = _accountConverter.toAssetObject(account);

		// TODO:

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_BUSINESS_EVENTS,
			_getBusinessEvents(account));

		// TODO:

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_LANGUAGE, _getLanguage(account));

		// TODO:

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ORGANIZATION,
			_getOrganization(account));

		// TODO:

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_SUPPORT_REGION,
			_getSupportRegion(account));

		AccountUserAccountBucket accountUserAccountBucket =
			getAccountUserAccountBucket(account);

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_CUSTOMER_CONTACTS,
			_assetReferenceResolverService.resolveObjectIds(
				_contactConverter,
				accountUserAccountBucket.getCustomerUserAccounts(),
				UserAccount::getExternalReferenceCode));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_WORKER_CONTACTS,
			_assetReferenceResolverService.resolveObjectIds(
				_contactConverter,
				accountUserAccountBucket.getWorkerUserAccounts(),
				UserAccount::getExternalReferenceCode));

		// TODO:

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ASSIGNED_TEAMS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_teamConverter, _getAssignedTeamOrganizations(account),
				Organization::getExternalReferenceCode,
				_teamConverter::toAssetObject));

		// TODO: needs testing

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ENTITLEMENTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_entitlementConverter, _getEntitlementDefinitions(account),
				EntitlementDefinition::getName,
				_entitlementConverter::toAssetObject));

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_externalLinkConverter, _getExternalLinkProperties(account),
				Property::getExternalReferenceCode,
				_externalLinkConverter::toAssetObject));

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_POSTAL_ADDRESSES,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_postalAddressConverter,
				ListUtil.fromArray(account.getPostalAddresses()),
				postalAddress -> String.valueOf(postalAddress.getId()),
				this::_toPostalAddressAssetObject));

		_accountAssetService.upsertJSMAccount(
			account.getExternalReferenceCode(), assetObject);
	}

	@Autowired
	private ExternalLinkConverter _externalLinkConverter;

	private JiraAssetObject _toPostalAddressAssetObject(
		PostalAddress postalAddress) {

		JiraAssetObject jiraAssetObject = _postalAddressConverter.toAssetObject(
			postalAddress);

		jiraAssetObject.setAttributeValue(
			PostalAddressConstants.ATTRIBUTE_NAME_MAILING,
			_getMailing(postalAddress));

		return jiraAssetObject;
	}

	private AccountUserAccountBucket getAccountUserAccountBucket(
			Account account)
		throws Exception {

		List<UserAccount> accountUserAccounts =
			_userAccountService.getAccountUserAccounts(account.getId());

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

			RoleBrief[] roleBriefs = accountBrief.getRoleBriefs();

			RoleBrief roleBrief = _findFirst(
				roleBriefs,
				roleBrief1 -> employeeRoleNames.contains(roleBrief1.getName()));

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

	private static final Log _log = LogFactory.getLog(
		AccountsRestController.class);

	@Autowired
	private AccountAssetService _accountAssetService;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountPermission _accountPermission;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private EntitlementConverter _entitlementConverter;

	@Autowired
	private EntitlementDefinitionService _entitlementDefinitionService;

	@Autowired
	private EntitlementService _entitlementService;

	@Autowired
	private PostalAddressConverter _postalAddressConverter;

	@Autowired
	private PropertyService _propertyService;

	@Autowired
	private SiteLanguageService _siteLanguageService;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private UserAccountService _userAccountService;

	private final List<String> employeeRoleNames = EmployeeRoles.getNames();

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

	private enum EmployeeRoles {

		CUSTOMER_EXPERIENCE_MANAGER(
			"C_CUSTOMER_EXPERIENCE_MANAGER", "Customer Experience Manager",
			"account"),
		LIFERAY_SALES("C_LIFERAY_SALES", "Liferay Sales", "account"),
		PRIMARY_CONTACT("C_PRIMARY_CONTACT", "Primary Contact", "account"),
		SECONDARY_CONTACT(
			"C_SECONDARY_CONTACT", "Secondary Contact", "account"),
		SOLUTION_ARCHITECT(
			"C_SOLUTION_ARCHITECT", "Solution Architect", "account");

		public static List<String> getNames() {
			List<String> names = new ArrayList<>();

			for (EmployeeRoles employeeRole : values()) {
				names.add(employeeRole.getName());
			}

			return names;
		}

		@SuppressWarnings("unused")
		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		public String getName() {
			return _name;
		}

		@SuppressWarnings("unused")
		public String getRoleType() {
			return _roleType;
		}

		EmployeeRoles(
			String externalReferenceCode, String name, String roleType) {

			_externalReferenceCode = externalReferenceCode;
			_name = name;
			_roleType = roleType;
		}

		private final String _externalReferenceCode;
		private final String _name;
		private final String _roleType;

	}

}