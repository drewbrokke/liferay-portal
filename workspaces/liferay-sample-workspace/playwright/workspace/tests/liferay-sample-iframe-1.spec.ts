/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '../fixtures/test';
import {getClientExtensions} from '../../helpers/clientExtensions';
import {addContentPage} from '../../helpers/pages';

for (const clientExtension of getClientExtensions('liferay-sample-iframe-1')) {
	test(`${clientExtension.name} renders its iframe on a page`, async ({
		api,
		companyId,
		page,
		site,
	}) => {
		await page.goto(
			await addContentPage(api, site, {
				companyId,
				widgets: [clientExtension],
			})
		);

		await expect(
			page.locator(`iframe[src="${clientExtension.typeSettings.url[0]}"]`)
		).toBeVisible();
	});
}
