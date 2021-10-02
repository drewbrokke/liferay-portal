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
boolean showTermsOfUse = GetterUtil.getBoolean(request.getAttribute("SHOW_TERMS_OF_USE"));
%>

<c:if test="<% showTermsOfUse %>">
	<h1>Show terms of use</h1>
</c:if>

<c:if test="<% !showTermsOfUse %>">
	<h1>Don't show</h1>
</c:if>
