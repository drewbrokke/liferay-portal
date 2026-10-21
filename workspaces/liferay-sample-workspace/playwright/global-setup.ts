/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LiferayAPI} from './helpers/api';
import {
	adminEmailAddress,
	adminPassword,
	apiEmailAddress,
	apiPassword,
} from './helpers/environment';

interface Role {
	id: number;
	name: string;
}

interface UserAccount {
	id: number;
}

/**
 * Creates the administrator the API client authenticates as, once per run.
 * Runs as the browser's user, which is safe here because no browser session
 * exists yet.
 */
export default async function globalSetup() {
	const api = await LiferayAPI.create({
		emailAddress: adminEmailAddress,
		password: adminPassword,
	});

	try {
		const response = await api.requestContext.get(
			`/o/headless-admin-user/v1.0/user-accounts/by-email-address/${apiEmailAddress}`
		);

		if (response.ok()) {
			return;
		}

		if (response.status() !== 404) {
			throw new Error(
				`GET user account ${apiEmailAddress} returned ${response.status()}: ${await response.text()}`
			);
		}

		const userAccount = await api.post<UserAccount>(
			'/o/headless-admin-user/v1.0/user-accounts',
			{
				alternateName: apiEmailAddress.split('@')[0],
				emailAddress: apiEmailAddress,
				familyName: 'API',
				givenName: 'Playwright',
				password: apiPassword,
			}
		);

		const {items: roles} = await api.get<{items: Role[]}>(
			'/o/headless-admin-user/v1.0/roles?pageSize=200'
		);

		const administrator = roles.find(
			(role) => role.name === 'Administrator'
		);

		if (!administrator) {
			throw new Error('The Administrator role does not exist.');
		}

		await api.post(
			`/o/headless-admin-user/v1.0/roles/${administrator.id}/association/user-account/${userAccount.id}`,
			undefined
		);
	}
	finally {
		await api.dispose();
	}
}
