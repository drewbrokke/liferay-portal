/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../../fixtures/test';
import {expectServed} from '../../helpers/assertions';
import {
	getClientExtension,
	getClientExtensionURL,
} from '../../helpers/clientExtensions';

/**
 * Asserts that a JS import maps entry sample is served and that every page
 * maps its bare specifier to it.
 */
export function testImportMapsEntry(projectName: string, id?: string) {
	const clientExtension = getClientExtension(projectName, id);

	test(`${clientExtension.name} maps its bare specifier on every page`, async ({
		page,
	}) => {
		const url = getClientExtensionURL(clientExtension, 'url');

		await expectServed(page.request, url, /javascript/);

		await page.goto('/');

		const importMap = JSON.parse(
			await page
				.locator('script[type="importmap"]')
				.evaluate((script) => script.textContent || '{}')
		);

		expect(
			importMap.imports[clientExtension.typeSettings.bareSpecifier[0]]
		).toContain(url);
	});
}
