/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type UpdateFunction = (toggled: boolean, disabled: boolean) => void;
const toggles: {[featureFlagKey: string]: UpdateFunction} = {};

export function registerToggle(
	featureFlagKey: string,
	updateFunction: UpdateFunction
) {
	toggles[featureFlagKey] = updateFunction;
}

export function updateToggle(
	featureFlagKey: string,
	toggled: boolean,
	disabled: boolean
) {
	const updateFunction = toggles[featureFlagKey];

	if (updateFunction) {
		updateFunction(toggled, disabled);
	}
}
