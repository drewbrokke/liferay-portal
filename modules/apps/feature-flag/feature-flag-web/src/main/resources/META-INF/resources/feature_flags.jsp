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
			>

				<%
				FeatureFlagDisplay featureFlagDisplay = (FeatureFlagDisplay)model;
				%>

				<liferay-ui:search-container-column-text colspan="11">
					<div>
						<h4>
							<%= featureFlagDisplay.getTitle()%>
							<span>

							<clay:badge
								displayType="<%= featureFlagDisplay.getBadgeDisplayStyle() %>"
								label="<%= featureFlagDisplay.getStatusString() %>"
							/>
							</span>
						</h4>
					</div>
					<div>
						<span class="text-default"><%= featureFlagDisplay.getDescription()%></span>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text colspan="1">
					<aui:input
						checked="<%= featureFlagDisplay.isEnabled() %>"
						inlineLabel="right"
						label='<%= featureFlagDisplay.isEnabled() ? "Enabled" : "Disabled" %>'
						name='<%= featureFlagDisplay.getKey() + "-toggle" %>'
						type="toggle-switch"
					/>

					<react:component
						module="js/FeatureFlagToggle"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"enabled", featureFlagDisplay.isEnabled()
							).put(
								"featureFlagKey", featureFlagDisplay.getKey()
							).build()
						 %>'
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator displayStyle="descriptive" markupView="lexicon"/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>
