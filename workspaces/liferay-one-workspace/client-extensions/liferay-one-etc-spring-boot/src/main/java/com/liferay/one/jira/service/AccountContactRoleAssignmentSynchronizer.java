/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.constants.AccountContactRoleAssignmentConstants;
import com.liferay.one.jira.converter.AccountContactRoleAssignmentConverter;
import com.liferay.one.jira.converter.AccountConverter;
import com.liferay.one.jira.converter.ContactConverter;
import com.liferay.one.jira.converter.ContactRoleConverter;
import com.liferay.one.jira.model.JiraAssetObject;
import com.liferay.petra.string.StringBundler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Drew Brokke
 */
@Component
public class AccountContactRoleAssignmentSynchronizer {

	public void syncAssignContactRole(
			String contactRoleExternalKey, String contactExternalKey,
			String accountExternalKey)
		throws Exception {

		_syncAssignment(
			contactRoleExternalKey, contactExternalKey, accountExternalKey,
			false);
	}

	public void syncUnassignContactRole(
			String contactRoleExternalKey, String contactExternalKey,
			String accountExternalKey)
		throws Exception {

		_syncAssignment(
			contactRoleExternalKey, contactExternalKey, accountExternalKey,
			true);
	}

	private void _syncAssignment(
			String contactRoleExternalKey, String contactExternalKey,
			String accountExternalKey, boolean deleted)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					deleted ? "Unassigning" : "Assigning",
					" account contact role ", contactRoleExternalKey,
					" for contact ", contactExternalKey, " on account ",
					accountExternalKey));
		}

		JiraAssetObject jiraAssetObject =
			_accountContactRoleAssignmentConverter.toAssetObject(
				contactRoleExternalKey, contactExternalKey, accountExternalKey,
				deleted, new Date());

		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT_ROLE,
			_assetReferenceObjectService.getReferenceObjectId(
				_contactRoleConverter, contactRoleExternalKey));
		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_CONTACT,
			_assetReferenceObjectService.getReferenceObjectId(
				_contactConverter, contactExternalKey));
		jiraAssetObject.setAttributeValue(
			AccountContactRoleAssignmentConstants.ATTRIBUTE_NAME_ACCOUNT,
			_assetReferenceObjectService.getReferenceObjectId(
				_accountConverter, accountExternalKey));

		_assetObjectUpsertService.upsert(
			_accountContactRoleAssignmentConverter, jiraAssetObject);
	}

	private static final Log _log = LogFactory.getLog(
		AccountContactRoleAssignmentSynchronizer.class);

	@Autowired
	private AccountContactRoleAssignmentConverter
		_accountContactRoleAssignmentConverter;

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

}