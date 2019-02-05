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
ViewUADApplicationsSummaryDisplay viewUADApplicationsSummaryDisplay = (ViewUADApplicationsSummaryDisplay)request.getAttribute(UADWebKeys.VIEW_UAD_APPLICATIONS_SUMMARY_DISPLAY);

SearchContainer<UADApplicationSummaryDisplay> uadNonreviewableSummaryDisplaySearchContainer = viewUADApplicationsSummaryDisplay.getSearchContainer();

UADNonreviewableSummaryManagementToolbarDisplayContext uadNonreviewableSummaryManagementToolbarDisplayContext = new UADNonreviewableSummaryManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, request, uadNonreviewableSummaryDisplaySearchContainer);

portletDisplay.setShowBackIcon(true);

PortletURL backURL = renderResponse.createRenderURL();

backURL.setParameter("mvcRenderCommandName", "/view_uad_summary");
backURL.setParameter("p_u_i_d", String.valueOf(selectedUser.getUserId()));

portletDisplay.setURLBack(backURL.toString());

renderResponse.setTitle(StringBundler.concat(selectedUser.getFullName(), " - ", LanguageUtil.get(request, "personal-data-erasure")));

String statusLabelDone = StringUtil.toUpperCase(LanguageUtil.get(request, "done"), locale);
String statusLabelPending = StringUtil.toUpperCase(LanguageUtil.get(request, "pending"), locale);
%>

<div class="container-fluid container-fluid-max-xl container-form-lg">
	<aui:form method="post" name="viewUADNonreviewableSummaryFm">
		<aui:input name="redirect" type="hidden" value="<%= backURL.toString() %>" />
		<aui:input name="p_u_i_d" type="hidden" value="<%= String.valueOf(selectedUser.getUserId()) %>" />

		<div class="sheet sheet-lg">
			<div class="sheet-header">
				<h2 class="sheet-title"><liferay-ui:message key="process-automatically-deletable-data" /></h2>
			</div>

			<div class="sheet-section">
				<h3 class="sheet-subtitle">
					<liferay-ui:message key="status-summary" />
				</h3>

				<div class="autofit-row autofit-row-center">
					<div class="autofit-col autofit-col-expand">
						<div class="autofit-section">
							<strong><liferay-ui:message key="remaining-items" />: </strong><%= viewUADApplicationsSummaryDisplay.getTotalCount() %>
						</div>
					</div>

					<div class="autofit-col">
						<portlet:actionURL name="/delete_nonreviewable_uad" var="deleteURL" />

						<aui:button cssClass="btn-sm" onClick='<%= renderResponse.getNamespace() + "confirmAction('viewUADNonreviewableSummaryFm', '" + deleteURL.toString() + "', '" + UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-anonymize-the-users-personal-data") + "')" %>' primary="true" value="delete" />
					</div>
				</div>
			</div>

			<div class="sheet-section">
				<h3 class="sheet-subtitle"><liferay-ui:message key="applications" /></h3>

				<clay:management-toolbar
					displayContext="<%= uadNonreviewableSummaryManagementToolbarDisplayContext %>"
				/>

				<liferay-ui:search-container
					searchContainer="<%= uadNonreviewableSummaryDisplaySearchContainer %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.user.associated.data.web.internal.display.UADApplicationSummaryDisplay"
						escapedModel="<%= true %>"
						keyProperty="key"
						modelVar="uadNonreviewableSummaryDisplay"
					>
						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand table-list-title"
							name="name"
							value="<%= UADLanguageUtil.getApplicationName(uadNonreviewableSummaryDisplay.getApplicationKey(), locale) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="items"
							property="count"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="status"
						>
							<clay:label
								label="<%= uadNonreviewableSummaryDisplay.hasItems() ? statusLabelPending : statusLabelDone %>"
								style='<%= uadNonreviewableSummaryDisplay.hasItems() ? "warning" : "success" %>'
							/>
						</liferay-ui:search-container-column-text>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</div>
		</div>
	</aui:form>
</div>

<%@ include file="/action/confirm_action_js.jspf" %>