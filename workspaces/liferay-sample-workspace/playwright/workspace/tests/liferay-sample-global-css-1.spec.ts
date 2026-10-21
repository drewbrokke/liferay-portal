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

const clientExtension = getClientExtension('liferay-sample-global-css-1');

test(`${clientExtension.name} is added to every page of the company`, async ({
	page,
}) => {
	const url = getClientExtensionURL(clientExtension, 'url');

	await expectServed(page.request, url, /text\/css/);

	await page.goto('/');

	await expect(page.locator(`link[href*="${url}"]`)).toBeAttached();
});
