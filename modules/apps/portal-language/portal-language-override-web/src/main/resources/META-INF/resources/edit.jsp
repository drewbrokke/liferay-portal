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
EditDisplayContext editDisplayContext = (EditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(Validator.isNotNull(editDisplayContext.getBackURL()));
portletDisplay.setURLBack(editDisplayContext.getBackURL());
%>

<portlet:actionURL name="editPortalLanguageOverride" var="editURL" />

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= editURL %>"
		method="POST"
	>
		<aui:input name="redirect" type="hidden" value="<%= editDisplayContext.getBackURL() %>" />

		<liferay-frontend:edit-form-body>
			<c:choose>
				<c:when test="<%= Validator.isNotNull(editDisplayContext.getKey()) %>">
					<aui:input name="key" type="hidden" value="<%= editDisplayContext.getKey() %>" />

					<aui:input disabled="<%= true %>" name="keyDisplay" type="text" value="<%= editDisplayContext.getKey() %>" />

					<c:if test="<%= editDisplayContext.isShowOriginalValues() %>">
						<liferay-ui:input-localized
							disabled="<%= true %>"
							helpMessage="originalValues"
							ignoreRequestValue="<%= true %>"
							name="originalValuesDisplay"
							selectedLanguageId="<%= editDisplayContext.getSelectedLanguage() %>"
							xml='<%= LocalizationUtil.getXml(editDisplayContext.getOriginalValuesLocalizedValuesMap(), "value") %>'
						/>
					</c:if>
				</c:when>
				<c:otherwise>
					<aui:input name="key" pattern="[^ ]+" required="<%= true %>" value="<%= editDisplayContext.getKey() %>" />
				</c:otherwise>
			</c:choose>

			<liferay-ui:input-localized
				helpMessage="overrideTranslations"
				ignoreRequestValue="<%= true %>"
				name="value"
				selectedLanguageId="<%= editDisplayContext.getSelectedLanguage() %>"
				xml='<%= LocalizationUtil.getXml(editDisplayContext.getValuesLocalizedValuesMap(), "value") %>'
			/>
		</liferay-frontend:edit-form-body>

		<liferay-frontend:edit-form-footer>
			<aui:button name="save" type="submit" />
			<aui:button href="<%= editDisplayContext.getBackURL() %>" type="cancel" />
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>