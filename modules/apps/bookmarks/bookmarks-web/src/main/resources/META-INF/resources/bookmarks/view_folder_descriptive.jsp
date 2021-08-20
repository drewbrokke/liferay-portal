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

BookmarksFolder folder = (BookmarksFolder)row.getObject();

folder = folder.toEscapedModel();
%>

<portlet:renderURL var="rowURL">
	<!-- MVC parameters -->
	<portlet:param name="bookmarkId" value="<%= String.valueOf(folder.getFolderId()) %>" />
	<portlet:param name="mvcRenderCommandName" value="~bookmarks~view_folder" />
	<portlet:param name="parentFolderId" value="<%= String.valueOf(folder.getParentFolderId()) %>" />
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

<h4>
	<aui:a href="${rowURL}">
		<%= folder.getName() %>
	</aui:a>
</h4>

<h5 class="text-default">
	<%= folder.getDescription() %>
</h5>

<%
int foldersCount = BookmarksFolderServiceUtil.getFoldersCount(scopeGroupId, folder.getFolderId());
int entriesCount = BookmarksEntryServiceUtil.getEntriesCount(scopeGroupId, folder.getFolderId());
%>

<span class="h6">
	<liferay-ui:message arguments="<%= foldersCount %>" key='<%= (foldersCount == 1) ? "x-folder" : "x-folders" %>' />
</span>
<span class="h6">
	<liferay-ui:message arguments="<%= entriesCount %>" key='<%= (entriesCount == 1) ? "x-bookmark" : "x-bookmarks" %>' />
</span>