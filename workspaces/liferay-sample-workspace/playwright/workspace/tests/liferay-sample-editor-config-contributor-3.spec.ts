/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '../fixtures/test';
import {expectServed} from '../../helpers/assertions';
import {
	getClientExtensions,
	getClientExtensionURL,
} from '../../helpers/clientExtensions';

for (const clientExtension of getClientExtensions(
	'liferay-sample-editor-config-contributor-3'
)) {
	test(`${clientExtension.name} serves its editor configuration script`, async ({
		request,
	}) => {
		for (const typeSetting of ['url']) {
			await expectServed(
				request,
				getClientExtensionURL(clientExtension, typeSetting),
				/javascript/
			);
		}
	});
}
