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

<%@ page import="com.liferay.commerce.account.web.internal.constants.CommerceAccountPortletKeys" %><%@
page import="com.liferay.petra.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %>

<%@ page import="javax.portlet.PortletRequest" %><%@
page import="javax.portlet.PortletURL" %>

<%
PortletURL acknowledgeActionURL = PortletURLBuilder.create(
	PortletURLFactoryUtil.create(request, CommerceAccountPortletKeys.COMMERCE_ACCOUNT, PortletRequest.ACTION_PHASE)
).setActionName(
	"/commerce_terms_of_use/acknowledge"
).buildPortletURL();
%>

<aui:script position="inline">
	const bodyHTML = 'Use of Liferay Commerce apps is not covered by Liferay Support and other subscription benefits without an active Liferay Commerce Subscription. If you do not want to use or have access to Liferay Commerce, access can be disabled completely by following these steps <a href="{0}">here</a>. If you are interested in purchasing a Liferay Commerce Subscription, please speak to your Liferay sales representative or contact Liferay Sales at <a href="mailto:{1}">{1}</a>.'

	Liferay.Util.openModal({
		bodyHTML,
		buttons: [
			{
				displayType: 'primary',
				label: '<liferay-ui:message key="done" />',
				onClick: function({processClose}) {
					Liferay.Util.fetch(
						'<%= acknowledgeActionURL.toString() %>',
						{ method: 'POST' }
					);

					processClose();
				}
			}
		],
		height: '457px',
		title: '<liferay-ui:message key="terms-of-use" />'
	});
</aui:script>