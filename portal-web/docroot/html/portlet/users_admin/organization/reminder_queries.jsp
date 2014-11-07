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

<%@ include file="/html/portlet/users_admin/init.jsp" %>

<%
Organization organization = (Organization)request.getAttribute(WebKeys.ORGANIZATION);

String reminderQueries = ParamUtil.getString(request, "reminderQueries");

String currentLanguageId = LanguageUtil.getLanguageId(request);
Locale defaultLocale = LocaleUtil.getDefault();
String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

Locale[] locales = LanguageUtil.getAvailableLocales();

if ((organization != null) && Validator.isNull(reminderQueries)) {
	reminderQueries = StringUtil.merge(organization.getReminderQueryQuestions(defaultLocale), StringPool.NEW_LINE);
}

Map<Locale, String> reminderQueriesMap = LocalizationUtil.getLocalizedParameter(renderRequest, "reminderQueries");
%>

<h3><liferay-ui:message key="reminder-queries" /></h3>

<div class="alert alert-info">
	<liferay-ui:message key="specify-custom-reminder-queries-for-the-users-of-this-organization" />
</div>

<aui:fieldset cssClass="reminder">
	<aui:input label='<%= LanguageUtil.get(request, "default-language") + StringPool.COLON + StringPool.SPACE + defaultLocale.getDisplayName(defaultLocale) %>' name="reminderQueries" type="textarea" value="<%= reminderQueries %>" />

	<aui:select cssClass="localized-language-selector" label='<%= LanguageUtil.get(request, "localized-language") + StringPool.COLON %>' name="reminderQueryLanguageId">
		<aui:option value="" />

		<%
		for (int i = 0; i < locales.length; i++) {
			if (locales[i].equals(defaultLocale)) {
				continue;
			}

			String curReminderQueries = reminderQueriesMap.get(locales[i]);

			if ((organization != null) && Validator.isNull(curReminderQueries)) {
				curReminderQueries = StringUtil.merge(organization.getReminderQueryQuestions(locales[i]), StringPool.NEW_LINE);
			}

			String style = StringPool.BLANK;

			if (Validator.isNotNull(curReminderQueries)) {
				style = "font-weight: bold;";
			}
		%>

			<aui:option label="<%= locales[i].getDisplayName(locale) %>" selected="<%= (currentLanguageId.equals(LocaleUtil.toLanguageId(locales[i]))) %>" style="<%= style %>" value="<%= LocaleUtil.toLanguageId(locales[i]) %>" />

		<%
		}
		%>

	</aui:select>

	<%
	for (int i = 0; i < locales.length; i++) {
		if (locales[i].equals(defaultLocale)) {
			continue;
		}

		String curReminderQueries = reminderQueriesMap.get(locales[i]);

		if ((organization != null) && Validator.isNull(curReminderQueries)) {
			curReminderQueries = StringUtil.merge(organization.getReminderQueryQuestions(locales[i]), StringPool.NEW_LINE);
		}
	%>

		<aui:input name='<%= "reminderQueries_" + LocaleUtil.toLanguageId(locales[i]) %>' type="hidden" value="<%= curReminderQueries %>" />

	<%
	}
	%>

	<aui:input label="" name="reminderQueries_temp" title="reminder-queries" type="textarea" />
</aui:fieldset>

<aui:script sandbox="<%= true %>">
	var form = $(document.<portlet:namespace />fm);
	var languageOptions = form.fm('reminderQueryLanguageId').children();
	var lastLanguageId = '<%= currentLanguageId %>';
	var reminderQueriesChanged = false;

	function onReminderQueriesChanged() {
		reminderQueriesChanged = true;
	}

	function updateReminderQueriesLanguage() {
		var reminderQueriesTemp = $('#<portlet:namespace />reminderQueries_temp');

		if (lastLanguageId != '<%= defaultLanguageId %>') {
			if (reminderQueriesChanged) {
				var reminderQueriesValue = reminderQueriesTemp.val();

				if (reminderQueriesValue == null) {
					reminderQueriesValue = '';
				}

				$('#<portlet:namespace />reminderQueries_' + lastLanguageId).val(reminderQueriesValue);

				reminderQueriesChanged = false;
			}
		}

		var selLanguageId = '';

		for (var i = 0; i < languageOptions.length; i++) {
			var option = languageOptions.eq(i);

			if (option.prop('selected')) {
				selLanguageId = option.val();

				break;
			}
		}

		if (selLanguageId != '') {
			updateReminderQueriesLanguageTemps(selLanguageId);

			reminderQueriesTemp.removeClass('hide');
		}
		else {
			reminderQueriesTemp.addClass('hide');
		}



		lastLanguageId = selLanguageId;
	}

	function updateReminderQueriesLanguageTemps(lang) {
		if (lang != '<%= defaultLanguageId %>') {
			var reminderQueriesLang = $('#<portlet:namespace />reminderQueries_' + lang);
			var reminderQueriesTemp = $('#<portlet:namespace />reminderQueries_temp');
			var reminderQueriesValue = reminderQueriesLang.val();
			var defaultReminderQueriesLang = $('#<portlet:namespace />reminderQueries_<%= defaultLanguageId %>');
			var defaultReminderQueriesValue = defaultReminderQueriesLang.val();

			if (defaultReminderQueriesValue == null) {
				defaultReminderQueriesValue = '';
			}

			if ((reminderQueriesValue == null) || (reminderQueriesValue == '')) {
				reminderQueriesTemp.val(defaultReminderQueriesValue);
			}
			else {
				reminderQueriesTemp.val(reminderQueriesValue);
			}
		}
	}

	updateReminderQueriesLanguageTemps(lastLanguageId);

	var reminderQueriesHandle = Liferay.on('submitForm', updateReminderQueriesLanguage);

	var clearReminderQueriesHandle = function(event) {
		if (event.portletId === '<%= portletDisplay.getRootPortletId() %>') {
			reminderQueriesHandle.detach();

			Liferay.detach('destroyPortlet', clearReminderQueriesHandle);
		}
	};

	Liferay.on('destroyPortlet', clearReminderQueriesHandle);

	$('#<portlet:namespace />reminderQueryLanguageId').on('change', updateReminderQueriesLanguage);
	$('#<portlet:namespace />reminderQueries_temp').on('change', onReminderQueriesChanged);
</aui:script>