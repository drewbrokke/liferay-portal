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

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADConstants;
import com.liferay.user.associated.data.web.internal.constants.UADWebKeys;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;
import com.liferay.user.associated.data.web.internal.display.UADInfoPanelDisplay;
import com.liferay.user.associated.data.web.internal.display.ViewUADEntitiesDisplay;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;
import com.liferay.user.associated.data.web.internal.search.UADHierarchyResultRowSplitter;
import com.liferay.user.associated.data.web.internal.util.GroupUtil;
import com.liferay.user.associated.data.web.internal.util.SelectedUserHelper;
import com.liferay.user.associated.data.web.internal.util.UADApplicationSummaryHelper;
import com.liferay.user.associated.data.web.internal.util.UADSearchContainerBuilder;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/view_uad_hierarchy"
	},
	service = MVCRenderCommand.class
)
public class ViewUADHierarchyMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ViewUADEntitiesDisplay viewUADEntitiesDisplay =
				new ViewUADEntitiesDisplay();

			String applicationKey = ParamUtil.getString(
				renderRequest, "applicationKey");

			viewUADEntitiesDisplay.setApplicationKey(applicationKey);

			viewUADEntitiesDisplay.setHierarchy(true);

			UADHierarchyDisplay uadHierarchyDisplay =
				_uadRegistry.getUADHierarchyDisplay(applicationKey);

			viewUADEntitiesDisplay.setResultRowSplitter(
				new UADHierarchyResultRowSplitter(
					LocaleThreadLocal.getThemeDisplayLocale(),
					uadHierarchyDisplay.getUADDisplays()));

			UADDisplay uadDisplay = _uadRegistry.getUADDisplay(
				ParamUtil.getString(renderRequest, "parentContainerClass"));

			viewUADEntitiesDisplay.setSearchContainer(
				_uadSearchContainerBuilder.getSearchContainer(
					renderRequest,
					_portal.getLiferayPortletResponse(renderResponse),
					applicationKey,
					PortletURLUtil.getCurrent(renderRequest, renderResponse),
					GroupUtil.getGroupIds(
						_groupLocalService,
						_selectedUserHelper.getSelectedUser(renderRequest),
						ParamUtil.getString(
							renderRequest, "scope",
							UADConstants.SCOPE_PERSONAL_SITE)),
					uadDisplay.getTypeClass(),
					ParamUtil.getLong(renderRequest, "parentContainerId"),
					_selectedUserHelper.getSelectedUser(renderRequest),
					uadHierarchyDisplay));

			viewUADEntitiesDisplay.setTypeClasses(
				uadHierarchyDisplay.getTypeClasses());

			renderRequest.setAttribute(
				UADWebKeys.UAD_HIERARCHY_DISPLAY, uadHierarchyDisplay);

			UADInfoPanelDisplay uadInfoPanelDisplay = new UADInfoPanelDisplay();

			uadInfoPanelDisplay.setHierarchyView(true);
			uadInfoPanelDisplay.setTopLevelView(false);
			uadInfoPanelDisplay.setUADDisplay(uadDisplay);

			renderRequest.setAttribute(
				UADWebKeys.UAD_INFO_PANEL_DISPLAY, uadInfoPanelDisplay);

			renderRequest.setAttribute(
				UADWebKeys.VIEW_UAD_ENTITIES_DISPLAY, viewUADEntitiesDisplay);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		return "/view_uad_hierarchy.jsp";
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SelectedUserHelper _selectedUserHelper;

	@Reference
	private UADApplicationSummaryHelper _uadApplicationSummaryHelper;

	@Reference
	private UADRegistry _uadRegistry;

	@Reference
	private UADSearchContainerBuilder _uadSearchContainerBuilder;

}