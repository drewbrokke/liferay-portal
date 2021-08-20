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

<%@ include file="/bookmarks/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

BookmarksEntry entry = null;

boolean view = false;

if (row != null) {
	Object result = row.getObject();

	if (result instanceof AssetEntry) {
		AssetEntry assetEntry = (AssetEntry)result;

		entry = BookmarksEntryServiceUtil.getEntry(assetEntry.getClassPK());
	}
	else {
		entry = (BookmarksEntry)result;
	}
}
else {
	entry = (BookmarksEntry)request.getAttribute("info_panel.jsp-entry");

	view = true;
}
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="actions"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.UPDATE) %>">
		<liferay-ui:icon
			message="edit"
			url='<%=
				RenderURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					"~bookmarks~edit_entry"
				).setNavigation(
					searchContainerViewState.getNavigation()
				).setParameter(
					"bookmarkId", entry.getEntryId(), false
				).setParameter(
					"parentFolderId", entry.getFolderId(), false
				).setParameter(
					"categoryId", searchContainerViewState.getCategoryId(), false
				).setParameter(
					"cur", searchContainerViewState.getCur(), false
				).setParameter(
					"delta", searchContainerViewState.getDelta(), false
				).setParameter(
					"displayStyle", searchContainerViewState.getDisplayStyle(), false
				).setParameter(
					"orderByCol", searchContainerViewState.getOrderByCol(), false
				).setParameter(
					"orderByType", searchContainerViewState.getOrderByType(), false
				).setParameter(
					"resetCur", searchContainerViewState.getResetCur(), false
				).setParameter(
					"tag", searchContainerViewState.getTag(), false
				).buildString()
			%>'
		/>

		<portlet:renderURL var="moveURL">
			<!-- MVC parameters -->
			<portlet:param name="mvcRenderCommandName" value="~bookmarks~move_entry" />
			<portlet:param name="bookmarkId" value="<%= String.valueOf(entry.getEntryId()) %>" />
			<portlet:param name="parentFolderId" value="<%= String.valueOf(entry.getFolderId()) %>" />
			<!-- SearchContainer view state -->
			<portlet:param name="categoryId" value="${searchContainerViewState.categoryId}" />
			<portlet:param name="cur" value="${searchContainerViewState.cur}" />
			<portlet:param name="delta" value="${searchContainerViewState.delta}" />
			<portlet:param name="displayStyle" value="${searchContainerViewState.displayStyle}" />
			<portlet:param name="navigation" value="${searchContainerViewState.navigation}" />
			<portlet:param name="orderByCol" value="${searchContainerViewState.orderByCol}" />
			<portlet:param name="orderByType" value="${searchContainerViewState.orderByType}" />
			<portlet:param name="resetCur" value="${searchContainerViewState.resetCur}" />
			<portlet:param name="tag" value="${searchContainerViewState.tag}" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="move"
			url="<%= moveURL %>"
		/>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= BookmarksEntry.class.getName() %>"
			modelResourceDescription="<%= entry.getName() %>"
			resourcePrimKey="<%= String.valueOf(entry.getEntryId()) %>"
			var="permissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			message="permissions"
			method="get"
			url="<%= permissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.SUBSCRIBE) && (bookmarksGroupServiceOverriddenConfiguration.emailEntryAddedEnabled() || bookmarksGroupServiceOverriddenConfiguration.emailEntryUpdatedEnabled()) %>">
		<c:choose>
			<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), BookmarksEntry.class.getName(), entry.getEntryId()) %>">
				<portlet:actionURL name="~bookmarks~edit_entry" var="unsubscribeURL">
					<!-- MVC parameters -->
					<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
					<portlet:param name="bookmarkId" value="<%= String.valueOf(entry.getEntryId()) %>" />
					<portlet:param name="parentFolderId" value="<%= String.valueOf(entry.getFolderId()) %>" />
					<!-- SearchContainer view state -->
					<portlet:param name="categoryId" type="render" value="${searchContainerViewState.categoryId}" />
					<portlet:param name="cur" type="render" value="${searchContainerViewState.cur}" />
					<portlet:param name="delta" type="render" value="${searchContainerViewState.delta}" />
					<portlet:param name="displayStyle" type="render" value="${searchContainerViewState.displayStyle}" />
					<portlet:param name="navigation" type="render" value="${searchContainerViewState.navigation}" />
					<portlet:param name="orderByCol" type="render" value="${searchContainerViewState.orderByCol}" />
					<portlet:param name="orderByType" type="render" value="${searchContainerViewState.orderByType}" />
					<portlet:param name="resetCur" type="render" value="${searchContainerViewState.resetCur}" />
				</portlet:actionURL>

				<liferay-ui:icon
					message="unsubscribe"
					url="<%= unsubscribeURL %>"
				/>
			</c:when>
			<c:otherwise>
				<portlet:actionURL name="~bookmarks~edit_entry" var="subscribeURL">
					<!-- MVC parameters -->
					<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
					<portlet:param name="bookmarkId" value="<%= String.valueOf(entry.getEntryId()) %>" />
					<portlet:param name="parentFolderId" value="<%= String.valueOf(entry.getFolderId()) %>" />
					<!-- SearchContainer view state -->
					<portlet:param name="categoryId" type="render" value="${searchContainerViewState.categoryId}" />
					<portlet:param name="cur" type="render" value="${searchContainerViewState.cur}" />
					<portlet:param name="delta" type="render" value="${searchContainerViewState.delta}" />
					<portlet:param name="displayStyle" type="render" value="${searchContainerViewState.displayStyle}" />
					<portlet:param name="navigation" type="render" value="${searchContainerViewState.navigation}" />
					<portlet:param name="orderByCol" type="render" value="${searchContainerViewState.orderByCol}" />
					<portlet:param name="orderByType" type="render" value="${searchContainerViewState.orderByType}" />
					<portlet:param name="resetCur" type="render" value="${searchContainerViewState.resetCur}" />
				</portlet:actionURL>

				<liferay-ui:icon
					message="subscribe"
					url="<%= subscribeURL %>"
				/>
			</c:otherwise>
		</c:choose>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.DELETE) %>">
		<portlet:actionURL name="~bookmarks~edit_entry" var="deleteURL">
			<!-- MVC parameters -->
			<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(scopeGroupId) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
			<portlet:param name="bookmarkId" value="<%= String.valueOf(entry.getEntryId()) %>" />
			<portlet:param name="parentFolderId" value="<%= String.valueOf(entry.getFolderId()) %>" />
			<!-- SearchContainer view state -->
			<portlet:param name="categoryId" type="render" value="${searchContainerViewState.categoryId}" />
			<portlet:param name="cur" type="render" value="${searchContainerViewState.cur}" />
			<portlet:param name="delta" type="render" value="${searchContainerViewState.delta}" />
			<portlet:param name="displayStyle" type="render" value="${searchContainerViewState.displayStyle}" />
			<portlet:param name="navigation" type="render" value="${searchContainerViewState.navigation}" />
			<portlet:param name="orderByCol" type="render" value="${searchContainerViewState.orderByCol}" />
			<portlet:param name="orderByType" type="render" value="${searchContainerViewState.orderByType}" />
			<portlet:param name="resetCur" type="render" value="${searchContainerViewState.resetCur}" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			trash="<%= trashHelper.isTrashEnabled(scopeGroupId) %>"
			url="<%= deleteURL %>"
		/>
	</c:if>

	<c:if test="<%= portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN) %>">
		<liferay-export-import-changeset:publish-entity-menu-item
			className="<%= BookmarksEntry.class.getName() %>"
			groupId="<%= entry.getGroupId() %>"
			uuid="<%= entry.getUuid() %>"
		/>
	</c:if>
</liferay-ui:icon-menu>