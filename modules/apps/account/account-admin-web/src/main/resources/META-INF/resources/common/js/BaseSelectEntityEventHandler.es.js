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

import {PortletBase, delegate, openSelectionModal} from 'frontend-js-web';
import {Config} from 'metal-state';

class BaseSelectEntityEventHandler extends PortletBase {

	/**
	 * @inheritDoc
	 */
	attached() {
		this.selectButton.addEventListener(
			'click',
			this._handleSelectButtonClicked
		);

		this._removeUserButtonHandle = delegate(
			this.container,
			'click',
			this.removeButtonSelector,
			this.handleRemoveButtonClicked.bind(this)
		);
	}

	/**
	 * @inheritDoc
	 */
	created() {
		this._handleSelectButtonClicked = this._handleSelectButtonClicked.bind(
			this
		);
	}

	/**
	 * @inheritDoc
	 */
	detached() {
		super.detached();

		this.selectButton.removeEventListener(
			'click',
			this._handleSelectButtonClicked
		);

		this._removeUserButtonHandle.dispose();
	}

	handleOnSelect() {
		throw new Error('The handleOnSelect method is not implemented');
	}

	handleRemoveButtonClicked() {
		throw new Error(
			'The handleRemoveButtonClicked method is not implemented'
		);
	}

	_handleSelectButtonClicked() {
		this._openSelectionModal();
	}

	_openSelectionModal() {
		openSelectionModal({
			buttonAddLabel: this.modalButtonAddLabel,
			id: this.ns(this.selectEventName),
			multiple: this.selectMultiple,
			onSelect: this.handleOnSelect.bind(this),
			selectEventName: this.ns(this.selectEventName),
			selectedData: this.selectedData,
			title: this.modalTitle,
			url: this.selectURL,
		});
	}

	_setSearchContainer(searchContainerId) {
		return Liferay.SearchContainer.get(this.ns(searchContainerId));
	}

	_setElement(selector) {
		return this.one(selector);
	}
}

BaseSelectEntityEventHandler.STATE = {
	container: Config.string().setter('_setElement'),
	modalButtonAddLabel: Config.string().value(''),
	modalTitle: Config.string(),
	removeButtonSelector: Config.string(),
	searchContainer: Config.string().setter('_setSearchContainer'),
	selectButton: Config.string().setter('_setElement'),
	selectMultiple: Config.bool().value(false),
	selectEventName: Config.string(),
	selectURL: Config.string(),
	selectedData: Config.array(Config.string()).value([]),
};

export default BaseSelectEntityEventHandler;
