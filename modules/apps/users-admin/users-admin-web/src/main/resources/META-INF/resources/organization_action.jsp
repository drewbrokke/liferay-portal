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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

Organization rowOrganization = (Organization)row.getObject();

long rowOrganizationId = rowOrganization.getOrganizationId();

Group rowOrganizationGroup = rowOrganization.getGroup();

long rowOrganizationGroupId = rowOrganization.getGroupId();

UsersAdminPermissionsUtil usersAdminPermissionsUtil = new UsersAdminPermissionsUtil(permissionChecker);
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>

	<%
	boolean hasUpdatePermission = OrganizationPermissionUtil.contains(permissionChecker, rowOrganization, ActionKeys.UPDATE);
	%>

	<c:if test="<%= hasUpdatePermission %>">
		<portlet:renderURL var="editOrganizationURL">
			<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_organization" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="organizationId" value="<%= String.valueOf(rowOrganizationId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="edit"
			url="<%= editOrganizationURL %>"
		/>
	</c:if>

	<%--<c:if test="<%= OrganizationPermissionUtil.contains(permissionChecker, rowOrganization, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= Organization.class.getName() %>"
			modelResourceDescription="<%= HtmlUtil.escape(rowOrganization.getName()) %>"
			resourcePrimKey="<%= String.valueOf(rowOrganization.getOrganizationId()) %>"
			var="editOrganizationPermissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			message="permissions"
			method="get"
			url="<%= editOrganizationPermissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>--%>

	<%
	boolean showManageSiteAction = rowOrganizationGroup.isSite() && (GroupPermissionUtil.contains(permissionChecker, rowOrganizationGroup, ActionKeys.MANAGE_STAGING) || hasUpdatePermission);
	%>

	<c:if test="<%= showManageSiteAction %>">

		<%
		PortletURL editSettingsURL = PortletProviderUtil.getPortletURL(request, rowOrganizationGroup, Group.class.getName(), PortletProvider.Action.EDIT);

		editSettingsURL.setParameter("viewOrganizationsRedirect", currentURL);
		%>

		<liferay-ui:icon
			message="manage-site"
			url="<%= editSettingsURL.toString() %>"
		/>
	</c:if>

	<%
	boolean showAssignOrganizationRolesAction = permissionChecker.isGroupOwner(rowOrganizationGroupId) || OrganizationPermissionUtil.contains(permissionChecker, rowOrganization, ActionKeys.ASSIGN_USER_ROLES);
	%>

	<c:if test="<%= showAssignOrganizationRolesAction %>">

		<%
		PortletURL assignUserRolesURL = PortletProviderUtil.getPortletURL(request, UserGroupRole.class.getName(), PortletProvider.Action.EDIT);

		assignUserRolesURL.setParameter("className", User.class.getName());
		assignUserRolesURL.setParameter("groupId", String.valueOf(rowOrganizationGroupId));
		assignUserRolesURL.setWindowState(LiferayWindowState.POP_UP);
		%>

		<liferay-ui:icon
			message="assign-organization-roles"
			url="<%= assignUserRolesURL.toString() %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<c:if test="<%= usersAdminPermissionsUtil.showAssignMembersAction(rowOrganizationId) %>">

		<%
		String taglibOnClick = renderResponse.getNamespace() + "openSelectUsersDialog('" + rowOrganizationId + "');";
		%>

		<liferay-ui:icon
			message="assign-users"
			onClick="<%= taglibOnClick %>"
			url="javascript:;"
		/>
	</c:if>

	<c:if test="<%= usersAdminPermissionsUtil.showAddOrganizationUserAction(rowOrganizationId) %>">

		<%
		String parentOrganizationViewUsersURL = UsersAdminPortletURLUtil.createParentOrganizationViewURL(rowOrganizationId, PortalUtil.getLiferayPortletRequest(renderRequest), PortalUtil.getLiferayPortletResponse(renderResponse), "view-all-users");
		%>

		<portlet:renderURL var="addUserURL">
			<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_user" />
			<portlet:param name="backURL" value="<%= parentOrganizationViewUsersURL %>" />
			<portlet:param name="organizationsSearchContainerPrimaryKeys" value="<%= String.valueOf(rowOrganizationId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="add-user"
			url="<%= addUserURL %>"
		/>
	</c:if>

	<%
	String parentOrganizationViewSuborganizationsURL = UsersAdminPortletURLUtil.createParentOrganizationViewURL(rowOrganizationId, PortalUtil.getLiferayPortletRequest(renderRequest), PortalUtil.getLiferayPortletResponse(renderResponse), "view-all-organizations");
	%>

	<c:if test="<%= rowOrganization.isParentable() %>">

		<%
		String[] childrenTypes = rowOrganization.getChildrenTypes();

		for (String childrenType : childrenTypes) {
		%>

			<c:if test="<%= usersAdminPermissionsUtil.showAddOrganizationAction(rowOrganizationId) %>">
				<portlet:renderURL var="addSuborganizationURL">
					<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_organization" />
					<portlet:param name="redirect" value="<%= parentOrganizationViewSuborganizationsURL %>" />
					<portlet:param name="parentOrganizationSearchContainerPrimaryKeys" value="<%= String.valueOf(rowOrganizationId) %>" />
					<portlet:param name="type" value="<%= childrenType %>" />
				</portlet:renderURL>

				<liferay-ui:icon
					message='<%= LanguageUtil.format(request, "add-x", childrenType) %>'
					url="<%= addSuborganizationURL %>"
				/>
			</c:if>

		<%
		}
		%>

	</c:if>

	<%
	boolean hasDeletePermission = OrganizationPermissionUtil.contains(permissionChecker, rowOrganization, ActionKeys.DELETE);
	%>

	<c:if test="<%= hasDeletePermission %>">

		<%
		String taglibDeleteURL = "javascript:" + renderResponse.getNamespace() + "deleteOrganization('" + rowOrganizationId + "', '" + parentOrganizationViewSuborganizationsURL + "', '" + currentURL + "');";
		%>

		<liferay-ui:icon
			cssClass="item-remove"
			message="delete"
			url="<%= taglibDeleteURL %>"
		/>
	</c:if>

	<%
	long organizationId = ParamUtil.getLong(request, "organizationId", 0);
	%>

	<c:if test="<%= usersAdminPermissionsUtil.showRemoveOrganizationAction(rowOrganizationId, organizationId) %>">
				<portlet:actionURL name="/users_admin/edit_organization_assignments" var="removeOrganizationURL">
			<portlet:param name="assignmentsRedirect" value="<%= currentURL %>" />
			<portlet:param name="organizationId" value="<%= String.valueOf(organizationId) %>" />
			<portlet:param name="removeOrganizationIds" value="<%= String.valueOf(rowOrganizationId) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			confirmation="are-you-sure-you-want-to-remove-this"
			message="remove"
			url="<%= removeOrganizationURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>