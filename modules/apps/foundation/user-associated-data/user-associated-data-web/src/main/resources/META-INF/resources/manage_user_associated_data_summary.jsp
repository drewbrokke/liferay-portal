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
User selUser = PortalUtil.getSelectedUser(request);

selUserId = selUser.getUserId();

Boolean deactivated = !selUser.isActive();

Integer selUserPrivateLayoutsPageCount = selUser.getPrivateLayoutsPageCount();

Integer selUserPublicLayoutsPageCount = selUser.getPublicLayoutsPageCount();

Boolean forgotten = (selUserPrivateLayoutsPageCount + selUserPublicLayoutsPageCount) == 0;

Boolean reviewable = deactivated && forgotten;
%>

<div class="container-fluid-1280">
	<liferay-ui:header title="personal-data-erasure" />

	<liferay-ui:panel-container extended="<%= true %>" persistState="<%= true %>">
		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="stepOnePanel" persistState="<%= true %>" title="step-one-title">
			<div>
				<liferay-ui:message key="step-one-description" />
			</div>

			<div>
				<portlet:actionURL name="/user_associated_data/deactivate_user" var="deactivateUserURL">
					<portlet:param name="mvcActionCommandName" value="/user_associated_data/deactivate_user" />
					<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
					<portlet:param name="redirect" value="<%= currentURLObj.toString() %>" />
				</portlet:actionURL>

				<aui:button disabled="<%= deactivated %>" onClick="<%= deactivateUserURL %>" value="deactivate-user" />

				<c:if test="<%= deactivated %>">
					<liferay-ui:icon iconCssClass="icon-ok-sign" label="<%= true %>" message="user-successfully-deactivated" />
				</c:if>
			</div>
		</liferay-ui:panel>

		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="stepTwoPanel" persistState="<%= true %>" title="step-two-title">
			<div>
				<liferay-ui:message key="step-two-description" />

				<c:if test="<%= !forgotten %>">

					<%
					Group selUserGroup = selUser.getGroup();
					%>

					<c:if test="<%= selUserPublicLayoutsPageCount > 0 %>">
						<liferay-ui:icon
							label="<%= true %>"
							message="open-profile-pages"
							method="get"
							target="_blank"
							url="<%= selUserGroup.getDisplayURL(themeDisplay, false) %>"
						/>
					</c:if>

					<c:if test="<%= selUserPrivateLayoutsPageCount > 0 %>">
						<liferay-ui:icon
							label="<%= true %>"
							message="open-dashboard-pages"
							method="get"
							target="_blank"
							url="<%= selUserGroup.getDisplayURL(themeDisplay, true) %>"
						/>
					</c:if>
				</c:if>
			</div>

			<div>
				<portlet:actionURL name="/user_associated_data/forget_personal_site" var="forgetPersonalSiteURL">
					<portlet:param name="mvcActionCommandName" value="/user_associated_data/forget_personal_site" />
					<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
					<portlet:param name="redirect" value="<%= currentURLObj.toString() %>" />
				</portlet:actionURL>

				<aui:button disabled="<%= !deactivated || forgotten %>" onClick="<%= forgetPersonalSiteURL %>" value="delete-personal-site" />

				<c:if test="<%= forgotten %>">
					<liferay-ui:icon iconCssClass="icon-ok-sign" label="<%= true %>" message="personal-site-successfully-forgotten" />
				</c:if>
			</div>
		</liferay-ui:panel>

		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="stepThreePanel" persistState="<%= true %>" title="step-three-title">
			<div>
				<liferay-ui:message key="step-three-description" />
			</div>

			<div>
				<portlet:renderURL var="manageUserAssociatedDataEntitiesURL">
					<portlet:param name="mvcRenderCommandName" value="/user_associated_data/manage_user_associated_data_entity_sets" />
					<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
				</portlet:renderURL>

				<aui:button disabled="<%= !reviewable %>" onClick="<%= manageUserAssociatedDataEntitiesURL %>" value="review" />
			</div>
		</liferay-ui:panel>

		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="stepFourPanel" persistState="<%= true %>" title="step-four-title">
			<div>
				<liferay-ui:message key="step-four-description" />
			</div>

			<div>
				<portlet:actionURL name="/user_associated_data/delete_remaining_user_associated_data" var="deleteRemainingUserAssociatedDataURL">
					<portlet:param name="mvcActionCommandName" value="/user_associated_data/delete_remaining_user_associated_data" />
					<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
					<portlet:param name="redirect" value="<%= currentURLObj.toString() %>" />
				</portlet:actionURL>

				<aui:button disabled="<%= true %>" onClick="<%= deleteRemainingUserAssociatedDataURL %>" value="anonymize-data" />
			</div>
		</liferay-ui:panel>

		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="stepFivePanel" persistState="<%= true %>" title="step-five-title">
			<div>
				<liferay-ui:message key="step-five-description" />
			</div>

			<div>
				<portlet:actionURL name="/user_associated_data/delete_user" var="deleteUserURL">
					<portlet:param name="mvcActionCommandName" value="/user_associated_data/delete_user" />
					<portlet:param name="selUserId" value="<%= String.valueOf(selUserId) %>" />
					<portlet:param name="redirect" value="<%= currentURLObj.toString() %>" />
				</portlet:actionURL>

				<aui:button disabled="<%= true %>" onClick="<%= deleteUserURL %>" value="delete-user" />
			</div>
		</liferay-ui:panel>
	</liferay-ui:panel-container>
</div>