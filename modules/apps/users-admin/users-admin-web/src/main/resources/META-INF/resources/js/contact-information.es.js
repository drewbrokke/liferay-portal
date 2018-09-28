function openEditContactInformationWindow(title, entryId, fieldNames, baseRenderURL, baseEditURL, portletNamespace) {
	var renderURL = new URL(baseRenderURL);

	var renderURLSearchParams = renderURL.searchParams;

	renderURLSearchParams.set(`${portletNamespace}entryId`, entryId);

	var modalId = `${portletNamespace}editContactInformationModal`;

	Liferay.Util.openWindow(
		{
			dialog: {
				destroyOnHide: true,
				height: 520,
				modal: true,
				resizable: false,
				'toolbars.footer': [
					{
						cssClass: 'btn-link close-modal',
						id: 'cancelButton',
						label: Liferay.Language.get('cancel'),
						on: {
							click: function() {
								Liferay.Util.getWindow(modalId).hide();
							}
						}
					},
					{
						cssClass: 'btn-primary',
						id: 'saveButton',
						label: Liferay.Language.get('save'),
						on: {
							click: function(event) {
								var contentWindow = document.getElementById(modalId + '_iframe_').contentWindow;

								var formValidator = contentWindow.Liferay.Form.get(`${portletNamespace}fm`).formValidator;

								formValidator.validate();

								if (!formValidator.hasErrors()) {
									var windowDocument = contentWindow.document;

									var editURL = new URL(baseEditURL);

									var editURLSearchParams = editURL.searchParams;

									editURLSearchParams.set(`${portletNamespace}entryId`, entryId);

									fieldNames.forEach(
										function(fieldName) {
											var namespacedFieldName = `${portletNamespace}${fieldName}`;

											var field = windowDocument.getElementById(namespacedFieldName);

											var value = field.value;

											if (field.type === 'checkbox') {
												value = field.checked;
											}

											editURLSearchParams.set(namespacedFieldName, value);
										}
									);

									var form = document.getElementById(`${portletNamespace}fm`);

									submitForm(form, editURL.href);

									Liferay.Util.getWindow(modalId).hide();
								}
							}
						}
					}
				],
				width: '600'
			},
			id: modalId,
			title: title,
			uri: renderURL.href
		}
	);
}

export {openEditContactInformationWindow};
export default openEditContactInformationWindow;