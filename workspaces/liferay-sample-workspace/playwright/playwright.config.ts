/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {defineConfig, devices} from '@playwright/test';

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// require('dotenv').config();

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
	/* Fail the build on CI if you accidentally left test.only in the source code. */
	forbidOnly: !!process.env.CI,

	/* Run tests in files in parallel */
	fullyParallel: true,

	/* Configure projects for major browsers */
	projects: [
		{
			name: 'chromium',
			use: {...devices['Desktop Chrome']},
		},

		// {
		// 	name: 'firefox',
		// 	use: {...devices['Desktop Firefox']},
		// },

		// {
		// 	name: 'webkit',
		// 	use: {...devices['Desktop Safari']},
		// },

		/* Test against mobile viewports. */
		// {
		//   name: 'Mobile Chrome',
		//   use: { ...devices['Pixel 5'] },
		// },
		// {
		//   name: 'Mobile Safari',
		//   use: { ...devices['iPhone 12'] },
		// },

		/* Test against branded browsers. */
		// {
		//   name: 'Microsoft Edge',
		//   use: { ...devices['Desktop Edge'], channel: 'msedge' },
		// },
		// {
		//   name: 'Google Chrome',
		//   use: { ...devices['Desktop Chrome'], channel: 'chrome' },
		// },
	],

	/* Reporter to use. See https://playwright.dev/docs/test-reporters */
	reporter: [
		[
			'html',
			{
				open: 'never',
			},
		],
		['dot'],
	],

	/* Retry on CI only */
	retries: process.env.CI ? 2 : 0,
	testDir: './tests',

	timeout: 240000,

	/* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
	use: {
		baseURL: process.env.PORTAL_URL
			? process.env.PORTAL_URL
			: 'http://localhost:8080',
		screenshot: 'only-on-failure',
		trace: 'retain-on-failure',
	},

	/* Run your local dev server before starting the tests */
	// webServer: {
	// 	command: '../gradlew serverStart',
	// 	reuseExistingServer: !process.env.CI,
	// 	url: 'http://127.0.0.1:8080',
	// },

	/* Opt out of parallel tests on CI. */
	workers: process.env.CI ? 1 : undefined,
});
