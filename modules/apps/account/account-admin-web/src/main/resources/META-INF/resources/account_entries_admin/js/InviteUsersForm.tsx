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

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {
	fetch,
	getOpener,
	openToast,
} from 'frontend-js-web';
import React, {useState, MouseEventHandler} from 'react';
import {InputGroup, MultiSelectItem, ValidatableMultiSelectItem} from "./types";
import InviteUserFormGroup from "./InviteUsersFormGroup";

interface IProps {
	accountEntryId: number;
	availableAccountRoles: MultiSelectItem[];
	inviteAccountUsersURL: string;
	portletNamespace: string;
	redirectURL: string
}

interface IState {
	inputGroups: InputGroup[];
}

const initialState: IState = {
	inputGroups: [
		{
			accountRoles: [],
			emailAddresses: [],
			id: 'inputGroup-1'
		}
	]
}

function InviteUsersForm({
	accountEntryId,
	availableAccountRoles,
	inviteAccountUsersURL,
	portletNamespace,
	redirectURL,
}: IProps) {
	const [inputGroups, setInputGroups] = useState([
		{selectedAccountRoles: [], selectedEmailAddresses: []},
	]);

	const [state, setState] = useState<IState>(initialState);

	const closeModal = () => {
		const openerWindow = getOpener();

		openerWindow.Liferay.fire('closeModal');
	};

	const submitForm: MouseEventHandler<HTMLButtonElement> = (event) => {
		event.preventDefault();

		const form = document.querySelector(
			`#${portletNamespace}inviteUserForm`
		) as HTMLFormElement;

		const error = form?.querySelector('.has-error');

		if (!error && form) {
			const formData = new FormData(form);

			formData.append(`${portletNamespace}count`, String(state.inputGroups.length));

			fetch(inviteAccountUsersURL, {
				body: formData,
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({success}) => {
					if (success) {
						getOpener().Liferay.fire('closeModal', {
							id: `${portletNamespace}inviteUsersDialog`,
							redirect: redirectURL,
						});
					}
					else {
						throw new Error();
					}
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'your-request-failed-to-complete'
						),
						title: Liferay.Language.get('error'),
						type: 'danger',
					});
				});
		}
	};

	const setEmailAddresses = async (inputGroupId: string, emailAddresses: MultiSelectItem[]) => {
		const inputGroup = state.inputGroups.find(
			inputGroup => inputGroup.id === inputGroupId);

		if (inputGroup) {
			const promises = emailAddresses.map(
				emailAddress => new Promise<ValidatableMultiSelectItem>( async (resolve, reject) => {
					const response = await fetch(`/o/account-admin/validate-email-address/`, {
						body: Liferay.Util.objectToFormData({
							accountEntryId,
							emailAddress: emailAddress.label,
						}),
						method: 'POST',
					});

					const {errorMessage, _isValid} = await response.json();

					const validatedEmailAddress: ValidatableMultiSelectItem = {
						...emailAddress
					};

					if (errorMessage) {
						validatedEmailAddress.errorMessage = errorMessage
					}

					resolve(validatedEmailAddress);
				})
			);

			inputGroup.emailAddresses = await Promise.all(promises);

			setState(state);
		}
	};

	const setAccountRoles = (inputGroupId: string, accountRoles: MultiSelectItem[]) => {
		const inputGroup = state.inputGroups.find(
			inputGroup => inputGroup.id === inputGroupId);

		if (inputGroup) {
			inputGroup.accountRoles = accountRoles.map(
				accountRole => ({
					label: accountRole.label,
					value: accountRole.value,
					isValid: availableAccountRoles.some(
						availableAccountRole =>
							availableAccountRole.label === accountRole.label
					),
				}));

			setState(state);
		}
	}

	return (
		<ClayForm
			className="lfr-form-content"
			id={`${portletNamespace}inviteUserForm`}
		>
			{state.inputGroups.map((inputGroup, index) => (
				<InviteUserFormGroup
					index={index}
					key={inputGroup.id}
					id={inputGroup.id}
					portletNamespace={portletNamespace}
					accountEntryId={accountEntryId}
					availableAccountRoles={availableAccountRoles}
					accountRoles={inputGroup.accountRoles}
					onEmailAddressItemsChange={items => setEmailAddresses(inputGroup.id, items)}
					onAccountRoleItemsChange={items => setAccountRoles(inputGroup.id, items)}
					emailAddresses={inputGroup.emailAddresses}
				/>
			))}

			<ClayLayout.SheetFooter>
				<ClayButton
					displayType="secondary"
					onClick={() => {
						state.inputGroups.push(
							{
								accountRoles: [], 
								emailAddresses: [], 
								id: `input-${state.inputGroups.length}`
							}
						);
						
						setState(state);
					}}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="plus" />
					</span>

					{Liferay.Language.get('add-entry')}
				</ClayButton>
			</ClayLayout.SheetFooter>

			<ClayLayout.SheetFooter className="dialog-footer">
				<ClayButton displayType="primary" onClick={submitForm}>
					{Liferay.Language.get('invite')}
				</ClayButton>

				<ClayButton displayType="secondary" onClick={closeModal}>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayLayout.SheetFooter>
		</ClayForm>
	);
}



export default InviteUsersForm;
