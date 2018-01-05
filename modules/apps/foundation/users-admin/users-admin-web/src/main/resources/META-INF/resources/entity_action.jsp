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
UADEntityTypeComposite entityTypeComposite = (UADEntityTypeComposite)request.getAttribute("entityTypeComposite");

long userId = entityTypeComposite.getUserId();
%>

<liferay-ui:icon-menu direction="left-side" icon="<%= StringPool.BLANK %>" markupView="lexicon" message="<%= StringPool.BLANK %>" showWhenSingleIcon="<%= true %>">
	<portlet:renderURL var="manageUserAssociatedDataEntitiesURL">
		<portlet:param name="mvcRenderCommandName" value="/users_admin/manage_user_associated_data_entities" />
		<portlet:param name="key" value="${entityTypeComposite.getKey()}" />
		<portlet:param name="userId" value="<%= String.valueOf(userId) %>" />
	</portlet:renderURL>

	<liferay-ui:icon
		message="view"
		url="<%= manageUserAssociatedDataEntitiesURL %>"
	/>
</liferay-ui:icon-menu>