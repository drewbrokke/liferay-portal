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
PortletURL acknowledgeActionURL = PortletURLBuilder.create(
	PortletURLFactoryUtil.create(request, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN, PortletRequest.ACTION_PHASE)
).setActionName(
	"/account_admin/acknowledge_toc"
).setRedirect(
	PortalUtil.getCurrentURL(request)
).buildPortletURL();
%>

<div style="display: none">
	<liferay-portlet:runtime portletName="<%= AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN %>" />
</div>

<form action="<%= acknowledgeActionURL.toString() %>" id="acknowledgeTOCForm"></form>

<script>
	const bodyHTML = 'Use of Liferay Commerce apps is not covered by Liferay Support and other subscription benefits without an active Liferay Commerce Subscription. If you do not want to use or have access to Liferay Commerce, access can be disabled completely by following these steps <a href="{0}">here</a>. If you are interested in purchasing a Liferay Commerce Subscription, please speak to your Liferay sales representative or contact Liferay Sales at <a href="mailto:{1}">{1}</a>.'

	Liferay.Util.openModal({
		bodyHTML,
		buttons: [
			{
				displayType: 'primary',
				label: '<liferay-ui:message key="done" />',
				onClick: function() {
					const form = document.getElementById('acknowledgeTOCForm');

					Liferay.Util.postForm(form);
				},
			},
		],
		height: '457px',
		title: '<liferay-ui:message key="terms-of-use" />',
	});
</script>
