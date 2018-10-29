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
LanguageTemplateConfigurationDisplayContext languageTemplateConfigurationDisplayContext = (LanguageTemplateConfigurationDisplayContext)request.getAttribute(LanguageTemplateConfigurationDisplayContext.class.getName());

String currentTemplateName = languageTemplateConfigurationDisplayContext.getCurrentTemplateName();
%>

<portlet:actionURL name="/site_navigation_language/update_language_template_configuration" var="editURL" />

<div class="sheet sheet-lb">
	<div class="sheet-header">
		<h2><%= languageTemplateConfigurationDisplayContext.getTitle() %></h2>
	</div>

	<aui:form action="<%= editURL %>" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= languageTemplateConfigurationDisplayContext.getRedirect() %>" />

		<aui:select label="<%= HtmlUtil.escape(languageTemplateConfigurationDisplayContext.getFieldLabel()) %>" name="ddmTemplateKey" value="<%= currentTemplateName %>">

			<%
			for (String[] templateValue : languageTemplateConfigurationDisplayContext.getTemplateValues()) {
			%>

				<aui:option label="<%= templateValue[1] %>" selected="<%= currentTemplateName.equals(templateValue[0]) %>" value="<%= templateValue[0] %>" />

			<%
			}
			%>

		</aui:select>

		<div class="sheet-footer">
			<aui:button type="submit" />
		</div>
	</aui:form>
</div>