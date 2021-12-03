<%@ page import="java.util.Map" %>
<%@ page import="com.liferay.portal.kernel.settings.LocalizedValuesMap" %>
<%@ page import="java.util.Locale" %><%--
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
			<clay:sheet-header>
				<h2 class="sheet-title"><liferay-ui:message key="language-key-information" /></h2>
			</clay:sheet-header>

			<clay:sheet-section>
				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<span class="heading-text"><liferay-ui:message key="key" /></span>
					</clay:content-col>
				</clay:content-row>

				<clay:content-row
					containerElement="div"
					cssClass=""
				>
					<clay:content-col expand="true">
						<c:choose>
							<c:when test="<%= Validator.isNotNull(editDisplayContext.getKey()) %>">
								<aui:input name="key" type="hidden" value="<%= editDisplayContext.getKey() %>" />

								<span><%= editDisplayContext.getKey() %></span>
							</c:when>
							<c:otherwise>
								<aui:input name="key" pattern="[^ ]+" required="<%= true %>" value="<%= editDisplayContext.getKey() %>" />
							</c:otherwise>
						</c:choose>
					</clay:content-col>
				</clay:content-row>

				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<span class="heading-text"><liferay-ui:message key="translation-override" /></span>
					</clay:content-col>
				</clay:content-row>

				<clay:content-row
					containerElement="div"
				>
					<clay:content-col
						expand="<%= true %>"
					>

						<%
						LocalizedValuesMap valuesLocalizedValuesMap = editDisplayContext.getValuesLocalizedValuesMap();
						LocalizedValuesMap originalValuesLocalizedValuesMap = editDisplayContext.getOriginalValuesLocalizedValuesMap();

						for (Locale availableLocale : editDisplayContext.getAvailableLocales()) {
							String languageId = LanguageUtil.getLanguageId(availableLocale);

							String name = liferayPortletResponse.getNamespace() + "_value_" + availableLocale;

							String value = valuesLocalizedValuesMap.get(availableLocale);
							String originalValue = originalValuesLocalizedValuesMap.get(availableLocale);
						%>

							<div class="form-group">
								<div class="form-group-item">
									<aui:input
										helpMessage="foo-bar"
										name="<%= name %>"
										label="<%= TextFormatter.format(languageId, TextFormatter.O) %>"
										value="<%= value %>"
									/>

								<c:if test="<%= editDisplayContext.isShowOriginalValues() %>">
									<div class="form-group-item-label">
										<span class="text-secondary"><strong>Original value:</strong> <%= originalValue %></span>
									</div>
								</c:if>
								</div>

							</div>

						<%
						}
						%>

						<liferay-ui:input-localized
							helpMessage="overrideTranslations"
							ignoreRequestValue="<%= true %>"
							name="value"
							selectedLanguageId="<%= editDisplayContext.getSelectedLanguage() %>"
							xml='<%= LocalizationUtil.getXml(editDisplayContext.getValuesLocalizedValuesMap(), "value") %>'
						/>
					</clay:content-col>
				</clay:content-row>
			</clay:sheet-section>
		</liferay-frontend:edit-form-body>

		<liferay-frontend:edit-form-footer>
			<aui:button name="save" type="submit" />
			<aui:button href="<%= editDisplayContext.getBackURL() %>" type="cancel" />
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>