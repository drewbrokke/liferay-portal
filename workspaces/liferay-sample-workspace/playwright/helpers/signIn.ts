/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {adminEmailAddress, adminPassword} from './environment';

/**
 * Signs in through the login form. Posting to /c/portal/login and reusing a
 * saved session both behave differently across versions, while the form does
 * not, so every test signs in within its own browser context.
 */
export async function signIn(page: Page) {
	await page.goto(
		'/?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_state=maximized'
	);

	await page.getByLabel('Email Address').fill(adminEmailAddress);
	await page.getByLabel('Password', {exact: true}).fill(adminPassword);

	await page
		.locator('form')
		.filter({has: page.getByLabel('Email Address')})
		.getByRole('button', {name: 'Sign In'})
		.click();

	await expect
		.poll(
			() =>
				page
					.evaluate(() =>
						(window as any).Liferay.ThemeDisplay.isSignedIn()
					)
					.catch(() => false),
			{timeout: 30 * 1000}
		)
		.toBe(true);
}
