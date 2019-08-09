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
AccountsAdminPortletDisplayContext accountsAdminPortletDisplayContext = new AccountsAdminPortletDisplayContext(liferayPortletRequest, liferayPortletResponse);

ViewAccountsManagementToolbarDisplayContext viewAccountsManagementToolbarDisplayContext = new ViewAccountsManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, request, accountsAdminPortletDisplayContext.getAccountsSearchContainer());
%>

<clay:management-toolbar
	displayContext="<%= viewAccountsManagementToolbarDisplayContext %>"
/>

<div class="container-fluid-1280">
	<liferay-ui:search-container
		searchContainer="<%= accountsAdminPortletDisplayContext.getAccountsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.model.AccountEntry"
			keyProperty="accountEntryId"
			modelVar="accountEntry"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				name="name"
				property="name"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="parent-account"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="website"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="account-owner"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="active"
				value='<%= LanguageUtil.get(request, Objects.equals(WorkflowConstants.STATUS_APPROVED, accountEntry.getStatus()) ? "yes" : "no") %>'
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>