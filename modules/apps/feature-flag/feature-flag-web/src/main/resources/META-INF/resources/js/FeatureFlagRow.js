import React from 'react';

const FeatureFlagRow = ({title, description, status, enabled}) => {

	return (
		<>
			<h2 className="h3">
				{title}
			</h2>

			<span className="text-default">
				{description}
			</span>

			<span className="text-default">
				{description}
			</span>
		</>
	);
}

export default FeatureFlagRow;