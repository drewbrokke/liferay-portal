/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

export const adminEmailAddress =
	process.env.LIFERAY_ADMIN_EMAIL_ADDRESS || 'test@liferay.com';

export const adminPassword = process.env.LIFERAY_ADMIN_PASSWORD || 'test';

export const portalURL =
	process.env.PORTAL_URL ||
	`http://localhost:${process.env.LIFERAY_FUNCTIONAL_TEST_PORT || '18080'}`;

export const workspaceDir = path.join(__dirname, '..', '..');
