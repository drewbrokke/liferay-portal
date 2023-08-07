#!/bin/bash

cd $(dirname "${0}")

function refresh_liferay_sample_workspace {

	#
	# Client Extension: Sample Custom Element 2
	#

	rm -fr liferay-sample-workspace/client-extensions/liferay-sample-custom-element-2

	../tools/create_custom_element.sh liferay-sample-custom-element-2 react

	sed '/"eslintConfig": {/,/},/d' liferay-sample-custom-element-2/package.json | awk '!/^,$/' > temp.json && mv temp.json liferay-sample-custom-element-2/package.json

	mkdir -p liferay-sample-custom-element-2/src/common/components

	cat <<EOF > liferay-sample-custom-element-2/src/common/components/Comic.js
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Liferay} from '../services/liferay/liferay';

let oAuth2Client;

try {
	oAuth2Client = Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-sample-node-oauth-application-user-agent'
	);
}
catch (error) {
	console.error(error);
}

function Comic() {
	const [comicData, setComicData] = React.useState(null);

	React.useEffect(() => {
		oAuth2Client
			?.fetch('/comic')
			.then((comic) => {
			setComicData({
				alt: comic.alt,
				img: comic.img,
				title: comic.safe_title,
			});
		});
	}, []);

	return !comicData ? (
		<div>Loading...</div>
	) : (
		<div>
			<h2>{comicData.title}</h2>

			<p>
				<img alt={comicData.alt} src={comicData.img} />
			</p>
		</div>
	);
}

export default Comic;
EOF

	cat <<EOF > liferay-sample-custom-element-2/src/common/components/DadJoke.js
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Liferay} from '../services/liferay/liferay';

let oAuth2Client;

try {
	oAuth2Client = Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-sample-etc-spring-boot-oauth-application-user-agent'
	);
}
catch (error) {
	console.error(error);
}

function DadJoke() {
	const [joke, setJoke] = React.useState(null);

	React.useEffect(() => {
		oAuth2Client
			?.fetch('/dad/joke')
			.then((response) => response.text())
			.then((joke) => {
				setJoke(joke);
			})
			// eslint-disable-next-line no-console
			.catch((error) => console.log(error));
	}, []);

	if (!joke) {
		return <div>Loading...</div>;
	}

	return <div>{joke}</div>;
}

export default DadJoke;
EOF

	cat <<EOF > liferay-sample-custom-element-2/src/index.js
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {createRoot} from 'react-dom/client';

import Comic from './common/components/Comic';
import DadJoke from './common/components/DadJoke';
import api from './common/services/liferay/api';
import {Liferay} from './common/services/liferay/liferay';
import HelloBar from './routes/hello-bar/pages/HelloBar';
import HelloFoo from './routes/hello-foo/pages/HelloFoo';
import HelloWorld from './routes/hello-world/pages/HelloWorld';

import './common/styles/index.scss';

const App = ({route}) => {
	if (route === 'hello-bar') {
		return <HelloBar />;
	}

	if (route === 'hello-foo') {
		return <HelloFoo />;
	}

	return (
		<div>
			<HelloWorld />

			{Liferay.ThemeDisplay.isSignedIn() && (
				<div>
					<Comic />

					<hr />

					<DadJoke />
				</div>
			)}
		</div>
	);
};

class WebComponent extends HTMLElement {
	connectedCallback() {
		createRoot(this).render(
			<App route={this.getAttribute('route')} />,
			this
		);

		if (Liferay.ThemeDisplay.isSignedIn()) {
			api('o/headless-admin-user/v1.0/my-user-account')
				.then((response) => response.json())
				.then((response) => {
					if (response.givenName) {
						const nameElements =
							document.getElementsByClassName('hello-world-name');

						if (nameElements.length) {
							nameElements[0].innerHTML = response.givenName;
						}
					}
				})
				.catch((error) => {
					// eslint-disable-next-line no-console
					console.log(error);
				});
		}
	}
}

const ELEMENT_ID = 'liferay-sample-custom-element-2';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
EOF

	sed -i "s/react-scripts test/react-scripts test --passWithNoTests --watchAll=false/" liferay-sample-custom-element-2/package.json

	mv liferay-sample-custom-element-2 liferay-sample-workspace/client-extensions

	#
	# Client Extension: Sample Custom Element 3
	#

	rm -fr liferay-sample-workspace/client-extensions/liferay-sample-custom-element-3

	../tools/create_custom_element.sh liferay-sample-custom-element-3 angular

	mv liferay-sample-custom-element-3 liferay-sample-workspace/client-extensions
}

function main {
	refresh_liferay_sample_workspace
}

main "${@}"