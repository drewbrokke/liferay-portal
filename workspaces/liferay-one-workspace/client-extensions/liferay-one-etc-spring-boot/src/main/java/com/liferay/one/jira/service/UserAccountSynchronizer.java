/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountContactInformation;
import com.liferay.one.jira.constants.ContactConstants;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.ContactRoleConverter;
import com.liferay.one.jira.converter.EntitlementConverter;
import com.liferay.one.jira.converter.ExternalLinkConverter;
import com.liferay.one.jira.converter.PhoneConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.model.EntitlementDefinition;
import com.liferay.one.model.Property;
import com.liferay.one.service.EntitlementService;
import com.liferay.one.service.PropertyService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class UserAccountSynchronizer {

	public void syncUserAccount(UserAccount userAccount) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Syncing user account " +
					userAccount.getExternalReferenceCode() + " to JSM");
		}

		List<AccountBrief> accountBriefs = ListUtil.fromArray(
			userAccount.getAccountBriefs());

		JiraAssetObject jiraAssetObject = _contactConverter.toAssetObject(
			userAccount);

		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_accountConverter, accountBriefs,
				AccountBrief::getExternalReferenceCode));
		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_CONTACT_ROLES,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_contactRoleConverter, _getRoleBriefs(accountBriefs),
				RoleBrief::getExternalReferenceCode));
		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_ENTITLEMENTS,
			_assetReferenceObjectService.getOrCreateReferenceObjectIds(
				_entitlementConverter,
				_getEntitlementDefinitions(accountBriefs),
				EntitlementDefinition::getDisplayName,
				_entitlementConverter::toAssetObject));
		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_EXTERNAL_LINKS,
			_assetReferenceObjectService.getOrCreateReferenceObjectIds(
				_externalLinkConverter, _getExternalLinkProperties(userAccount),
				Property::getExternalReferenceCode,
				_externalLinkConverter::toAssetObject));
		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_TEAMS,
			_assetReferenceObjectService.fetchReferenceObjectIds(
				_teamConverter,
				ListUtil.fromArray(userAccount.getOrganizationBriefs()),
				OrganizationBrief::getExternalReferenceCode));
		jiraAssetObject.setAttributeValue(
			ContactConstants.ATTRIBUTE_NAME_PHONES,
			_assetReferenceObjectService.getOrCreateReferenceObjectIds(
				_phoneConverter, _getTelephones(userAccount),
				Phone::getPhoneNumber, _phoneConverter::toAssetObject));

		_assetObjectUpsertService.upsert(_contactConverter, jiraAssetObject);
	}

	private List<EntitlementDefinition> _getEntitlementDefinitions(
			List<AccountBrief> accountBriefs)
		throws Exception {

		List<EntitlementDefinition> entitlementDefinitions = new ArrayList<>();

		for (AccountBrief accountBrief : accountBriefs) {
			entitlementDefinitions.addAll(
				_entitlementService.getActiveEntitlementDefinitions(
					accountBrief.getId()));
		}

		return entitlementDefinitions;
	}

	private List<Property> _getExternalLinkProperties(UserAccount userAccount)
		throws Exception {

		List<Property> externalLinkProperties = new ArrayList<>();

		List<Property> properties = _propertyService.getUserAccountProperties(
			userAccount.getId());

		for (Property property : properties) {
			if (_externalLinkConverter.isExternalLinkProperty(property)) {
				externalLinkProperties.add(property);
			}
		}

		return externalLinkProperties;
	}

	private List<RoleBrief> _getRoleBriefs(List<AccountBrief> accountBriefs) {
		List<RoleBrief> roleBriefs = new ArrayList<>();

		for (AccountBrief accountBrief : accountBriefs) {
			RoleBrief[] accountRoleBriefs = accountBrief.getRoleBriefs();

			if (accountRoleBriefs != null) {
				Collections.addAll(roleBriefs, accountRoleBriefs);
			}
		}

		return roleBriefs;
	}

	private List<Phone> _getTelephones(UserAccount userAccount) {
		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			return Collections.emptyList();
		}

		return ListUtil.fromArray(
			userAccountContactInformation.getTelephones());
	}

	private static final Log _log = LogFactory.getLog(
		UserAccountSynchronizer.class);

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AssetObjectUpsertService _assetObjectUpsertService;

	@Autowired
	private AssetReferenceObjectService _assetReferenceObjectService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private ContactRoleConverter _contactRoleConverter;

	@Autowired
	private EntitlementConverter _entitlementConverter;

	@Autowired
	private EntitlementService _entitlementService;

	@Autowired
	private ExternalLinkConverter _externalLinkConverter;

	@Autowired
	private PhoneConverter _phoneConverter;

	@Autowired
	private PropertyService _propertyService;

	@Autowired
	private TeamConverter _teamConverter;

}