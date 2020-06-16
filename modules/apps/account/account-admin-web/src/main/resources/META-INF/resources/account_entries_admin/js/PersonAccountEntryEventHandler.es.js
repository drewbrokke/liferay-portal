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

import {PortletBase} from 'frontend-js-web';
import * as dom from 'metal-dom';
import {Config} from 'metal-state';
import {EventHandler} from 'metal-events';

class PersonAccountEntryEventHandler extends PortletBase {

	/**
	 * @inheritDoc
	 */
	created() {
		this.eventHandler_ = new EventHandler();
	}

	/**
	 * @inheritDoc
	 */
	attached() {
		this.eventHandler_.add(dom.delegate(
			this.container,
			'click',
			this.removeButtonSelector,
			this._handleRemoveButtonClicked.bind(this)
		));
	}

	/**
	 * @inheritDoc
	 */
	detached() {
		super.detached();
		this.eventHandler_.removeAllListeners();
	}

	_handleRemoveButtonClicked(event) {
		debugger;
	}

	_querySelector(selector) {
		return this.one(selector);
	}
}

PersonAccountEntryEventHandler.STATE = {
	container: Config.string().setter('_querySelector'),
	removeButtonSelector: Config.string(),
	userIdInput: Config.string().setter('_querySelector'),
};

export default PersonAccountEntryEventHandler;
