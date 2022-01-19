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

import {default as BaseSelectEntityEventHandler} from '../../../../../common/js/BaseSelectEntityEventHandler.es';

class UsersAdminSelectAccountEventHandler extends BaseSelectEntityEventHandler {
	handleOnSelect(selectedItems) {
		this._updateSearchContainer(selectedItems);
	}

	handleRemoveButtonClicked(event) {
		this._removeAccountEntryFromSearchContainer(event);
	}

	_removeAccountEntryFromSearchContainer(event) {
		var link = event.target.closest('a.remove-link');

		var rowId = link.dataset.rowid;

		this.searchContainer.deleteRow(link.closest('tr'), rowId);

		this.addAccountEntryIds = this.addAccountEntryIds.filter((id) => id === rowId);

		this.deleteAccountEntryIds.push(rowId);

		this._updateFieldValues();
	}

	_updateFieldValues() {
		this.addAccountEntryIdsInput.value = this.addAccountEntryIds.join(
			','
		);
		this.deleteAccountEntryIdsInput.value = this.deleteAccountEntryIds.join(
			','
		);

		this.selectedData = this.addAccountEntryIds.map(id => id.toString());
		
	}

	_updateSearchContainer(selectedItems) {
		for (const selectedItem of selectedItems) {
			var entityId = selectedItem.entityid;

			var rowColumns = [];

			rowColumns.push(selectedItem.entityname);
			rowColumns.push(selectedItem.type);
			rowColumns.push(`<a class="remove-link" data-rowId="${entityId}" href="javascript:;">${this.removeUserIconMarkup}</a>`);

			this.searchContainer.addRow(rowColumns, entityId);

			this.addAccountEntryIds.push(entityId);
		}

		this.searchContainer.updateDataStore();

		this.deleteAccountEntryIds = this.deleteAccountEntryIds.filter(
			(id) => id === rowId
		);

		this._updateFieldValues();
	}

}

UsersAdminSelectAccountEventHandler.STATE = {
	addAccountEntryIds: Config.array().value([]).internal(),
	deleteAccountEntryIds: Config.array().value([]).internal(),
	removeUserIconMarkup: Config.string(),
	addAccountEntryIdsInput: Config.string().setter('_setElement'),
	deleteAccountEntryIdsInput: Config.string().setter('_setElement'),
};

export default UsersAdminSelectAccountEventHandler;
