/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {testCustomElementRenders} from '../helpers/customElements';
import {testImportMapsEntry} from '../helpers/importMaps';

testImportMapsEntry(
	'liferay-sample-etc-frontend',
	'liferay-sample-etc-frontend-js-import-maps-entry'
);

// The custom element imports the entry above, so it only renders when the
// import map resolves

testCustomElementRenders({
	id: 'liferay-sample-etc-frontend-custom-element',
	projectName: 'liferay-sample-etc-frontend',
	rendered: (element) => element.getByText('Greetings in:'),
});
