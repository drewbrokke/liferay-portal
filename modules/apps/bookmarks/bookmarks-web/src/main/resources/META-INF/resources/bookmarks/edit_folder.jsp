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
String redirect = ParamUtil.getString(request, "redirect");

BookmarksFolder folder = (BookmarksFolder)request.getAttribute(BookmarksWebKeys.BOOKMARKS_FOLDER);

long bookmarkId = ParamUtil.get(request, "bookmarkId", BeanPropertiesUtil.getLong(folder, "folderId", BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID));

long parentFolderId = ParamUtil.get(request, "parentFolderId", BeanPropertiesUtil.getLong(folder, "folderId", BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID));

boolean mergeWithParentFolderDisabled = ParamUtil.getBoolean(request, "mergeWithParentFolderDisabled");

if (folder != null) {
	BookmarksUtil.addPortletBreadcrumbEntries(parentFolderId, renderRequest, renderResponse, searchContainerViewState);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
	}
}
else {
	if (parentFolderId > 0) {
		BookmarksUtil.addPortletBreadcrumbEntries(parentFolderId, renderRequest, renderResponse, searchContainerViewState);

		if (!layout.isTypeControlPanel()) {
			PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-subfolder"), currentURL);
		}
	}
	else if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-folder"), currentURL);
	}
}

String headerTitle = (folder == null) ? ((parentFolderId > 0) ? LanguageUtil.get(request, "add-subfolder") : LanguageUtil.get(request, "add-folder")) : LanguageUtil.format(request, "edit-x", folder.getName(), false);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(headerTitle);
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<portlet:actionURL copyCurrentRenderParameters="<%= true %>" name="~bookmarks~edit_folder" var="editFolderURL">
		<portlet:param name="mvcRenderCommandName" type="render" value="~bookmarks~view_folder" />
	</portlet:actionURL>

	<aui:form action="<%= editFolderURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveFolder();" %>'>
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="bookmarkId" type="hidden" value="<%= bookmarkId %>" />
		<aui:input name="parentFolderId" type="hidden" value="<%= parentFolderId %>" />

		<liferay-ui:error exception="<%= FolderNameException.class %>">
			<p>
				<liferay-ui:message arguments="<%= BookmarksFolderConstants.NAME_RESERVED_WORDS %>" key="the-folder-name-cannot-be-blank-or-a-reserved-word-such-as-x" />
			</p>

			<p>
				<liferay-ui:message arguments="<%= BookmarksFolderConstants.NAME_INVALID_CHARACTERS %>" key="the-folder-name-cannot-contain-the-following-invalid-characters-x" />
			</p>
		</liferay-ui:error>

		<aui:model-context bean="<%= folder %>" model="<%= BookmarksFolder.class %>" />

		<aui:fieldset-group markupView="lexicon">
			<aui:fieldset>
				<aui:input autoFocus="<%= windowState.equals(WindowState.MAXIMIZED) %>" name="name" />

				<aui:input name="description" />
			</aui:fieldset>

			<c:if test="<%= folder != null %>">
				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="parent-folder">

					<%
					String parentFolderName = LanguageUtil.get(request, "home");

					try {
						BookmarksFolder parentFolder = BookmarksFolderServiceUtil.getFolder(parentFolderId);

						parentFolderName = parentFolder.getName();
					}
					catch (NoSuchFolderException nsfe) {
					}
					%>

					<div class="form-group">
						<aui:input label="parent-folder" name="parentFolderName" type="resource" value="<%= parentFolderName %>" />

						<aui:button name="selectFolderButton" value="select" />

						<aui:script>
							var <portlet:namespace />selectFolderButton = document.getElementById(
								'<portlet:namespace />selectFolderButton'
							);

							if (<portlet:namespace />selectFolderButton) {
								<portlet:namespace />selectFolderButton.addEventListener(
									'click',
									(event) => {
										Liferay.Util.openSelectionModal({
											onSelect: function (event) {
												var folderData = {
													idString: 'parentFolderId',
													idValue: event.entityid,
													nameString: 'parentFolderName',
													nameValue: event.entityname,
												};

												Liferay.Util.selectFolder(
													folderData,
													'<portlet:namespace />'
												);
											},
											selectEventName: '<portlet:namespace />selectFolder',
											title:
												'<liferay-ui:message arguments="folder" key="select-x" />',
											url:
												'<liferay-portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="mvcRenderCommandName" value="~bookmarks~select_folder" /></liferay-portlet:renderURL>',
										});
									}
								);
							}
						</aui:script>

						<%
						String taglibRemoveFolder = "Liferay.Util.removeEntitySelection('parentFolderId', 'parentFolderName', this, '" + liferayPortletResponse.getNamespace() + "');";
						%>

						<aui:button disabled="<%= parentFolderId <= 0 %>" name="removeFolderButton" onClick="<%= taglibRemoveFolder %>" value="remove" />
					</div>

					<aui:input disabled="<%= mergeWithParentFolderDisabled %>" inlineLabel="right" label="merge-with-parent-folder" labelCssClass="simple-toggle-switch" name="mergeWithParentFolder" type="toggle-switch" />
				</aui:fieldset>
			</c:if>

			<liferay-expando:custom-attributes-available
				className="<%= BookmarksFolder.class.getName() %>"
			>
				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
					<liferay-expando:custom-attribute-list
						className="<%= BookmarksFolder.class.getName() %>"
						classPK="<%= (folder != null) ? folder.getFolderId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</aui:fieldset>
			</liferay-expando:custom-attributes-available>

			<c:if test="<%= folder == null %>">
				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
					<aui:field-wrapper label="permissions">
						<liferay-ui:input-permissions
							modelName="<%= BookmarksFolder.class.getName() %>"
						/>
					</aui:field-wrapper>
				</aui:fieldset>
			</c:if>

			<div class="sheet-footer">
				<aui:button type="submit" />

				<aui:button href="${cancelURL}" type="cancel" />
			</div>
		</aui:fieldset-group>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />saveFolder() {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var cmd = form.querySelector(
				'#<portlet:namespace /><%= Constants.CMD %>'
			);

			if (cmd) {
				cmd.setAttribute(
					'value',
					'<%= (folder == null) ? Constants.ADD : Constants.UPDATE %>'
				);

				submitForm(form);
			}
		}
	}
</aui:script>