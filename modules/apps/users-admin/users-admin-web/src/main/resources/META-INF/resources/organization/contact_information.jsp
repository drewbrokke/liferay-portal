<%--
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
--%>

<%@ include file="/init.jsp" %>

<div class="sheet-section">
	<liferay-util:include page="/organization/phone_numbers.jsp" servletContext="<%= application %>" />
</div>

<div class="sheet-section">
	<liferay-util:include page="/organization/additional_email_addresses.jsp" servletContext="<%= application %>" />
</div>

<div class="sheet-section">
	<liferay-util:include page="/organization/websites.jsp" servletContext="<%= application %>" />
</div>

<aui:script>
	function <portlet:namespace />openEditContactInformationWindow(title, entryId, fieldNames, baseRenderURL, baseEditURL) {
		var renderURL = new URL(baseRenderURL);

		var renderURLSearchParams = renderURL.searchParams;

		renderURLSearchParams.set('<portlet:namespace />entryId', entryId);

		var modalId = '<portlet:namespace />editContactInformationModal';

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
							label: '<%= UnicodeLanguageUtil.get(request, "cancel") %>',
							on: {
								click: function() {
									Liferay.Util.getWindow(modalId).hide();
								}
							}
						},
						{
							cssClass: 'btn-primary',
							id: 'saveButton',
							label: '<%= LanguageUtil.get(request, "save") %>',
							on: {
								click: function(event) {
									var contentWindow = document.getElementById(modalId + '_iframe_').contentWindow;

									var formValidator = contentWindow.Liferay.Form.get('<portlet:namespace />fm').formValidator;

									formValidator.validate();

									if (!formValidator.hasErrors()) {
										var windowDocument = contentWindow.document;

										var editURL = new URL(baseEditURL);

										var editURLSearchParams = editURL.searchParams;

										editURLSearchParams.set('<portlet:namespace />entryId', entryId);

										fieldNames.forEach(
											function(fieldName) {
												var namespacedFieldName = '<portlet:namespace />' + fieldName;

												var field = windowDocument.getElementById(namespacedFieldName);

												var value = field.value;

												if (field.type === 'checkbox') {
													value = field.checked;
												}

												editURLSearchParams.set(namespacedFieldName, value);
											}
										);

										var form = document.getElementById('<portlet:namespace />fm');

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
</aui:script>