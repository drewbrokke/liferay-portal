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

BookmarksFolder folder = null;

if (row != null) {
	folder = (BookmarksFolder)row.getObject();
}
else {
	folder = (BookmarksFolder)request.getAttribute("info_panel.jsp-folder");
}

String modelResource = null;
String modelResourceDescription = null;
String resourcePrimKey = null;

boolean showPermissionsURL = false;

if (folder != null) {
	modelResource = BookmarksFolder.class.getName();
	modelResourceDescription = folder.getName();
	resourcePrimKey = String.valueOf(folder.getFolderId());

	showPermissionsURL = BookmarksFolderPermission.contains(permissionChecker, folder, ActionKeys.PERMISSIONS);
}
else {
	modelResource = "com.liferay.bookmarks";
	modelResourceDescription = themeDisplay.getScopeGroupName();
	resourcePrimKey = String.valueOf(scopeGroupId);

	showPermissionsURL = GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.PERMISSIONS);
}

boolean view = false;

if (row == null) {
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
	<c:if test="<%= (folder != null) && BookmarksFolderPermission.contains(permissionChecker, folder, ActionKeys.UPDATE) %>">
		<liferay-ui:icon
			message="edit"
			url='<%=
				RenderURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					"~bookmarks~edit_folder"
				).setNavigation(
					searchContainerViewState.getNavigation()
				).setParameter(
					"bookmarkId", folder.getFolderId(), false
				).setParameter(
					"parentFolderId", folder.getParentFolderId(), false
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
				).setParameter(
					"mergeWithParentFolderDisabled", row == null
				).buildString()
			%>'
		/>

		<portlet:renderURL var="moveURL">
			<!-- MVC parameters -->
			<portlet:param name="mvcRenderCommandName" value="~bookmarks~move_folder" />
			<portlet:param name="bookmarkId" value="<%= String.valueOf(folder.getFolderId()) %>" />
			<portlet:param name="parentFolderId" value="<%= String.valueOf(folder.getParentFolderId()) %>" />
			<portlet:param name="rowIdsBookmarksFolder" value="<%= String.valueOf(folder.getFolderId()) %>" />
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

	<c:if test="<%= showPermissionsURL %>">
		<liferay-security:permissionsURL
			modelResource="<%= modelResource %>"
			modelResourceDescription="<%= HtmlUtil.escape(modelResourceDescription) %>"
			resourcePrimKey="<%= resourcePrimKey %>"
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

	<c:if test="<%= (folder != null) && BookmarksFolderPermission.contains(permissionChecker, folder, ActionKeys.DELETE) %>">
		<portlet:actionURL name="~bookmarks~edit_folder" var="deleteURL">
			<!-- MVC parameters -->
			<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(scopeGroupId) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
			<portlet:param name="bookmarkId" value="<%= String.valueOf(folder.getFolderId()) %>" />
			<portlet:param name="parentFolderId" value="<%= String.valueOf(folder.getParentFolderId()) %>" />
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

	<%
	boolean bookmarksAdmin = portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN);
	boolean hasExportImportPortletInfoPermission = GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
	boolean inStagingGroup = stagingGroupHelper.isStagingGroup(scopeGroupId);
	boolean portletStaged = stagingGroupHelper.isStagedPortlet(scopeGroupId, BookmarksPortletKeys.BOOKMARKS);
	%>

	<c:if test="<%= (folder != null) && hasExportImportPortletInfoPermission && bookmarksAdmin && inStagingGroup && portletStaged %>">
		<portlet:actionURL name="/bookmarks/publish_folder" var="publishFolderURL">
			<portlet:param name="folderId" value="<%= String.valueOf(folder.getFolderId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			confirmation="are-you-sure-you-want-to-publish-the-selected-folder"
			message="publish-to-live"
			url="<%= publishFolderURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>