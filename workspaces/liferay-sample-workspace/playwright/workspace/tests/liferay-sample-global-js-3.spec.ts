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

const clientExtension = getClientExtension('liferay-sample-global-js-3');

test(`${clientExtension.name} is added to the head of every page of the company`, async ({
	page,
}) => {
	const url = getClientExtensionURL(clientExtension, 'url');

	await expectServed(page.request, url, /javascript/);

	await page.goto('/');

	await expect(page.locator(`head script[src*="${url}"]`)).toBeAttached();
});
