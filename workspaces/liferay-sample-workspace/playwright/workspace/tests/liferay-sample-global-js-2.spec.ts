/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {
	getClientExtension,
	getClientExtensionURL,
} from '../../helpers/clientExtensions';
import {addContentPage} from '../../helpers/pages';

const clientExtension = getClientExtension('liferay-sample-global-js-2');

test(`${clientExtension.name} is added with its script element attributes`, async ({
	api,
	page,
	site,
}) => {
	await page.goto(
		await addContentPage(api, site, {
			settings: {globalJSClientExtensions: [clientExtension]},
		})
	);

	const script = page.locator(
		`script[src*="${getClientExtensionURL(clientExtension, 'url')}"]`
	);

	await expect(script).toBeAttached();

	const attributes = JSON.parse(
		clientExtension.typeSettings.scriptElementAttributesJSON[0]
	);

	for (const [name, value] of Object.entries(attributes)) {

		// Liferay deliberately drops async and defer from the declared
		// attributes, because the page's load type controls them

		if (name === 'async' || name === 'defer') {
			continue;
		}

		if (value === true) {
			await expect(script, `the ${name} attribute`).toHaveAttribute(name);
		}
		else {
			await expect(script, `the ${name} attribute`).toHaveAttribute(
				name,
				String(value)
			);
		}
	}
});
