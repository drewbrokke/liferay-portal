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

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;
import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewStateFactory;
import com.liferay.petra.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.RenderURL;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Neil Griffin
 */
public abstract class BaseMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long parentFolderId = ParamUtil.getLong(
			renderRequest, "parentFolderId");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainerViewState searchContainerViewState =
			searchContainerViewStateFactory.create(
				"descriptive", "all", "title", "asc", renderRequest,
				new String[] {"createDate", "title"});

		prepareViewCommon(
			parentFolderId, themeDisplay.getPortletDisplay(), renderRequest,
			renderResponse, searchContainerViewState);

		PortletConfig portletConfig = (PortletConfig)renderRequest.getAttribute(
			"javax.portlet.config");

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(
			renderRequest.getLocale());

		return prepareView(
			parentFolderId, renderRequest, renderResponse, resourceBundle,
			searchContainerViewState, themeDisplay);
	}

	protected boolean isViewCancelable() {
		return true;
	}

	protected abstract String prepareView(
			long parentFolderId, RenderRequest renderRequest,
			RenderResponse renderResponse, ResourceBundle resourceBundle,
			SearchContainerViewState searchContainerViewState,
			ThemeDisplay themeDisplay)
		throws PortletException;

	protected void prepareViewCommon(
		long parentFolderId, PortletDisplay portletDisplay,
		RenderRequest renderRequest, RenderResponse renderResponse,
		SearchContainerViewState searchContainerViewState) {

		renderRequest.setAttribute(
			"searchContainerViewState", searchContainerViewState);

		if (isViewCancelable()) {
			RenderURL cancelURL = RenderURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"~bookmarks~view_folder"
			).setNavigation(
				searchContainerViewState.getNavigation()
			).setParameter(
				"bookmarkId", parentFolderId
			).setParameter(
				"categoryId", searchContainerViewState.getCategoryId(), false
			).setParameter(
				"cur", searchContainerViewState.getCur(), false
			).setParameter(
				"delta", searchContainerViewState.getDelta(), false
			).setParameter(
				"displayStyle", searchContainerViewState.getDisplayStyle(),
				false
			).setParameter(
				"orderByCol", searchContainerViewState.getOrderByCol(), false
			).setParameter(
				"orderByType", searchContainerViewState.getOrderByType(), false
			).setParameter(
				"resetCur", searchContainerViewState.getResetCur(), false
			).setParameter(
				"tag", searchContainerViewState.getTag(), false
			).buildRenderURL();

			renderRequest.setAttribute("cancelURL", cancelURL);

			portletDisplay.setURLBack(cancelURL.toString());

			portletDisplay.setShowBackIcon(true);
		}
	}

	@Reference
	protected SearchContainerViewStateFactory searchContainerViewStateFactory;

}