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

import {
	createPortletURL,
	navigate,
	openSelectionModal,
	postForm,
} from 'frontend-js-web';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const activateAccountUsers = (itemData) => {
		_updateAccountUsers(itemData.activateAccountUsersURL);
	};

	const addAccountUser = (itemData) => {
		openSelectionModal({
			id: `${portletNamespace}addAccountUser`,
			onSelect: (selectedItem) => {
				var addAccountUserURL = createPortletURL(
					itemData.addAccountUserURL,
					{
						accountEntryId: selectedItem.accountentryid,
					}
				);

				navigate(addAccountUserURL);
			},
			selectEventName: `${portletNamespace}selectAccountEntry`,
			title: Liferay.Language.get(itemData.dialogTitle),
			url: itemData.accountEntrySelectorURL,
		});
	};

	const deactivateAccountUsers = (itemData) => {
		if (
			confirm(
				Liferay.Language.get(
					'are-you-sure-you-want-to-deactivate-the-selected-users'
				)
			)
		) {
			_updateAccountUsers(itemData.deactivateAccountUsersURL);
		}
	};

	const deleteAccountUsers = (itemData) => {
		if (
			confirm(
				Liferay.Language.get(
					'are-you-sure-you-want-to-delete-the-selected-users'
				)
			)
		) {
			_updateAccountUsers(itemData.deleteAccountUsersURL);
		}
	};

	const _openAccountEntrySelector = (
		dialogButtonLabel,
		dialogEventName,
		dialogTitle,
		accountEntrySelectorURL,
		callback
	) => {
		openSelectionModal({
			buttonAddLabel: dialogButtonLabel,
			multiple: true,
			onSelect: (selectedItem) => {
				if (selectedItem) {
					callback(selectedItem);
				}
			},
			selectEventName: dialogEventName,
			title: dialogTitle,
			url: accountEntrySelectorURL,
		});
	};

	const selectAccountEntries = (itemData) => {
		_openAccountEntrySelector(
			Liferay.Language.get('select'),
			`${portletNamespace}selectAccountEntries`,
			Liferay.Language.get(itemData.dialogTitle),
			itemData.accountEntriesSelectorURL,
			(selectedItems) => {
				var redirectURL = Liferay.Util.PortletURL.createPortletURL(
					itemData.redirectURL,
					{
						accountEntriesNavigation: 'accounts',
						accountEntryIds: selectedItems.value,
					}
				);

				window.location.href = redirectURL;
			}
		);
	};

	const _updateAccountUsers = (url) => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (!form) {
			return;
		}
		postForm(form, {
			data: {
				accountUserIds: Liferay.Util.listCheckedExcept(
					form,
					`${portletNamespace}allRowIds`
				),
			},
			url,
		});
	};

	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			const data = item.data;

			const action = data?.action;

			if (action === 'selectAccountEntries') {
				selectAccountEntries(data);
			}
			else if (action === 'deactivateAccountUsers') {
				deactivateAccountUsers(data);
			}
			else if (action === 'activateAccountUsers') {
				activateAccountUsers(data);
			}
			else if (action === 'deleteAccountUsers') {
				deleteAccountUsers(data);
			}
		},
		onCreateButtonClick: (event, {item}) => {
			const data = item.data;

			if (!data) {
				return;
			}

			addAccountUser(data);
		},
	};
}
