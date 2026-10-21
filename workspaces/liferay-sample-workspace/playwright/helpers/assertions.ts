/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {APIRequestContext, expect} from '@playwright/test';

/**
 * Asserts that the portal serves a client extension's file. Right after a
 * deploy the file can take a moment to appear, so poll before failing.
 */
export async function expectServed(
	requestContext: APIRequestContext,
	url: string,
	contentType: RegExp
): Promise<void> {
	await expect
		.poll(async () => (await requestContext.get(url)).status(), {
			message: `${url} is not served`,
			timeout: 60 * 1000,
		})
		.toBe(200);

	const response = await requestContext.get(url);

	expect(response.headers()['content-type'], url).toMatch(contentType);
}
