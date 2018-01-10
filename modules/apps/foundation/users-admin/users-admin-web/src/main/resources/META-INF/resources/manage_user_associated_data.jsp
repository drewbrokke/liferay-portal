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
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

Long selUserId = ParamUtil.getLong(request, "selUserId");
%>

<liferay-ui:search-container
	emptyResultsMessage="no-data-requires-anonymization"
	id="UADEntityTypeComposites"
>
	<liferay-ui:search-container-results
		results="${entityTypeComposites}"
	/>

	<liferay-ui:search-container-row
		className="java.lang.Object"
		escapedModel="<%= true %>"
		keyProperty="name"
		modelVar="entityTypeComposite"
	>
		<portlet:renderURL var="manageUserAssociatedDataEntitiesURL">
			<portlet:param name="mvcRenderCommandName" value="/users_admin/manage_user_associated_data_entities" />
			<portlet:param name="key" value="${entityTypeComposite.getKey()}" />
			<portlet:param name="userId" value="<%= String.valueOf(selUserId) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= manageUserAssociatedDataEntitiesURL %>"
			name="name"
			property="entityTypeName"
		/>

		<liferay-ui:search-container-column-text
			href="<%= manageUserAssociatedDataEntitiesURL %>"
			name="description"
			property="entityTypeDescription"
		/>

		<liferay-ui:search-container-column-text
			href="<%= manageUserAssociatedDataEntitiesURL %>"
			name="count"
			property="count"
		/>

		<liferay-ui:search-container-column-text
			href="<%= manageUserAssociatedDataEntitiesURL %>"
			name="nonanonymizable-fields"
			property="entityTypeNonAnonymizableFields"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<portlet:actionURL name="/users_admin/anonymize_user_associated_data" var="anonymizeUserAssociatedDataActionURL" />

<portlet:renderURL var="manageUserAssociatedDataRenderURL">
	<portlet:param name="mvcRenderCommandName" value="/users_admin/manage_user_associated_data" />
	<portlet:param name="backURL" value="<%= backURL %>" />
	<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
</portlet:renderURL>

<aui:form action="<%= anonymizeUserAssociatedDataActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="anonymize" />
	<aui:input name="redirect" type="hidden" value="<%= manageUserAssociatedDataRenderURL %>" />
	<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
	<aui:input name="selUserId" type="hidden" value="<%= selUserId %>" />

	<aui:button type="submit" value="autoanonymize-all" />
</aui:form>