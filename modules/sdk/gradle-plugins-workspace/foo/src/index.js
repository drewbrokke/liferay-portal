import React from 'react';
import ReactDOM from 'react-dom';

import AppComponent from './AppComponent';

class CustomElement extends HTMLElement {
	constructor() {
		super();

		const root = document.createElement('div');

		ReactDOM.render(
			<AppComponent/>, 
			root
		);

		this.appendChild(root);
	}

	connectedCallback() {
	}

	disconnectedCallback() {
	}

}

if (customElements.get('asdf')) {
	console.log(
		'Skipping registration for <asdf> (already registered)'
	);
} else {
	customElements.define('asdf', CustomElement);
}
