import {ClayAlert} from 'clay-alert';
import dom from 'metal-dom';
import Uri from 'metal-uri';

function openEditContactInformationWindow(title, primaryKey, baseRenderURL, height) {
	const renderURL = new Uri(baseRenderURL);

	const portletNamespace = Liferay.Util.getPortletNamespace(renderURL.getParameterValue('p_p_id'));

	renderURL.setParameterValue(Liferay.Util.ns(portletNamespace, 'primaryKey'), primaryKey);

	const modalId = Liferay.Util.ns(portletNamespace, 'editContactInformationModal');

	Liferay.Util.openWindow(
		{
			dialog: {
				destroyOnHide: true,
				height: height,
				modal: true,
				resizable: false,
				'toolbars.footer': [
					{
						cssClass: 'btn-link close-modal',
						id: 'cancelButton',
						label: Liferay.Language.get('cancel'),
						on: {
							click: () => Liferay.Util.getWindow(modalId).hide()
						}
					},
					{
						cssClass: 'btn-primary',
						id: 'saveButton',
						label: Liferay.Language.get('save'),
						on: {
							click: function(event) {
								const {contentWindow} = document.getElementById(modalId + '_iframe_');

								const {form} = contentWindow.Liferay.Form.get(Liferay.Util.ns(portletNamespace, 'fm'));

								form.dispatchEvent(new Event('submit'));
							}
						}
					}
				],
				width: '600'
			},
			dialogIframe: {
				bodyCssClass: 'contact-information-edit-form'
			},
			id: modalId,
			title: title,
			uri: renderURL.toString()
		}
	);
}

function registerContactInformationListener(selector, renderURL, height) {
	var editClickHandler = dom.delegate(
		document.body,
		'click',
		selector,
		event => {
			event.preventDefault();

			const {primaryKey, title} = event.delegateTarget.dataset;

			openEditContactInformationWindow(
				title,
				primaryKey ? primaryKey : 0,
				renderURL,
				height
			);
		}
	);

	Liferay.once(
		'destroyPortlet',
		editClickHandler.removeListener.bind(editClickHandler)
	);
}

function registerModalFormListener(actionURL, form) {
	if (form) {
		form.addEventListener('submit', function (event) {
			event.preventDefault();

			const formData = new FormData(form);

			fetch(actionURL, {method: 'POST', body: formData})
				.then(function (response) {
					return response.json();
				})
				.then(function (data) {
					const exceptionType = data.exception;

					if (exceptionType) {
						console.log(exceptionType);

						const wrapper = document.getElementById('WebsiteURLException' + 'Wrapper');

						if (wrapper) {
							const messageKey = wrapper.getAttribute('data-key');

							showErrorAlert(
								'WebsiteURLException', messageKey);
						}
						else {
							showErrorToast(Liferay.Language.get('your-request-failed-to-complete'));
						}

					}
					else {
						const parentWindow = window.opener ?
							window.opener.parent : window.parent;

						parentWindow.location.reload();
					}
				})
				.catch(function (error) {
					showErrorToast(error.message);

					console.error(error);
				})
		});
	}
}

function showErrorAlert(exceptionType, messageKey) {
	new ClayAlert(
		{
			closeable: true,
			destroyOnHide: true,
			message: Liferay.Language.get(messageKey),
			spritemap: themeDisplay.getPathThemeImages() + '/lexicon/icons.svg',
			style: 'danger',
			title: Liferay.Language.get('error') + ':',
			visible: true
		},
		document.querySelector('#' + exceptionType + 'Wrapper')
	);
}

function showErrorToast(message) {
	Liferay.Util.openToast({
		message: message,
		type: 'danger',
		title: Liferay.Language.get('error') + ':'
	});
}

export {registerContactInformationListener, registerModalFormListener};
export default {registerContactInformationListener};