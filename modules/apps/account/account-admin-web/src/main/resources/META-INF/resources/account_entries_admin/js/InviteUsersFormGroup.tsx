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

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayMultiSelect from '@clayui/multi-select';
import {
    sub,
} from 'frontend-js-web';
import React from 'react';
import {InputGroup, MultiSelectItem} from "./types";

interface IProps extends InputGroup {
    index: number;
    accountEntryId: number;
    availableAccountRoles: MultiSelectItem[];
    onAccountRoleItemsChange: (items: MultiSelectItem[]) => void;
    onEmailAddressItemsChange: (items: MultiSelectItem[]) => void;
    portletNamespace: string;
}

const InviteUserFormGroup = ({
    availableAccountRoles,
    index, 
    onAccountRoleItemsChange,
    onEmailAddressItemsChange,
    accountRoles,
    emailAddresses,
    portletNamespace,
}: IProps) => {
    const invalidAccountRoles = accountRoles.filter(
        accountRole => accountRole.errorMessage != null);
    const invalidEmailAddresses = emailAddresses.filter(
        emailAddress => emailAddress.errorMessage != null);

    const showRequiredMessage = !emailAddresses.length || invalidAccountRoles.length;

    return (
        <ClayLayout.Sheet size="lg">
            <ClayForm.Group
                className={
                    !!invalidEmailAddresses.length || showRequiredMessage
                        ? 'has-error'
                        : ''
                }
            >
                <label
                    htmlFor={`${portletNamespace}emailAddressesMultiSelect${index}`}
                >
                    {Liferay.Language.get('emails')}

                    <ClayIcon
                        className="ml-1 reference-mark"
                        symbol="asterisk"
                    />
                </label>

                <ClayInput.Group>
                    <ClayInput.GroupItem>
                        <ClayMultiSelect
                            autoFocus={true}
                            id={`${portletNamespace}emailAddressesMultiSelect${index}`}
                            inputName={`${portletNamespace}emailAddresses${index}`}
                            items={emailAddresses}
                            // @ts-ignore
                            onItemsChange={onEmailAddressItemsChange}
                        />

                        {showRequiredMessage && (
                            <ClayForm.FeedbackGroup>
                                <ClayForm.FeedbackItem>
                                    {Liferay.Language.get(
                                        'this-field-is-required'
                                    )}
                                </ClayForm.FeedbackItem>
                            </ClayForm.FeedbackGroup>
                        )}

                        {invalidEmailAddresses.length && (
                            <ClayForm.FeedbackGroup>
                                {invalidEmailAddresses.map(
                                    (invalidEmailAddress) => (
                                        <ClayForm.FeedbackItem
                                            key={
                                                invalidEmailAddress.label
                                            }
                                        >
                                            {invalidEmailAddress.errorMessage}
                                        </ClayForm.FeedbackItem>
                                    )
                                )}
                            </ClayForm.FeedbackGroup>
                        )}
                    </ClayInput.GroupItem>
                </ClayInput.Group>
            </ClayForm.Group>

            <ClayForm.Group
                className={invalidAccountRoles.length ? 'has-error' : ''}
            >
                <label
                    htmlFor={`${portletNamespace}accountRolesMultiSelect${index}`}
                >
                    {Liferay.Language.get('roles')}
                </label>

                <ClayInput.Group>
                    <ClayInput.GroupItem>
                        <ClayMultiSelect
                            id={`${portletNamespace}accountRolesMultiSelect${index}`}
                            inputName={`${portletNamespace}accountRoleIds${index}`}
                            items={accountRoles}
                            // @ts-ignore
                            onItemsChange={onAccountRoleItemsChange}
                            sourceItems={availableAccountRoles}
                        />

                        <ClayForm.FeedbackGroup>
                            <ClayForm.Text>
                                {Liferay.Language.get(
                                    'roles-will-be-applied-to-all-users-above'
                                )}
                            </ClayForm.Text>
                        </ClayForm.FeedbackGroup>

                        {invalidAccountRoles.length && (
                            <ClayForm.FeedbackGroup>
                                {invalidAccountRoles.map(
                                    (invalidAccountRole) => (
                                        <ClayForm.FeedbackItem
                                            key={invalidAccountRole.value}
                                        >
                                            {sub(
                                                Liferay.Language.get(
                                                    'x-is-not-a-valid-role'
                                                ),
                                                invalidAccountRole.label
                                            )}
                                        </ClayForm.FeedbackItem>
                                    )
                                )}
                            </ClayForm.FeedbackGroup>
                        )}
                    </ClayInput.GroupItem>
                </ClayInput.Group>
            </ClayForm.Group>
        </ClayLayout.Sheet>
    );
};

export default InviteUserFormGroup;