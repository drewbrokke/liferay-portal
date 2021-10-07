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

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.terms.of.use.web.internal.constants.TermsOfUseConstants" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %>

<%@ page import="javax.portlet.PortletRequest" %><%@
page import="javax.portlet.ResourceURL" %>

<%
String documentationLinkOpenTag = String.format("<a href=\"%s\" target=\"_blank\">", TermsOfUseConstants.COMMERCE_ACTIVATION_DOCUMENTATION_URL);
String documentationLinkCloseTag = "</a>";
String emailLink = String.format("<a href=\"mailto:%s\"}>%s</a>", TermsOfUseConstants.LIFERAY_SALES_EMAIL_ADDRESS, TermsOfUseConstants.LIFERAY_SALES_EMAIL_ADDRESS);
%>

<aui:script position="inline">
	Liferay.Util.openModal({
		bodyHTML:
			'<liferay-ui:message arguments="<%= new String[] { documentationLinkOpenTag, documentationLinkCloseTag, emailLink } %>" key="commerce-terms-of-use" />',
		buttons: [
			{
				displayType: 'primary',
				label: '<liferay-ui:message key="done" />',
				onClick: function ({processClose}) {
					<!-- Liferay.Util.fetch('<%= resourceURL.toString() %>', {
						method: 'POST',
					}); -->

					alert('sending confirmation')

					processClose();
				},
			},
		],
		height: '457px',
		title: '<liferay-ui:message key="terms-of-use" />',
	});
</aui:script>