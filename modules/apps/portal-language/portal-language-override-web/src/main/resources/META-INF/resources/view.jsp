<%@ page import="com.liferay.portal.language.override.web.internal.display.ViewDisplayContext" %>
<%@ page import="com.liferay.portal.language.override.web.internal.dto.PLOItemDTO" %>

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
ViewDisplayContext viewDisplayContext = (ViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewDisplayContext.getManagementToolbarDisplayContext() %>"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<liferay-ui:search-container
			searchContainer="<%= viewDisplayContext.getSearchContainer() %>"
			orderByCol="key"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.language.override.web.internal.dto.PLOItemDTO"
				keyProperty="key"
				modelVar="ploItemDTO"
			>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="key"
					value="<%= ploItemDTO.getKey() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="value"
					value="<%= ploItemDTO.getValue() %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>