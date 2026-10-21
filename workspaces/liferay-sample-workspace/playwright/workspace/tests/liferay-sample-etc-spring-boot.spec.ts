/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {
	ObjectDefinition,
	addObjectAction,
	addObjectDefinition,
	deleteObjectAction,
	deleteObjectDefinition,
	getObjectAction,
} from '../../helpers/objects';

const FUNCTION_PREFIX = 'function#liferay-sample-etc-spring-boot';

test('Object action 1 runs successfully after an entry is added', async ({
	api,
}) => {
	let objectDefinition: ObjectDefinition | undefined;

	try {
		objectDefinition = await addObjectDefinition(api, {
			name: 'SpringBootObjectAction',
		});

		const objectAction = await addObjectAction(
			api,
			objectDefinition.externalReferenceCode,
			{
				objectActionExecutorKey: `${FUNCTION_PREFIX}-object-action-1`,
				objectActionTriggerKey: 'onAfterAdd',
			}
		);

		await api.post(objectDefinition.restContextPath, {name: 'Sample'});

		await expect
			.poll(
				async () =>
					(await getObjectAction(api, objectAction.id)).status?.label,
				{timeout: 30 * 1000}
			)
			.toBe('success');
	}
	finally {
		await deleteObjectDefinition(api, objectDefinition);
	}
});

test('Object action 2 calls back into Liferay to update a new user', async ({
	api,
}) => {
	const objectAction = await addObjectAction(api, 'L_USER', {
		objectActionExecutorKey: `${FUNCTION_PREFIX}-object-action-2`,
		objectActionTriggerKey: 'onAfterAdd',
	});

	let userAccountId: number | undefined;

	try {
		const givenName = `given${Date.now()}`;

		const userAccount = await api.post(
			'/o/headless-admin-user/v1.0/user-accounts',
			{
				alternateName: `alternate${Date.now()}`,
				emailAddress: `${givenName}@liferay.com`,
				familyName: 'Sample',
				givenName,
			}
		);

		userAccountId = userAccount.id;

		await expect
			.poll(
				async () =>
					(
						await api.get(
							`/o/headless-admin-user/v1.0/user-accounts/${userAccountId}`
						)
					).alternateName,
				{timeout: 30 * 1000}
			)
			.toBe(givenName.toLowerCase());
	}
	finally {
		await deleteObjectAction(api, objectAction.id);

		if (userAccountId) {
			await api.delete(
				`/o/headless-admin-user/v1.0/user-accounts/${userAccountId}`
			);
		}
	}
});

test('Object entry manager 1 stores, updates, and deletes entries', async ({
	api,
}) => {
	let objectDefinition: ObjectDefinition | undefined;

	try {
		objectDefinition = await addObjectDefinition(api, {
			name: 'SpringBootEntryManager',
			storageType: `${FUNCTION_PREFIX}-object-entry-manager-1`,
		});

		const {restContextPath} = objectDefinition;

		const objectEntry = await api.post(restContextPath, {name: 'Created'});

		const objectEntryPath = `${restContextPath}/by-external-reference-code/${objectEntry.externalReferenceCode}`;

		expect((await api.get(objectEntryPath)).name).toBe('Created');

		await api.put(objectEntryPath, {name: 'Updated'});

		expect((await api.get(objectEntryPath)).name).toBe('Updated');

		await api.delete(objectEntryPath);

		const response = await api.requestContext.get(objectEntryPath);

		expect(response.ok()).toBe(false);
	}
	finally {
		await deleteObjectDefinition(api, objectDefinition);
	}
});

test('Object validation rule 1 rejects entries named "Invalid Name"', async ({
	api,
}) => {
	let objectDefinition: ObjectDefinition | undefined;

	try {

		// The validation rule only applies to C_Employee

		objectDefinition = await addObjectDefinition(api, {name: 'Employee'});

		await api.post(
			`/o/object-admin/v1.0/object-definitions/${objectDefinition.id}/object-validation-rules`,
			{
				active: true,
				engine: `${FUNCTION_PREFIX}-object-validation-rule-1`,
				errorLabel: {en_US: 'This entry is not possible.'},
				name: {en_US: 'Sample Validation'},
				script: '',
			}
		);

		const invalidResponse = await api.requestContext.post(
			objectDefinition.restContextPath,
			{data: {name: 'Invalid Name'}}
		);

		expect(invalidResponse.status()).toBe(400);
		expect(await invalidResponse.text()).toContain(
			'This entry is not possible.'
		);

		const validObjectEntry = await api.post(
			objectDefinition.restContextPath,
			{name: 'Valid Name'}
		);

		expect(validObjectEntry.name).toBe('Valid Name');
	}
	finally {
		await deleteObjectDefinition(api, objectDefinition);
	}
});
