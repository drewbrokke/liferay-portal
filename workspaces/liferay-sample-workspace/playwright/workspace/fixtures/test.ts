/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test as shared} from '../../fixtures/test';

/**
 * Fixtures that only this workspace needs. Everything under the workspace
 * directory belongs to this workspace and never syncs from
 * liferay-sample-workspace, so extend the shared test here and import this
 * module from the specs.
 */
export const test = shared.extend<{}>({});

export {expect} from '../../fixtures/test';
