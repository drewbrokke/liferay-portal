/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {
	ObjectDefinition,
	addObjectAction,
	addObjectDefinition,
	deleteObjectDefinition,
	getObjectAction,
} from '../../helpers/objects';

for (const objectActionNumber of [1, 2]) {
	test(`Object action ${objectActionNumber} runs successfully after an entry is added`, async ({
		api,
	}) => {
		let objectDefinition: ObjectDefinition | undefined;

		try {
			objectDefinition = await addObjectDefinition(api, {
				name: `NodeObjectAction${objectActionNumber}`,
			});

			const objectAction = await addObjectAction(
				api,
				objectDefinition.externalReferenceCode,
				{
					objectActionExecutorKey: `function#liferay-sample-etc-node-object-action-${objectActionNumber}`,
					objectActionTriggerKey: 'onAfterAdd',
				}
			);

			await api.post(objectDefinition.restContextPath, {name: 'Sample'});

			await expect
				.poll(
					async () =>
						(await getObjectAction(api, objectAction.id)).status
							?.label,
					{timeout: 30 * 1000}
				)
				.toBe('success');
		}
		finally {
			await deleteObjectDefinition(api, objectDefinition);
		}
	});
}
