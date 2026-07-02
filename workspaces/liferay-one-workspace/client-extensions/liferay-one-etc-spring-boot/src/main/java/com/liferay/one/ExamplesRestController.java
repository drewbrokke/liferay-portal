/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.one.jira.constants.AccountConstants;
import com.liferay.one.jira.constants.AccountContactRoleAssignmentConstants;
import com.liferay.one.jira.constants.TeamConstants;
import com.liferay.one.jira.converter.AccountContactRoleAssignmentConverter;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.ContactRoleConverter;
import com.liferay.one.jira.converter.PostalAddressConverter;
import com.liferay.one.jira.converter.TeamConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.one.jira.service.AccountAssetService;
import com.liferay.one.jira.service.AssetReferenceResolverService;
import com.liferay.one.jira.service.JiraAssetService;
import com.liferay.one.service.AccountService;
import com.liferay.portal.kernel.util.ListUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Executable documentation for the JSM reference-attribute patterns. Hard
 * reference attributes (Account, Contacts, Postal Addresses, ...) store the
 * referenced asset object's JSM id, never an external key, so every hard
 * reference needs a resolution step through {@link
 * AssetReferenceResolverService} before it can be set. Soft references (the
 * "* External Key" text attributes) are plain strings and come straight from
 * the converters.
 *
 * @author Drew Brokke
 */
@RequestMapping("/examples")
@RestController
public class ExamplesRestController extends OneBaseRestController {

	/**
	 * An assignment needs both reference flavors at once. The converter
	 * fills the soft side from the external key tuple: the composed "Name"
	 * (the type's dedup key) and the three "* External Key" text attributes.
	 * The three hard references are required (minimum cardinality 1), and
	 * they resolve WITHOUT create: an assignment pointing at a missing
	 * account, contact, or contact role means those entities have not been
	 * synced yet, and creating stubs for them here would insert invalid
	 * objects.
	 */
	@PostMapping("/account-contact-role-assignments/sync-to-jsm")
	public ResponseEntity<Void> postAccountContactRoleAssignmentsSyncToJSM(
		@RequestParam("accountExternalReferenceCode") String
			accountExternalReferenceCode,
		@RequestParam("contactExternalReferenceCode") String
			contactExternalReferenceCode,
		@RequestParam("contactRoleExternalKey") String contactRoleExternalKey) {

		JiraAssetObject jiraAssetObject =
			_accountContactRoleAssignmentConverter.toAssetObject(
				contactRoleExternalKey, contactExternalReferenceCode,
				accountExternalReferenceCode, false);

		String accountObjectId =
			_assetReferenceResolverService.resolveObjectIdOrThrow(
				_accountConverter, accountExternalReferenceCode);
		String contactObjectId =
			_assetReferenceResolverService.resolveObjectIdOrThrow(
				_contactConverter, contactExternalReferenceCode);
		String contactRoleObjectId =
			_assetReferenceResolverService.resolveObjectIdOrThrow(
				_contactRoleConverter, contactRoleExternalKey);

		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
			accountObjectId);
		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT,
			contactObjectId);
		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT_ROLE,
			contactRoleObjectId);

		_jiraAssetService.createObject(
			_accountContactRoleAssignmentConverter.getObjectTypeId(),
			jiraAssetObject);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * A multivalue hard reference resolved WITH create-if-missing. "Postal
	 * Addresses" holds one JSM object id per referenced Postal Address
	 * object. Creating missing ones is correct here because the postal
	 * addresses belong to the account being synced — there is no other flow
	 * that creates them first.
	 */
	@PostMapping("/postal-addresses/sync-to-jsm/{accountExternalReferenceCode}")
	public ResponseEntity<Void> postPostalAddressesSyncToJSM(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("accountExternalReferenceCode") String
				accountExternalReferenceCode)
		throws Exception {

		Account account = _accountService.getAccount(
			accountExternalReferenceCode, jwt);

		JiraAssetObject jiraAssetObject = _accountConverter.toAssetObject(
			account);

		jiraAssetObject.setAttributeValue(
			AccountConstants.ATTRIBUTE_NAME_POSTAL_ADDRESSES,
			_assetReferenceResolverService.resolveOrCreateObjectIds(
				_postalAddressConverter,
				ListUtil.fromArray(account.getPostalAddresses()),
				postalAddress -> String.valueOf(postalAddress.getId()),
				_postalAddressConverter::toAssetObject));

		_accountAssetService.upsertJSMAccount(
			accountExternalReferenceCode, jiraAssetObject);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * A single-value hard reference resolved WITHOUT create. A Team's
	 * "Account" must point at an Account asset object that already exists
	 * (accounts sync before teams); creating a stub account as a side effect
	 * of a team sync would insert garbage data. A real sync would fetch the
	 * Organization from Liferay — it is built inline here to keep the
	 * example self-contained.
	 */
	@PostMapping("/teams/sync-to-jsm/{organizationExternalReferenceCode}")
	public ResponseEntity<Void> postTeamsSyncToJSM(
		@PathVariable("organizationExternalReferenceCode") String
			organizationExternalReferenceCode,
		@RequestParam("accountExternalReferenceCode") String
			accountExternalReferenceCode,
		@RequestParam("name") String name) {

		Organization organization = new Organization();

		organization.setExternalReferenceCode(
			() -> organizationExternalReferenceCode);
		organization.setName(() -> name);

		JiraAssetObject jiraAssetObject = _teamConverter.toAssetObject(
			organization);

		jiraAssetObject.setAttributeValue(
			TeamConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceResolverService.resolveObjectIdOrThrow(
				_accountConverter, accountExternalReferenceCode));

		_jiraAssetService.createObject(
			_teamConverter.getObjectTypeId(), jiraAssetObject);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Autowired
	private AccountAssetService _accountAssetService;

	@Autowired
	private AccountContactRoleAssignmentConverter
		_accountContactRoleAssignmentConverter;

	@Autowired
	private AccountConverter _accountConverter;

	@Autowired
	private AccountService _accountService;

	@Autowired
	private AssetReferenceResolverService _assetReferenceResolverService;

	@Autowired
	private ContactConverter _contactConverter;

	@Autowired
	private ContactRoleConverter _contactRoleConverter;

	@Autowired
	private JiraAssetService _jiraAssetService;

	@Autowired
	private PostalAddressConverter _postalAddressConverter;

	@Autowired
	private TeamConverter _teamConverter;

}