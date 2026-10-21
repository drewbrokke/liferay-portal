/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {defineConfig, devices} from '@playwright/test';

import {portalURL} from './helpers/environment';

export default defineConfig({
	expect: {
		timeout: 15 * 1000,
	},
	forbidOnly: !!process.env.CI,
	globalSetup: require.resolve('./global-setup'),
	projects: [
		{
			name: 'client-extensions',
			use: devices['Desktop Chrome'],
		},
	],
	reporter: [['list'], ['html', {open: 'never'}]],
	retries: process.env.CI ? 1 : 0,
	testDir: './workspace/tests',
	timeout: 90 * 1000,
	use: {
		baseURL: portalURL,
		screenshot: 'only-on-failure',
		trace: 'retain-on-failure',
	},
	workers: 1,
});
