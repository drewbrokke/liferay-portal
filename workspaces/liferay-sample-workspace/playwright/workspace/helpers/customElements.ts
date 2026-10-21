/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {expect, test} from '../../fixtures/test';
import {getClientExtension} from '../../helpers/clientExtensions';
import {addContentPage} from '../../helpers/pages';

/**
 * Places a custom element sample on a content page and asserts that it
 * renders, which proves its scripts, styles, and any import maps it relies on
 * all load in this version of Liferay.
 */
export function testCustomElementRenders({
	id,
	projectName,
	rendered,
}: {
	id?: string;
	projectName: string;
	rendered: (element: Locator, page: Page) => Locator;
}) {
	const clientExtension = getClientExtension(projectName, id);

	test(`${clientExtension.name} renders on a page`, async ({
		api,
		companyId,
		page,
		site,
	}) => {
		const pageErrors: string[] = [];

		page.on('pageerror', (error) => pageErrors.push(error.message));

		await page.goto(
			await addContentPage(api, site, {
				companyId,
				widgets: [clientExtension],
			})
		);

		const element = page.locator(
			clientExtension.typeSettings.htmlElementName[0]
		);

		await expect(element).toBeAttached();

		try {
			await expect(rendered(element, page)).toBeVisible();
		}
		catch (error) {
			if (pageErrors.length) {
				throw new Error(
					`${clientExtension.name} did not render. Page errors:\n${pageErrors.join('\n')}`
				);
			}

			throw error;
		}

		expect(pageErrors, 'page errors').toEqual([]);
	});
}
