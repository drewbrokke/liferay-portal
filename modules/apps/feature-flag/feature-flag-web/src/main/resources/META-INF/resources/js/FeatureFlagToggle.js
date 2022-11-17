/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import React, {useState} from 'react';
import {ClayToggle} from '@clayui/form';

const FeatureFlagToggle = ({enabled: initialEnabled, featureFlagKey}) => {
	const [enabled, setEnabled] = useState(initialEnabled);

	async function updateEnabled(newEnabled) {
		alert(`updating enabled for ${featureFlagKey}: ${newEnabled}`);


		try {
			// const result = await Promise.resolve(newEnabled);
			await Promise.reject(new Error("there was a problem updating the status"));

			setEnabled(result);
		}
		catch (error) {
			alert(error.message);
		}
	}

	return (
		<>
			<ClayToggle
				id={`${featureFlagKey}-toggle`}
				symbol={{off: 'flag-empty', on: 'flag-full'}}
				label={enabled ? "Enabled" : "Disabled"}
				toggled={enabled}
				type="checkbox"
				onToggle={updateEnabled}
			/>
		</>
	);
}

export default FeatureFlagToggle;