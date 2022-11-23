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
FeatureFlagsDisplayContext featureFlagsDisplayContext = (FeatureFlagsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= featureFlagsDisplayContext.getManagementToolbarDisplayContext() %>"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<liferay-ui:search-container
			searchContainer="<%= featureFlagsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.feature.flag.web.internal.FeatureFlagDisplay"
				keyProperty="key"
			>

				<%
				FeatureFlagDisplay featureFlagDisplay = (FeatureFlagDisplay)model;
				%>

				<liferay-ui:search-container-column-text
					colspan="<%= 11 %>"
				>
					<h5>
						<strong><%= featureFlagDisplay.getTitle(locale) %></strong>
					</h5>

					<h6 class="text-default">
						<%= featureFlagDisplay.getDescription(locale) %>
					</h6>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					colspan="<%= 1 %>"
				>
					<%@ include file="/toggle_switch.jspf" %>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="descriptive"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>