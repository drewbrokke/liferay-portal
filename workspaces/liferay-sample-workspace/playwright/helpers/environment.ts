/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import dotenv from 'dotenv';
import path from 'path';

const playwrightDir = path.join(__dirname, '..');

/**
 * The environment wins over .env.local, which wins over .env. dotenv never
 * overwrites a variable that is already set, so load the most specific file
 * first.
 */
dotenv.config({path: path.join(playwrightDir, '.env.local'), quiet: true});
dotenv.config({path: path.join(playwrightDir, '.env'), quiet: true});

/**
 * The user the browser signs in as.
 */
export const adminEmailAddress =
	process.env.LIFERAY_ADMIN_EMAIL_ADDRESS || 'test@liferay.com';

export const adminPassword = process.env.LIFERAY_ADMIN_PASSWORD || 'test';

/**
 * The user the API client authenticates as. It is a separate administrator,
 * because a basic authentication request as the browser's user signs that
 * browser session out on some Liferay versions.
 */
export const apiEmailAddress =
	process.env.LIFERAY_API_EMAIL_ADDRESS || 'playwright@liferay.com';

export const apiPassword = process.env.LIFERAY_API_PASSWORD || 'playwright';

export const portalURL =
	process.env.PORTAL_URL ||
	`http://localhost:${process.env.LIFERAY_FUNCTIONAL_TEST_PORT || '18080'}`;

export const workspaceDir = path.join(playwrightDir, '..');
