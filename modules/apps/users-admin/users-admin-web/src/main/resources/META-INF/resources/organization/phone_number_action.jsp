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

<%
OrganizationScreenNavigationDisplayContext organizationScreenNavigationDisplayContext = (OrganizationScreenNavigationDisplayContext)request.getAttribute(UsersAdminWebKeys.ORGANIZATION_SCREEN_NAVIGATION_DISPLAY_CONTEXT);

long organizationId = organizationScreenNavigationDisplayContext.getOrganizationId();

Organization organization = organizationScreenNavigationDisplayContext.getOrganization();

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

Phone phone = (Phone)row.getObject();

long phoneId = phone.getPhoneId();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<liferay-ui:icon
		id='<%= row.getRowId() + "editPhone" %>'
		message="edit"
		url="javascript:;"
	/>

	<portlet:actionURL name="/users_admin/update_organization_contact_information" var="makePrimaryURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= UsersAdminWebKeys.CMD_MAKE_PRIMARY %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="entryId" value="<%= String.valueOf(phoneId) %>" />
		<portlet:param name="listType" value="<%= ListTypeConstants.PHONE %>" />
		<portlet:param name="organizationId" value="<%= String.valueOf(organizationId) %>" />
	</portlet:actionURL>

	<liferay-ui:icon
		message="make-primary"
		url="<%= makePrimaryURL %>"
	/>

	<portlet:actionURL name="/users_admin/update_organization_contact_information" var="removePhoneURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="entryId" value="<%= String.valueOf(phoneId) %>" />
		<portlet:param name="listType" value="<%= ListTypeConstants.PHONE %>" />
		<portlet:param name="organizationId" value="<%= String.valueOf(organizationId) %>" />
	</portlet:actionURL>

	<liferay-ui:icon
		message="remove"
		url="<%= removePhoneURL %>"
	/>
</liferay-ui:icon-menu>

<aui:script use="liferay-portlet-url">
	<portlet:renderURL var="editPhoneURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
		<portlet:param name="mvcPath" value="/organization/edit_phone_number.jsp" />
		<portlet:param name="phoneId" value="<%= String.valueOf(phoneId) %>" />
	</portlet:renderURL>

	function <portlet:namespace />openEditPhoneWindow(actionURL, title) {
		Liferay.Util.openWindow(
			{
				dialog: {
					destroyOnHide: true,
					height: '500',
					modal: true,
					resizable: false,
					'toolbars.footer': [
						{
							cssClass: 'btn-link close-modal',
							id: 'cancelButton',
							label: '<%= UnicodeLanguageUtil.get(request, "cancel") %>',
							on: {
								click: function() {
									Liferay.Util.getWindow('<portlet:namespace />editPhoneModal').hide();
								}
							}
						},
						{
							cssClass: 'btn-primary',
							id: 'addButton',
							label: '<%= LanguageUtil.get(request, "save") %>',
							on: {
								click: function(event) {
									var windowDocument = document.getElementById('<portlet:namespace />editPhoneModal_iframe_').contentWindow.document;

									var updatePhoneURL = Liferay.PortletURL.createURL(actionURL);

									updatePhoneURL.setParameter('phoneExtension', windowDocument.getElementById('<portlet:namespace />phoneExtension').value);
									updatePhoneURL.setParameter('phoneNumber', windowDocument.getElementById('<portlet:namespace />phoneNumber').value);
									updatePhoneURL.setParameter('phonePrimary', windowDocument.getElementById('<portlet:namespace />phonePrimary').value);
									updatePhoneURL.setParameter('phoneTypeId', windowDocument.getElementById('<portlet:namespace />phoneTypeId').value);

									var organizationFm = document.getElementById('<portlet:namespace />fm');

									submitForm(organizationFm, updatePhoneURL.toString());

									organizationFm.submit();

									Liferay.Util.getWindow('<portlet:namespace />editPhoneModal').hide();
								}
							}
						}
					],
					width: '500'
				},
				id: '<portlet:namespace />editPhoneModal',
				title: title,
				uri: '<%= editPhoneURL.toString() %>'
			}
		);
	}

	<portlet:actionURL name="/users_admin/update_organization_contact_information" var="updatePhoneURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UPDATE %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="entryId" value="<%= String.valueOf(phoneId) %>" />
		<portlet:param name="listType" value="<%= ListTypeConstants.PHONE %>" />
		<portlet:param name="organizationId" value="<%= String.valueOf(organizationId) %>" />
	</portlet:actionURL>

	$('#<portlet:namespace /><%= row.getRowId() %>editPhone').on(
		'click', function(event) {
			<portlet:namespace />openEditPhoneWindow('<%= updatePhoneURL.toString() %>', '<%= UnicodeLanguageUtil.get(request, "edit-phone-number") %>')
		}
	);
</aui:script>