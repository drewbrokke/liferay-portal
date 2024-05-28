/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, test} from '@playwright/test';

import {cleanCX, deployCX} from '../utils/deployHelpers.ts';
import performLogin from '../utils/performLogin.ts';

test.beforeAll(async () => {
	await deployCX(
		'client-extensions/liferay-sample-custom-element-1',
		'STARTED liferaysamplecustomelement1'
	);
});

test.afterAll(async () => {
	await cleanCX();
});

async function dragAndDrop(from: Locator, to: Locator, options: any = {}) {
	await expect(from).toBeVisible();
	await expect(to).toBeVisible();

	await from.dragTo(to, {...options, trial: true});
	await from.dragTo(to, options);
}

test('foo bar', async ({page}) => {
	await page.goto('/');

	await expect(await page.getByText('Welcome to Liferay')).toBeVisible();

	performLogin(page, 'test');

	await page.getByRole('link', {name: 'Edit'}).click();
	await page.getByRole('tab', {exact: true, name: 'Widgets'}).click();

	const menuitem = page.getByRole('menuitem', {
		name: 'Liferay Sample Custom Element',
	});

	await expect(menuitem).toBeVisible();

	const target = page
		.getByLabel('Container')
		.locator('.page-editor__container')
		.first();

	await expect(target).toBeVisible();

	await dragAndDrop(menuitem, target, {
		sourcePosition: {
			x: 20,
			y: 20,
		},
		targetPosition: {
			x: 20,
			y: 20,
		},
	});

	await expect(page.getByText('Portlet internal route:')).toBeVisible();

	await page.getByLabel('Publish', {exact: true}).click();

	await expect(page.getByText('Portlet internal route:')).toBeVisible();

	const vanillaCounterWidget = page.locator('vanilla-counter');

	await expect(vanillaCounterWidget).toBeVisible();

	const plusButton = vanillaCounterWidget.getByRole('button', {
		exact: true,
		name: '+',
	});

	await plusButton.click();
	await plusButton.click();

	await expect(vanillaCounterWidget.getByText('1')).not.toBeVisible();
	await expect(vanillaCounterWidget.getByText('2')).toBeVisible();

	const minusButton = vanillaCounterWidget.getByRole('button', {
		exact: true,
		name: '-',
	});

	await minusButton.click();

	await expect(vanillaCounterWidget.getByText('1')).toBeVisible();
	await expect(vanillaCounterWidget.getByText('2')).not.toBeVisible();
});
