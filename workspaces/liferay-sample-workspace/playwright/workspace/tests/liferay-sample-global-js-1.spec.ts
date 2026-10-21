/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {expectServed} from '../../helpers/assertions';
import {
	getClientExtension,
	getClientExtensionURL,
} from '../../helpers/clientExtensions';
import {addContentPage} from '../../helpers/pages';

const clientExtension = getClientExtension('liferay-sample-global-js-1');

test(`${clientExtension.name} is served`, async ({request}) => {
	await expectServed(
		request,
		getClientExtensionURL(clientExtension, 'url'),
		/javascript/
	);
});

test(`${clientExtension.name} is added to a page that applies it`, async ({
	api,
	page,
	site,
}) => {
	const url = getClientExtensionURL(clientExtension, 'url');

	await page.goto(
		await addContentPage(api, site, {
			settings: {globalJSClientExtensions: [clientExtension]},
		})
	);

	await expect(page.locator(`script[src*="${url}"]`)).toBeAttached();
});
