import React, {useState} from 'react';
import ClayLayout from '@clayui/layout';
import {ClayToggle} from '@clayui/form';

const FeatureFlagRow = ({title, description, elementId, status, enabled: initialEnabled}) => {
	const [enabled, setEnabled] = useState(initialEnabled);

	async function updateEnabled(newEnabled) {
		alert("updating enabled");

		const result = await Promise.resolve(newEnabled);

		setEnabled(result);
	}

	return (
		<div id={elementId}>
			<ClayLayout.Row justify="start">
				<ClayLayout.Col size={1}>
					<span className="text-default">
						{status}
					</span>
				</ClayLayout.Col>

				<ClayLayout.Col className={enabled ? "" : "text-muted"} size={9}>
					<p>
						<strong>
							{title}
						</strong>
					</p>

					<p>
						<span className="text-default">
							{description}
						</span>
					</p>
				</ClayLayout.Col>

				<ClayLayout.Col size={2} >
					<ClayToggle symbol={{off: 'flag-empty', on: 'flag-full'}} label={enabled ? "Enabled" : "Disabled"} toggled={enabled} type="checkbox" onToggle={updateEnabled} />
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}

export default FeatureFlagRow;