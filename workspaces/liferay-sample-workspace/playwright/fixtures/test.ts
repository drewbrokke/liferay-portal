/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test as base} from '@playwright/test';

import {LiferayAPI} from '../helpers/api';
import {Site} from '../helpers/pages';
import {signIn} from '../helpers/signIn';

export const test = base.extend<{
	api: LiferayAPI;
	companyId: string;
	site: Site;
}>({
	api: async ({}, use) => {
		const api = await LiferayAPI.create();

		try {
			await use(api);
		}
		finally {
			await api.dispose();
		}
	},

	page: async ({page}, use) => {
		await signIn(page);

		await use(page);
	},

	companyId: async ({page}, use) => {
		await page.goto('/');

		await use(
			await page.evaluate(() =>
				String((window as any).Liferay.ThemeDisplay.getCompanyId())
			)
		);
	},

	/**
	 * A throwaway site, so page level configuration never leaks between tests.
	 */
	site: async ({api}, use) => {
		const site = await api.post<Site>('/o/headless-site/v1.0/sites', {
			name: `Sample ${Date.now()}`,
		});

		try {
			await use(site);
		}
		finally {
			await api.delete(`/o/headless-site/v1.0/sites/${site.id}`);
		}
	},
});

export {expect};
