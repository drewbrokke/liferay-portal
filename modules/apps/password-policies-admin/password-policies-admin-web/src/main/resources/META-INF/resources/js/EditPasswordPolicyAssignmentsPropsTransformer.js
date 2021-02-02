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

import {createRenderURL, openSelectionModal} from 'frontend-js-web';

import {ACTIONS} from './actions';

export default function propsTransformer({
	additionalProps,
	portletNamespace,
	...props
}) {
	return {
		...props,
		onActionButtonClick(event, {item}) {
			const action = item.data?.action;

			if (action) {
				event.preventDefault();
				ACTIONS[action](portletNamespace);
			}
		},
		onPlusButtonClick() {
			const {
				basePortletURL,
				mvcPath,
				p_p_state,
				passwordPolicyId,
				passwordPolicyName,
				tabs1,
				tabs2,
			} = additionalProps;

			const url = createRenderURL(basePortletURL, {
				mvcPath,
				p_p_auth: Liferay.authToken,
				p_p_state,
				passwordPolicyId,
				tabs1,
				tabs2,
			});

			openSelectionModal({
				multiple: true,
				onSelect(result) {
					if (result && result.item) {
						const form = document[`${portletNamespace}fm`];

						if (result.memberType == 'users') {
							const addUserIdsInput = form.querySelector(
								`#${portletNamespace}addUserIds`
							);

							if (addUserIdsInput) {
								addUserIdsInput.setAttribute(
									'value',
									result.item
								);
							}
						}
						else if (result.memberType == 'organizations') {
							const addOrganizationIdsInput = form.querySelector(
								`#${portletNamespace}addOrganizationIds`
							);

							if (addOrganizationIdsInput) {
								addOrganizationIdsInput.setAttribute(
									'value',
									result.item
								);
							}
						}

						submitForm(form);
					}
				},
				selectEventName: `${portletNamespace}selectMember`,
				title: Liferay.Util.sub(
					Liferay.Language.get('add-assignees-to-x'),
					passwordPolicyName
				),
				url: url.toString(),
			});
		},
	};
}
