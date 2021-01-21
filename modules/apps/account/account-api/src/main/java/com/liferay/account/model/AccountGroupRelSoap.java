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

package com.liferay.account.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.account.service.http.AccountGroupRelServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 * @generated
 */
@Deprecated
public class AccountGroupRelSoap implements Serializable {

	public static AccountGroupRelSoap toSoapModel(AccountGroupRel model) {
		AccountGroupRelSoap soapModel = new AccountGroupRelSoap();

		soapModel.setMvccVersion(model.getMvccVersion());
		soapModel.setAccountGroupRelId(model.getAccountGroupRelId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setAccountGroupId(model.getAccountGroupId());
		soapModel.setAccountEntryId(model.getAccountEntryId());

		return soapModel;
	}

	public static AccountGroupRelSoap[] toSoapModels(AccountGroupRel[] models) {
		AccountGroupRelSoap[] soapModels =
			new AccountGroupRelSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static AccountGroupRelSoap[][] toSoapModels(
		AccountGroupRel[][] models) {

		AccountGroupRelSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new AccountGroupRelSoap[models.length][models[0].length];
		}
		else {
			soapModels = new AccountGroupRelSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static AccountGroupRelSoap[] toSoapModels(
		List<AccountGroupRel> models) {

		List<AccountGroupRelSoap> soapModels =
			new ArrayList<AccountGroupRelSoap>(models.size());

		for (AccountGroupRel model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new AccountGroupRelSoap[soapModels.size()]);
	}

	public AccountGroupRelSoap() {
	}

	public long getPrimaryKey() {
		return _AccountGroupRelId;
	}

	public void setPrimaryKey(long pk) {
		setAccountGroupRelId(pk);
	}

	public long getMvccVersion() {
		return _mvccVersion;
	}

	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	public long getAccountGroupRelId() {
		return _AccountGroupRelId;
	}

	public void setAccountGroupRelId(long AccountGroupRelId) {
		_AccountGroupRelId = AccountGroupRelId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getAccountGroupId() {
		return _accountGroupId;
	}

	public void setAccountGroupId(long accountGroupId) {
		_accountGroupId = accountGroupId;
	}

	public long getAccountEntryId() {
		return _accountEntryId;
	}

	public void setAccountEntryId(long accountEntryId) {
		_accountEntryId = accountEntryId;
	}

	private long _mvccVersion;
	private long _AccountGroupRelId;
	private long _companyId;
	private long _accountGroupId;
	private long _accountEntryId;

}