<%@ page
	import="com.liferay.portal.language.override.web.internal.display.ViewDisplayContext" %>
<%@ page import="com.liferay.portal.kernel.util.TextFormatter" %><%--
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
ViewDisplayContext viewDisplayContext = (ViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<%--// management toolbar--%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewDisplayContext.getManagementToolbarDisplayContext() %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		searchContainer="<%= viewDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.language.override.web.internal.dto.PLOItemDTO"
			keyProperty="key"
			modelVar="ploItemDTO"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcPath" value="/edit.jsp" />
				<portlet:param name="key" value="<%= ploItemDTO.getKey() %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text name="key" property="key" href="<%= rowURL %>" />
			<liferay-ui:search-container-column-text name="type" property="type" href="<%= rowURL %>" />
<%--			<liferay-ui:search-container-column-text name="flags" href="<%= rowURL %>">--%>

<%--				<%--%>
<%--				for (String languageId : ploItemDTO.getValueLanguageIds()) {--%>
<%--				%>--%>

<%--					<clay:icon symbol="<%= TextFormatter.format(languageId, TextFormatter.O) %>" />--%>

<%--				<%--%>
<%--				}--%>
<%--				%>--%>

<%--			</liferay-ui:search-container-column-text>--%>
		</liferay-ui:search-container-row>
	</liferay-ui:search-container>
</clay:container-fluid>