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
User selUser = PortalUtil.getSelectedUser(request, false);

SearchContainer<AccountEntryDisplay> accountEntryDisplaySearchContainer = AccountEntryDisplaySearchContainerFactory.createWithUserId(selUser.getUserId(), liferayPortletRequest, liferayPortletResponse);

accountEntryDisplaySearchContainer.setRowChecker(null);
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="accounts"
/>

<portlet:actionURL name="/account_admin/edit_account_user_account_entries" var="editAccountUserAccountEntriesURL">
	<portlet:param name="accountUserId" value="<%= String.valueOf(selUser.getUserId()) %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/account_admin/update_account_memberships" />
<aui:input name="addAccountEntryIds" type="hidden" />
<aui:input name="deleteAccountEntryIds" type="hidden" />

<div id='<%= liferayPortletResponse.getNamespace() + "usersAdminAccountEntriesContainer" %>'>
<clay:content-row
	containerElement="h3"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="accounts" /></span>
	</clay:content-col>

	<clay:content-col>
		<span class="heading-end">
			<liferay-ui:icon
				cssClass="modify-link"
				id="selectAccountLink"
				label="<%= true %>"
				linkCssClass="btn btn-secondary btn-sm"
				message="select"
				url="javascript:;"
			/>
		</span>
	</clay:content-col>
</clay:content-row>

<liferay-util:buffer
	var="removeAccountEntryIcon"
>
	<liferay-ui:icon
		icon="times-circle"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="this-user-does-not-belong-to-any-accounts"
	headerNames="name,type,null"
	id="userAdminAccountEntrySearchContainer"
	searchContainer="<%= accountEntryDisplaySearchContainer %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.account.admin.web.internal.display.AccountEntryDisplay"
		keyProperty="accountEntryId"
		modelVar="accountEntryDisplay"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="name"
			property="name"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="type"
			property="type"
			translate="<%= true %>"
		/>

		<liferay-ui:search-container-column-text>
			<c:if test="<%= AccountEntryPermission.contains(permissionChecker, accountEntryDisplay.getAccountEntryId(), ActionKeys.MANAGE_USERS) %>">
				<a class="remove-link" data-rowId="<%= accountEntryDisplay.getAccountEntryId() %>" href="javascript:;"><%= removeAccountEntryIcon %></a>
			</c:if>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>
</div>

<liferay-portlet:renderURL portletName="<%= AccountPortletKeys.ACCOUNT_USERS_ADMIN %>" var="selectAccountEntriesRenderURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/account_users_admin/select_account_entry.jsp" />
	<portlet:param name="singleSelect" value="<%= Boolean.FALSE.toString() %>" />
	<portlet:param name="accountUserId" value="<%= String.valueOf(selUser.getUserId()) %>" />
</liferay-portlet:renderURL>

<liferay-frontend:component
	componentId="UsersAdminSelectAccountEventHandler"
	context='<%=
		HashMapBuilder.<String, Object>put(
			"addAccountEntryIdsInput", "#addAccountEntryIds"
		).put(
			"deleteAccountEntryIdsInput", "#deleteAccountEntryIds"
		).put(
			"container", "#usersAdminAccountEntriesContainer"
		).put(
			"modalButtonAddLabel", LanguageUtil.get(locale, "assign")
		).put(
			"modalTitle", LanguageUtil.format(locale, "select-x", "accounts")
		).put(
			"portletNamespace", liferayPortletResponse.getNamespace()
		).put(
			"removeButtonSelector", ".remove-link"
		).put(
			"removeUserIconMarkup", removeAccountEntryIcon
		).put(
			"searchContainer", "userAdminAccountEntrySearchContainer"
		).put(
			"selectButton", "#selectAccountLink"
		).put(
			"selectedData",
			accountEntryDisplaySearchContainer.getResults().stream().map(AccountEntryDisplay::getAccountEntryId).map(String::valueOf).toArray(String[]::new)
		).put(
			"selectEventName", "selectAccountEntry"
		).put(
			"selectMultiple", true
		).put(
			"selectURL", selectAccountEntriesRenderURL.toString()
		).put(
			"userIdInput", "#personAccountEntryUserId"
		).build()
	%>'
	module="dynamic_include/com.liferay.users.admin.web/user/membership/js/UsersAdminSelectAccountEventHandler.es"
/>

