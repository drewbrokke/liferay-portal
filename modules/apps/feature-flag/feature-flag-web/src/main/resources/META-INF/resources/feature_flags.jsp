<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page
	import="com.liferay.feature.flag.web.internal.FeatureFlagsDisplayContext" %>
<%@ page import="com.liferay.feature.flag.web.internal.FeatureFlagDisplay" %>
<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%--
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
FeatureFlagsDisplayContext featureFlagsDisplayContext = (FeatureFlagsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:management-toolbar managementToolbarDisplayContext="<%= featureFlagsDisplayContext.getManagementToolbarDisplayContext() %>" />

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<liferay-ui:search-container searchContainer="<%= featureFlagsDisplayContext.getSearchContainer() %>">
			<liferay-ui:search-container-row
				className="com.liferay.feature.flag.web.internal.FeatureFlagDisplay"
				keyProperty="key"
				modelVar="var"
			>
				<%
				FeatureFlagDisplay featureFlagDisplay = (FeatureFlagDisplay)var;
				%>

				<liferay-ui:search-container-column-text
					colspan="<%= 2 %>"
				>
					<%
					String elementId = featureFlagDisplay.getKey() + "_wrapper";
					%>

					<div id="<%= elementId %>">
						<h2 class="h3"><%= featureFlagDisplay.getTitle()%></h2>
						<span class="text-default"><%= featureFlagDisplay.getDescription()%></span>
						<span class="text-default"><%= featureFlagDisplay.getStatusString()%></span>
					</div>

					<react:component
						module="js/FeatureFlagRow"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"title", featureFlagDisplay.getTitle()
							).put(
								"description", featureFlagDisplay.getDescription()
							).put(
								"status", featureFlagDisplay.getStatusString()
							).put(
								"enabled", featureFlagDisplay.isEnabled()
							).put(
								"elementId", elementId
							).build()
						 %>'
					/>
				</liferay-ui:search-container-column-text>

<%--				<liferay-ui:search-container-column-text name="key" property="key" />--%>
<%--				<liferay-ui:search-container-column-text name="title" property="title" />--%>
<%--				<liferay-ui:search-container-column-text name="description" property="description" />--%>
<%--				<liferay-ui:search-container-column-text name="enabled" value="<%= String.valueOf(featureFlagDisplay.isEnabled())%>" />--%>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator displayStyle="descriptive" markupView="lexicon"/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>
