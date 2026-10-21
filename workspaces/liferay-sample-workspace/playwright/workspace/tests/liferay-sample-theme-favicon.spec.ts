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
	'liferay-sample-theme-favicon'
)) {
	test(`${clientExtension.name} serves its favicon`, async ({request}) => {
		for (const typeSetting of ['url']) {
			await expectServed(
				request,
				getClientExtensionURL(clientExtension, typeSetting),
				/image\/(vnd\.microsoft\.icon|x-icon)/
			);
		}
	});
}

for (const clientExtension of getClientExtensions(
	'liferay-sample-theme-favicon'
)) {
	test(`${clientExtension.name} replaces the favicon of a page that applies it`, async ({
		api,
		page,
		site,
	}) => {
		await page.goto(
			await addContentPage(api, site, {
				settings: {favIcon: clientExtension},
			})
		);

		await expect(
			page.locator(
				`link[rel~="icon"][href*="${getClientExtensionURL(clientExtension, 'url')}"]`
			)
		).toBeAttached();
	});
}
