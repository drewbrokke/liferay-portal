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

renderResponse.setTitle(editDisplayContext.getPageTitle());
%>

<portlet:actionURL name="editPortalLanguageOverride" var="editURL" />

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= editURL %>"
		name="editPortalLanguageOverrideFm"
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
			</clay:sheet-section>

			<clay:sheet-section>
				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<span class="heading-text"><liferay-ui:message key="translation-override" /></span>
					</clay:content-col>

					<clay:content-col>
						<span class="heading-end">
							<liferay-ui:icon
								cssClass="modify-link"
								id="clearOverridesButton"
								label="<%= true %>"
								linkCssClass="btn btn-secondary btn-sm"
								message="clear-all-overrides"
								url="javascript:;"
							/>
						</span>
					</clay:content-col>
				</clay:content-row>

				<clay:content-row>
					<div class="sheet-text">
						<liferay-ui:icon
							icon="info-circle"
							message="hello-world"
							localizeMessage="true"
							cssClass="text-info font-weight-normal"
						/>

						<span class="text-info font-weight-bold">
							<clay:icon symbol="info-circle" cssClass="mr-2" /><liferay-ui:message key="please-add-at-least-one-value-below" />
						</span>
					</div>
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
							String name = "value_" + availableLocale;
							String value = valuesLocalizedValuesMap.get(availableLocale);
						%>

							<div class="form-group">
								<aui:input
									wrapperCssClass="mb-0"
									name="<%= name %>"
									label="<%= TextFormatter.format(languageId, TextFormatter.O) %>"
									value="<%= value %>"
								/>

								<c:if test="<%= editDisplayContext.isShowOriginalValues() %>">
									<div class="form-feedback-group">
										<div class="form-text">

											<%
											String openTag = "<span class=\"font-weight-bold\">";
											String originalValue = originalValuesLocalizedValuesMap.get(availableLocale);
											%>

											<liferay-ui:message
												arguments='<%= new String[] {openTag, "</span>", HtmlUtil.escape(originalValue)} %>'
												key="x-original-value-x-x"
											/>
										</div>
									</div>
								</c:if>
							</div>


						<%
						}
						%>

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

<portlet:actionURL name="deletePortalLanguageOverrides" var="deletePortalLanguageOverridesURL">
	<portlet:param name="redirect" value="<%= editDisplayContext.getBackURL() %>" />
	<portlet:param name="key" value="<%= editDisplayContext.getKey() %>" />
</portlet:actionURL>

<aui:script sandbox="true">
	const clearAllOverridesButton = document.getElementById('<portlet:namespace />clearOverridesButton');

	clearAllOverridesButton.addEventListener(
		'click',
		function(event) {
			if (confirm('<liferay-ui:message key="are-you-sure-you-want-to-reset-all-translation-overrides" />')) {
				submitForm(
					document.getElementById('<portlet:namespace />editPortalLanguageOverrideFm'),
					'<%= HtmlUtil.escapeJS(deletePortalLanguageOverridesURL) %>');
			}
		}
	);
</aui:script>