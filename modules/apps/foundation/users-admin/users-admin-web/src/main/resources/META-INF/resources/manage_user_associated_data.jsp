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

User selUser = PortalUtil.getSelectedUser(request);
%>

<portlet:actionURL name="/users_admin/manage_user_associated_data" var="manageUserAssociatedDataActionURL" />

<portlet:renderURL var="manageUserAssociatedDataRenderURL">
	<portlet:param name="mvcRenderCommandName" value="/users_admin/manage_user_associated_data" />
	<portlet:param name="backURL" value="<%= backURL %>" />
</portlet:renderURL>

<aui:form action="<%= manageUserAssociatedDataActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="anonymize" />
	<aui:input name="redirect" type="hidden" value="<%= manageUserAssociatedDataRenderURL %>" />
	<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
	<aui:input name="selUserId" type="hidden" value="<%= (selUser != null) ? selUser.getUserId() : 24 %>" />

	<aui:button type="submit" value="Autoanonymize" />
</aui:form>

<%
if (selUser != null) {
	PortalUtil.setPageSubtitle(selUser.getFullName() + " Associated Data ", request);
}
%>