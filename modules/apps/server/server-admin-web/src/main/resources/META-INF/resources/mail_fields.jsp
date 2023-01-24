<%@ page import="com.liferay.portal.kernel.mail.MailSettingsProviderUtil" %>
<%@ page import="com.liferay.portal.kernel.mail.MailSettings" %><%--
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
long companyId = ParamUtil.getLong(request, "preferencesCompanyId");

MailSettings obfuscatedMailSettings = MailSettingsProviderUtil.getObfuscatedMailSettings(companyId);
%>

<aui:fieldset>
	<aui:input name="preferencesCompanyId" type="hidden" value="<%= companyId %>" />

	<aui:input cssClass="lfr-input-text-container" label="incoming-pop-server" name="pop3Host" type="text" value="<%= obfuscatedMailSettings.pop3Host() %>" />

	<%
	int pop3Port = obfuscatedMailSettings.pop3Port();

	String pop3PortString = StringPool.BLANK;

	if (pop3Port > 0) {
		pop3PortString = String.valueOf(pop3Port);
	}
	%>

	<aui:input cssClass="lfr-input-text-container" label="incoming-port" name="pop3Port" type="text" value="<%= pop3PortString %>" />

	<aui:input label="use-a-secure-network-connection" name="pop3Secure" type="checkbox" value='<%= obfuscatedMailSettings.storeProtocol().equals("pop3s") %>' />

	<aui:input autocomplete="new-password" cssClass="lfr-input-text-container" label="user-name" name="pop3User" type="text" value="<%= obfuscatedMailSettings.pop3User() %>" />

	<aui:input autocomplete="new-password" cssClass="lfr-input-text-container" label="password" name="pop3Password" type="password" value="<%= obfuscatedMailSettings.pop3Password() %>" />

	<aui:input cssClass="lfr-input-text-container" label="outgoing-smtp-server" name="smtpHost" type="text" value="<%= obfuscatedMailSettings.smtpHost() %>" />

	<%
	int smtpPort = obfuscatedMailSettings.smtpPort();

	String smtpPortString = StringPool.BLANK;

	if (smtpPort > 0) {
		smtpPortString = String.valueOf(smtpPort);
	}
	%>
	<aui:input cssClass="lfr-input-text-container" label="outgoing-port" name="smtpPort" type="text" value="<%= smtpPortString %>" />

	<aui:input label="use-a-secure-network-connection" name="smtpSecure" type="checkbox" value='<%= obfuscatedMailSettings.transportProtocol().equals("smtps") %>' />

	<aui:input label="enable-starttls" name="smtpStartTLSEnable" type="checkbox" value="<%= obfuscatedMailSettings.smtpStartTLSEnable() %>" />

	<aui:input autocomplete="new-password" cssClass="lfr-input-text-container" label="user-name" name="smtpUser" type="text" value="<%= obfuscatedMailSettings.smtpUser() %>" />

	<aui:input autocomplete="new-password" cssClass="lfr-input-text-container" label="password" name="smtpPassword" type="password" value="<%= obfuscatedMailSettings.smtpPassword() %>" />

	<aui:input cssClass="lfr-textarea-container" label="manually-specify-additional-javamail-properties-to-override-the-above-configuration" name="advancedProperties" type="textarea" value="<%= obfuscatedMailSettings.advancedProperties() %>" />
</aui:fieldset>