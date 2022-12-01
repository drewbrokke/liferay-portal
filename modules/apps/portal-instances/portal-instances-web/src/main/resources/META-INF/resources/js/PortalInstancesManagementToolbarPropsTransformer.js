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

import {fetch, openModal, getOpener, openToast} from 'frontend-js-web';

function handleOnClick(namespace) {
	const iframe = document.querySelector('.liferay-modal iframe');

	if (!iframe) {
		return;
	}

	const iframeDocument = iframe.contentWindow.document;

	const form = iframeDocument.getElementById(`${namespace}fm`);

	const content = iframeDocument.querySelector('.add-instance-content');
	const loading = iframeDocument.querySelector('.add-instance-loading');

	const formData = new FormData(form);

	content.classList.add('d-none');
	content.classList.remove('d-block');
	loading.classList.add('d-flex');

	const alertContainer = iframeDocument.querySelector(
		'.add-instance-alert-container'
	);

	if (alertContainer.hasChildNodes()) {
		alertContainer.firstChild?.remove();
	}

	fetch(form.action, {
		body: formData,
		method: 'POST',
	})
		.then((response) => response.json())
		.then((response) => {
			const opener = getOpener();

			if (!response.error) {
				opener.Liferay.fire('closeModal', {
					id: `${namespace}addSiteDialog`,
					redirect: opener.location.href,
				});
			}
			else {
				content.classList.add('d-block');
				loading.classList.add('d-none');
				loading.classList.remove('d-flex');

				openToast({
					autoClose: false,
					container: alertContainer,
					message: response.error,
					toastProps: {
						onClose: null,
					},
					type: 'danger',
					variant: 'stripe',
				});
			}
		});
}

export default function propsTransformer({...otherProps}) {
	return {
		...otherProps,
		onCreateButtonClick(event, {item}) {
			event.preventDefault();

			openModal({
				buttons: [
					{
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						label: Liferay.Language.get('add'),
						onClick() {
							handleOnClick(item?.data.namespace)
						}
					},
				],
				height: '60vh',
				iframeBodyCssClass: '',
				size: 'md',
				title: Liferay.Language.get('add-instance'),
				url: item?.data.addInstanceURL,
			});
		},
	};
}
