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
	'liferay-sample-theme-css-4'
)) {
	test(`${clientExtension.name} serves its Clay and main CSS`, async ({
		request,
	}) => {
		for (const typeSetting of ['clayURL', 'mainURL']) {
			await expectServed(
				request,
				getClientExtensionURL(clientExtension, typeSetting),
				/text\/css/
			);
		}
	});
}

for (const clientExtension of getClientExtensions(
	'liferay-sample-theme-css-4'
)) {
	test(`${clientExtension.name} styles a page that applies it`, async ({
		api,
		page,
		site,
	}) => {
		await page.goto(
			await addContentPage(api, site, {
				settings: {themeCSSClientExtension: clientExtension},
			})
		);

		await expect(
			page.locator(
				`link[href*="${getClientExtensionURL(clientExtension, 'mainURL')}"]`
			)
		).toBeAttached();
	});
}
