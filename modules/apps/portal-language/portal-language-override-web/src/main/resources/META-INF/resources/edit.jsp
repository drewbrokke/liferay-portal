<%@ page
	import="com.liferay.portal.language.override.web.internal.display.EditDisplayContext" %>
<%@ page import="com.liferay.portal.kernel.util.LocalizationUtil" %><%--
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
EditDisplayContext editDisplayContext = (EditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<portlet:actionURL var="editURL" name="editPortalLanguageOverride" />

<clay:container-fluid>
	<liferay-frontend:edit-form action="<%= editURL %>" method="POST">
		<liferay-frontend:edit-form-body>
			<aui:input name="key" value="<%= editDisplayContext.getKey() %>" />
			<aui:input name="value" localized="<%= Boolean.TRUE %>" type="textarea" value='<%= LocalizationUtil.getXml(editDisplayContext.getLocalizedValuesMap(), "value") %>' />
		</liferay-frontend:edit-form-body>
		<liferay-frontend:edit-form-footer>
			<aui:button name="save" type="submit" />
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>
