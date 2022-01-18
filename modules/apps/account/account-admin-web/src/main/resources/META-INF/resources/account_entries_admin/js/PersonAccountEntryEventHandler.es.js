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

import {Config} from 'metal-state';

import {default as BaseSelectEntityEventHandler} from '../../common/js/BaseSelectEntityEventHandler.es';

class PersonAccountEntryEventHandler extends BaseSelectEntityEventHandler {
	handleOnSelect(selectedItemData) {
		this._setSearchContainerUser(selectedItemData);
	}

	handleRemoveButtonClicked() {
		this.searchContainer.deleteRow(1, this.searchContainer.getData());

		this._setUserIdInputValue(null);
	}

	_setSearchContainerUser({
		emailaddress: emailAddress,
		entityid: userId,
		entityname: userName,
		jobtitle: jobTitle,
	}) {
		this._setUserIdInputValue(userId);

		this.searchContainer.deleteRow(1, this.searchContainer.getData());
		this.searchContainer.addRow(
			[userName, emailAddress, jobTitle, this.removeUserIconMarkup],
			userId
		);
		this.searchContainer.updateDataStore([userId]);
	}

	_setUserIdInputValue(userId) {
		this.userIdInput.value = userId;

		if (userId) {
			this.selectedData = [userId];
		}
		else {
			this.selectedData = [];
		}
	}
}

PersonAccountEntryEventHandler.STATE = {
	removeUserIconMarkup: Config.string(),
	userIdInput: Config.string().setter('_setElement'),
};

export default PersonAccountEntryEventHandler;
