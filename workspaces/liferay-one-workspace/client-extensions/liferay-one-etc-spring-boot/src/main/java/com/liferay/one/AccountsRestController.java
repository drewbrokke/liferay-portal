/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.jira.constants.PostalAddressConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.EntitlementConverter;
import com.liferay.one.jira.converter.PostalAddressConverter;
import com.liferay.one.jira.converter.ProductVersionConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AccountAssetService;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.model.Entitlement;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.permission.BusinessEventPermission;
import com.liferay.one.service.AccountService;
import com.liferay.one.service.EntitlementDefinitionService;
import com.liferay.one.service.EntitlementService;
import com.liferay.one.service.UserAccountService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

	@GetMapping("/{externalReferenceCode}/jira/object-key")
	public ResponseEntity<String> getJiraObjectKey(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_businessEventPermission.check(
			externalReferenceCode, ActionKeys.VIEW, jwt);

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

	private Boolean _getAIPublicComments(Account account) {
		return null;
	}

	private Double _getARR(Account account) {
		return null;
	}

	private List<Organization> _getAssignedTeamOrganizations(Account account) {
		return null;
	}

	private String _getBusinessEvents(Account account) {
		return null;
	}

	private String _getDataRegion(Account account) {
		return null;
	}

	private String _getDXPVersionConfirmedDate(Account account) {
		return null;
	}

	private String _getDXPVersionName(Account account) {
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

	private String _getExpired(Account account) {
		return null;
	}

	private List<String> _getExternalLinkObjectIds(Account account) {
		return null;
	}

	private String _getGSOpportunity(Account account) {
		return null;
	}

	private Boolean _getInternal(Account account) {
		return null;
	}

	private String _getLanguage(Account account) {
		return null;
	}

	private Boolean _getMailing(PostalAddress postalAddress) {
		return null;
	}

	private Integer _getOrganization(Account account) {
		return null;
	}

	private String _getPremiumService(Account account) {
		return null;
	}

	private List<String> _getPreviousCustomerContactObjectIds(Account account) {
		return null;
	}

	private String _getProfileEmailAddress(Account account) {
		return null;
	}

	private String _getSuborganization(Account account) {
		return null;
	}

	private Boolean _getSuborganizationImported(Account account) {
		return null;
	}

	private String _getSupportRegion(Account account) {
		return null;
	}

	private List<UserAccount> _getWorkerUserAccounts(Account account) {
		return null;
	}

	private void _syncAccount(Account account) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing account " + account.getExternalReferenceCode() +
					" to JSM");
		}

		JiraAssetObject assetObject = _accountConverter.toAssetObject(account);

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_AI_PUBLIC_COMMENTS,
			_getAIPublicComments(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ARR, _getARR(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_BUSINESS_EVENTS,
			_getBusinessEvents(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_DATA_REGION,
			_getDataRegion(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_DXP_VERSION_CONFIRMED_DATE,
			_getDXPVersionConfirmedDate(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_EXPIRED, _getExpired(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_GS_OPPORTUNITY,
			_getGSOpportunity(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_INTERNAL, _getInternal(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_LANGUAGE, _getLanguage(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ORGANIZATION,
			_getOrganization(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_PREMIUM_SERVICE,
			_getPremiumService(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_PROFILE_EMAIL_ADDRESS,
			_getProfileEmailAddress(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_SUBORGANIZATION,
			_getSuborganization(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_SUBORGANIZATION_IMPORTED,
			_getSuborganizationImported(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_SUPPORT_REGION,
			_getSupportRegion(account));

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_DXP_VERSION,
			_assetReferenceResolverService.resolveObjectId(
				_productVersionConverter, _getDXPVersionName(account)));

		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ASSIGNED_TEAMS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_teamConverter, _getAssignedTeamOrganizations(account),
				Organization::getExternalReferenceCode,
				_teamConverter::toAssetObject));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_CUSTOMER_CONTACTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_contactConverter,
				_userAccountService.getAccountUserAccounts(account.getId()),
				UserAccount::getExternalReferenceCode,
				_contactConverter::toAssetObject));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_CUSTOMER_CONTACTS_PREVIOUS,
			_getPreviousCustomerContactObjectIds(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_ENTITLEMENTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_entitlementConverter, _getEntitlementDefinitions(account),
				EntitlementDefinition::getName,
				_entitlementConverter::toAssetObject));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_getExternalLinkObjectIds(account));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_POSTAL_ADDRESSES,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_postalAddressConverter,
				ListUtil.fromArray(account.getPostalAddresses()),
				postalAddress -> String.valueOf(postalAddress.getId()),
				this::_toPostalAddressAssetObject));
		assetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_WORKER_CONTACTS,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_contactConverter, _getWorkerUserAccounts(account),
				UserAccount::getExternalReferenceCode,
				_contactConverter::toAssetObject));

		_accountAssetService.upsertJSMAccount(
			account.getExternalReferenceCode(), assetObject);
	}

	private JiraAssetObject _toPostalAddressAssetObject(
		PostalAddress postalAddress) {

		JiraAssetObject jiraAssetObject = _postalAddressConverter.toAssetObject(
			postalAddress);

		jiraAssetObject.setAttributeValue(
			PostalAddressConstants.ATTRIBUTE_NAME_MAILING,
			_getMailing(postalAddress));

		return jiraAssetObject;
	}

	private static final Log _log = LogFactory.getLog(
		AccountsRestController.class);

	@Autowired
	private AccountAssetService _accountAssetService;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private BusinessEventPermission _businessEventPermission;

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
	private ProductVersionConverter _productVersionConverter;

	@Autowired
	private TeamConverter _teamConverter;

	@Autowired
	private UserAccountService _userAccountService;

}