/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LiferayAPI} from './api';

const OBJECT_ADMIN_PATH = '/o/object-admin/v1.0';

export interface ObjectDefinition {
	externalReferenceCode: string;
	id: number;
	name: string;
	restContextPath: string;
}

/**
 * Creates and publishes a company scoped object definition with one required
 * text field called "name". Any leftover definition with the same name from an
 * earlier run is deleted first, since object names are unique.
 */
export async function addObjectDefinition(
	api: LiferayAPI,
	{name, storageType}: {name: string; storageType?: string}
): Promise<ObjectDefinition> {
	await deleteObjectDefinitionByName(api, name);

	const objectDefinition = await api.post<ObjectDefinition>(
		`${OBJECT_ADMIN_PATH}/object-definitions`,
		{
			active: true,
			label: {en_US: name},
			name,
			objectFields: [
				{
					businessType: 'Text',
					DBType: 'String',
					indexed: true,
					indexedAsKeyword: false,
					label: {en_US: 'Name'},
					name: 'name',
					required: true,
				},
			],
			panelCategoryKey: 'control_panel.object',
			pluralLabel: {en_US: `${name}s`},
			portlet: true,
			scope: 'company',
			...(storageType ? {storageType} : {}),
		}
	);

	return api.post<ObjectDefinition>(
		`${OBJECT_ADMIN_PATH}/object-definitions/${objectDefinition.id}/publish`,
		{}
	);
}

export async function addObjectAction(
	api: LiferayAPI,
	objectDefinitionExternalReferenceCode: string,
	{
		objectActionExecutorKey,
		objectActionTriggerKey,
	}: {objectActionExecutorKey: string; objectActionTriggerKey: string}
): Promise<{id: number}> {
	return api.post(
		`${OBJECT_ADMIN_PATH}/object-definitions/by-external-reference-code/${objectDefinitionExternalReferenceCode}/object-actions`,
		{
			active: true,
			label: {en_US: 'Sample Action'},
			name: `sampleAction${Date.now()}`,
			objectActionExecutorKey,
			objectActionTriggerKey,
			parameters: {},
		}
	);
}

export async function deleteObjectAction(
	api: LiferayAPI,
	objectActionId: number
) {
	await api.delete(`${OBJECT_ADMIN_PATH}/object-actions/${objectActionId}`);
}

export async function deleteObjectDefinition(
	api: LiferayAPI,
	objectDefinition: ObjectDefinition | undefined
) {
	if (objectDefinition) {
		await api.delete(
			`${OBJECT_ADMIN_PATH}/object-definitions/${objectDefinition.id}`
		);
	}
}

export async function getObjectAction(
	api: LiferayAPI,
	objectActionId: number
): Promise<{status?: {code: number; label: string}}> {
	return api.get(`${OBJECT_ADMIN_PATH}/object-actions/${objectActionId}`);
}

async function deleteObjectDefinitionByName(api: LiferayAPI, name: string) {
	const {items} = await api.get(
		`${OBJECT_ADMIN_PATH}/object-definitions?filter=${encodeURIComponent(
			`name eq 'C_${name}'`
		)}`
	);

	for (const item of items) {
		await api.delete(`${OBJECT_ADMIN_PATH}/object-definitions/${item.id}`);
	}
}
