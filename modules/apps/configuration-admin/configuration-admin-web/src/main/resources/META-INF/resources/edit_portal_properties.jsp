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
EditPortalPropertiesDisplayContext editPortalPropertiesDisplayContext = (EditPortalPropertiesDisplayContext)request.getAttribute(ConfigurationAdminWebKeys.EDIT_PORTAL_PROPERTIES_DISPLAY_CONTEXT);
%>

<clay:sheet
	size="full"
>
	<h1>Hello Edit Portal Properties</h1>

	<portlet:actionURL name="/configuration_admin/edit_portal_properties" var="editPortalPropertiesActionURL" />

	<aui:form action="<%= editPortalPropertiesActionURL %>">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<%
		for (ConfigurablePortalProperty configurablePortalPropertyDisplay : editPortalPropertiesDisplayContext.getConfigurablePortalProperties()) {
		%>

			<aui:input name='<%= "settings--" + configurablePortalPropertyDisplay.getName() + "--" %>' value="<%= PrefsPropsUtil.getString(company.getCompanyId(), configurablePortalPropertyDisplay.getName()) %>" />

		<%
		}
		%>

		<clay:button
			label="save"
			small="<%= true %>"
			type="submit"
		/>
	</aui:form>
</clay:sheet>