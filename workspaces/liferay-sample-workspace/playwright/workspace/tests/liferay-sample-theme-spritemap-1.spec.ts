/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {expectServed} from '../../helpers/assertions';
import {
	getClientExtensions,
	getClientExtensionURL,
} from '../../helpers/clientExtensions';
import {addContentPage} from '../../helpers/pages';

for (const clientExtension of getClientExtensions(
	'liferay-sample-theme-spritemap-1'
)) {
	test(`${clientExtension.name} serves its spritemap`, async ({request}) => {
		for (const typeSetting of ['url']) {
			await expectServed(
				request,
				getClientExtensionURL(clientExtension, typeSetting),
				/image\/svg\+xml/
			);
		}
	});
}

for (const clientExtension of getClientExtensions(
	'liferay-sample-theme-spritemap-1'
)) {
	test(`${clientExtension.name} replaces the spritemap of a page that applies it`, async ({
		api,
		page,
		site,
	}) => {
		await page.goto(
			await addContentPage(api, site, {
				settings: {themeSpritemapClientExtension: clientExtension},
			})
		);

		expect(
			await page.evaluate(() => (window as any).Liferay.Icons.spritemap)
		).toContain(getClientExtensionURL(clientExtension, 'url'));
	});
}
