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

<%@ include file="/html/portlet/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "server");

boolean showTabs1 = false;

if (portletName.equals(PortletKeys.ADMIN_PLUGINS)) {
	tabs1 = "plugins";
}
else if (portletName.equals(PortletKeys.ADMIN_SERVER)) {
	tabs1 = "server";
}
else if (portletName.equals(PortletKeys.ADMIN)) {
	showTabs1 = true;
}

String tabs2 = ParamUtil.getString(request, "tabs2");
String tabs3 = ParamUtil.getString(request, "tabs3");

if (tabs1.equals("plugins")) {
	if (!tabs2.equals("portlet-plugins") && !tabs2.equals("theme-plugins") && !tabs2.equals("layout-template-plugins") && !tabs2.equals("hook-plugins") && !tabs2.equals("web-plugins")) {
		tabs2 = "portlet-plugins";
	}
}

NumberFormat numberFormat = NumberFormat.getInstance();

numberFormat.setMaximumIntegerDigits(2);
numberFormat.setMinimumIntegerDigits(2);
%>

<%@ include file="/html/portlet/admin/init-ext.jsp" %>