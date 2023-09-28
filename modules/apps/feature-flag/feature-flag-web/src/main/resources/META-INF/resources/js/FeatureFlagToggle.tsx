/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import React, {useState} from 'react';

import * as FeatureFlagToggleRegistry from './FeatureFlagToggleRegistry';

interface IDependentFeatureFlag {
	disabled: boolean;
	featureFlagKey: string;
	toggled: boolean;
}

interface IProps {
	ariaDescribedBy: string;
	companyId: number;
	disabled: boolean;
	featureFlagKey: string;
	inputName: string;
	labelOff: string;
	labelOn: string;
	toggled: boolean;
}

const FeatureFlagToggle = ({
	ariaDescribedBy,
	companyId,
	disabled: initialDisabled,
	featureFlagKey,
	inputName,
	labelOff,
	labelOn,
	toggled: initialToggled,
}: IProps) => {
	const [disabled, setDisabled] = useState(initialDisabled);
	const [toggled, setToggled] = useState(initialToggled);

	FeatureFlagToggleRegistry.registerToggle(
		featureFlagKey,
		(newToggled: boolean, newDisabled: boolean) => {
			if (toggled !== newToggled) {
				setToggled(newToggled);
			}
			if (disabled !== newDisabled) {
				setDisabled(newDisabled);
			}
		}
	);

	async function updateToggled(newToggled: boolean) {
		setDisabled(true);

		try {
			const response = await Liferay.Util.fetch(
				'/o/com-liferay-feature-flag-web/set-enabled',
				{
					body: Liferay.Util.objectToFormData({
						companyId,
						enabled: newToggled,
						key: featureFlagKey,
					}),
					method: 'POST',
				}
			);

			if (!response.ok) {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'could-not-update-feature-flag'
					),
					type: 'danger',
				});

				return;
			}

			setToggled(newToggled);

			const json = await response.json();

			for (const dependency of json.dependentFeatureFlags as IDependentFeatureFlag[]) {
				FeatureFlagToggleRegistry.updateToggle(
					dependency.featureFlagKey,
					dependency.toggled,
					dependency.disabled
				);
			}
		}
		finally {
			setDisabled(false);
		}
	}

	return (
		<>
			<ClayToggle
				aria-describedby={ariaDescribedBy}
				disabled={disabled}
				id={inputName}
				label={toggled ? labelOn : labelOff}
				onToggle={updateToggled}
				toggled={toggled}
				type="checkbox"
			/>
		</>
	);
};

export default FeatureFlagToggle;
