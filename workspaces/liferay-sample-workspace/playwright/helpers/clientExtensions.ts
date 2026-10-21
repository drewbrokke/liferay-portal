/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import {workspaceDir} from './environment';

const CET_CONFIGURATION_PREFIX =
	'com.liferay.client.extension.type.configuration.CETConfiguration~';

export interface ClientExtension {
	baseURL: string;
	externalReferenceCode: string;
	id: string;
	name: string;
	projectName: string;
	type: string;
	typeSettings: Record<string, string[]>;
}

/**
 * Reads the client extensions a project deploys from the configuration its
 * build generates, so tests follow the hashed file names and the IDs that
 * actually reached the portal instead of repeating them.
 */
export function getClientExtensions(projectName: string): ClientExtension[] {
	const configurationFile = path.join(
		workspaceDir,
		'client-extensions',
		projectName,
		'build',
		'liferay-client-extension-build',
		`${projectName}.client-extension-config.json`
	);

	if (!fs.existsSync(configurationFile)) {
		throw new Error(
			`${configurationFile} does not exist. Build ${projectName} first.`
		);
	}

	const configurations = JSON.parse(
		fs.readFileSync(configurationFile, 'utf8')
	);

	return Object.entries(configurations)
		.filter(([key]) => key.startsWith(CET_CONFIGURATION_PREFIX))
		.map(([key, value]: [string, any]) => {
			const id = key.substring(CET_CONFIGURATION_PREFIX.length);

			return {
				baseURL: String(value.baseURL).replace('${portalURL}', ''),
				externalReferenceCode: `LXC:${id}`,
				id,
				name: value.name,
				projectName,
				type: value.type,
				typeSettings: parseTypeSettings(value.typeSettings || []),
			};
		});
}

export function getClientExtension(
	projectName: string,
	id: string = projectName
): ClientExtension {
	const clientExtension = getClientExtensions(projectName).find(
		(clientExtension) => clientExtension.id === id
	);

	if (!clientExtension) {
		throw new Error(`${projectName} does not define ${id}.`);
	}

	return clientExtension;
}

export function getClientExtensionURL(
	clientExtension: ClientExtension,
	typeSetting: string
): string {
	const values = clientExtension.typeSettings[typeSetting];

	if (!values?.length) {
		throw new Error(
			`${clientExtension.id} has no ${typeSetting} type setting.`
		);
	}

	return toAbsoluteURL(clientExtension, values[0]);
}

export function getWidgetName(
	clientExtension: ClientExtension,
	companyId: string
): string {
	return [
		'com_liferay_client_extension_web_internal_portlet_ClientExtensionEntryPortlet',
		companyId,
		clientExtension.externalReferenceCode.replace(/\W/g, '_'),
	].join('_');
}

function parseTypeSettings(typeSettings: string[]): Record<string, string[]> {
	const parsedTypeSettings: Record<string, string[]> = {};

	for (const typeSetting of typeSettings) {
		const index = typeSetting.indexOf('=');

		parsedTypeSettings[typeSetting.substring(0, index)] = typeSetting
			.substring(index + 1)
			.split('\n')
			.filter(Boolean);
	}

	return parsedTypeSettings;
}

function toAbsoluteURL(clientExtension: ClientExtension, url: string): string {
	if (/^https?:\/\//.test(url) || url.startsWith('/')) {
		return url;
	}

	return `${clientExtension.baseURL}/${url}`;
}
