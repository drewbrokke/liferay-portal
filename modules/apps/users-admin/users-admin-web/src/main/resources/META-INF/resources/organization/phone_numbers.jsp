<%@ page import="com.liferay.portal.kernel.portlet.PortletURLUtil" %><%--
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

List<Phone> phones = PhoneServiceUtil.getPhones(Organization.class.getName(), organizationId);
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="phoneNumbers"
/>

<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + Organization.class.getName() + ListTypeConstants.PHONE %>" message="please-select-a-type" />
<liferay-ui:error exception="<%= PhoneNumberException.class %>" message="please-enter-a-valid-phone-number" />
<liferay-ui:error exception="<%= PhoneNumberExtensionException.class %>" message="please-enter-a-valid-phone-number-extension" />

<h3 class="sheet-subtitle">
	<span class="autofit-padded-no-gutters autofit-row">
		<span class="autofit-col autofit-col-expand">
			<span class="heading-text">
				<liferay-ui:message key="phone-numbers" />
			</span>
		</span>
		<span class="autofit-col">
			<liferay-ui:icon
				cssClass="modify-contact-info-link"
				data="<%=
					new HashMap<String, Object>() {
						{
							put("title", LanguageUtil.get(request, "add-phone-number"));
							put("entry-id", String.valueOf(0));
						}
					}
				%>"
				label="<%= true %>"
				linkCssClass="btn btn-secondary btn-sm"
				message="add"
				url="javascript:;"
			/>
		</span>
	</span>
</h3>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-wrapper"
	emptyResultsMessage="this-organization-does-not-have-any-phone-numbers"
	headerNames="phone-number,type,extension,"
	id="phonesSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= phones.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= phones.subList(searchContainer.getStart(), searchContainer.getResultEnd()) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Phone"
		escapedModel="<%= true %>"
		keyProperty="phoneId"
		modelVar="phone"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
			name="phone-number"
			property="number"
		/>

		<%
		ListType phoneListType = ListTypeServiceUtil.getListType(phone.getTypeId());

		String phoneTypeKey = phoneListType.getName();
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
			name="type"
			value="<%= LanguageUtil.get(request, phoneTypeKey) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
			name="extension"
			property="extension"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
		>
			<c:if test="<%= phone.isPrimary() %>">
				<span class="label label-primary">
					<span class="label-item label-item-expand"><%= StringUtil.toUpperCase(LanguageUtil.get(request, "primary"), locale) %></span>
				</span>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/organization/phone_number_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<portlet:actionURL name="/users_admin/update_organization_contact_information" var="editPhoneActionURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.EDIT %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="listType" value="<%= ListTypeConstants.PHONE %>" />
	<portlet:param name="organizationId" value="<%= String.valueOf(organizationId) %>" />
</portlet:actionURL>

<portlet:renderURL var="editPhoneRenderURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/organization/edit_phone_number.jsp" />
</portlet:renderURL>

<aui:script>
	$('body').on(
		'click',
		'.modify-contact-info-link a',
		function(event) {
			event.preventDefault();

			var currentTarget = $(event.currentTarget);

			<portlet:namespace />openEditContactInformationWindow(
				currentTarget.data('title'),
				currentTarget.data('entry-id'),
				['phoneExtension', 'phoneNumber', 'phonePrimary', 'phoneTypeId'],
				'<%= editPhoneRenderURL.toString() %>',
				'<%= editPhoneActionURL.toString() %>'
			);
		}
	);
</aui:script>