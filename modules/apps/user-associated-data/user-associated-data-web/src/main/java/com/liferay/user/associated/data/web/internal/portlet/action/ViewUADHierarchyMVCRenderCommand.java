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

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADWebKeys;
import com.liferay.user.associated.data.web.internal.display.ContainerDisplay;
import com.liferay.user.associated.data.web.internal.display.UADEntity;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;
import com.liferay.user.associated.data.web.internal.display.ViewUADEntitiesDisplay;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;
import com.liferay.user.associated.data.web.internal.search.UADHierarchyResultRowSplitter;
import com.liferay.user.associated.data.web.internal.util.SafeDisplayValueUtil;
import com.liferay.user.associated.data.web.internal.util.SelectedUserHelper;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
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
			User selectedUser = _selectedUserHelper.getSelectedUser(
				renderRequest);

			long[] groupIds = null;

			String applicationKey = ParamUtil.getString(
				renderRequest, "applicationKey");

			UADHierarchyDisplay uadHierarchyDisplay =
				_uadRegistry.getUADHierarchyDisplay(applicationKey);

			ViewUADEntitiesDisplay viewUADEntitiesDisplay =
				new ViewUADEntitiesDisplay();

			viewUADEntitiesDisplay.setApplicationKey(applicationKey);

			LiferayPortletResponse liferayPortletResponse =
				_portal.getLiferayPortletResponse(renderResponse);

			PortletURL currentURL = PortletURLUtil.getCurrent(
				renderRequest, renderResponse);

			viewUADEntitiesDisplay.setHierarchical(true);
			viewUADEntitiesDisplay.setResultRowSplitter(
				new UADHierarchyResultRowSplitter(
					LocaleThreadLocal.getThemeDisplayLocale(),
					uadHierarchyDisplay.getUADDisplays()));
			viewUADEntitiesDisplay.setSearchContainer(
				_getSearchContainer(
					renderRequest, liferayPortletResponse, applicationKey,
					currentURL, groupIds, selectedUser, uadHierarchyDisplay));

			UADDisplay<?>[] uadDisplays = uadHierarchyDisplay.getUADDisplays();

			renderRequest.setAttribute(
				UADWebKeys.INFO_PANEL_UAD_DISPLAY, uadDisplays[0]);

			renderRequest.setAttribute(
				UADWebKeys.VIEW_UAD_ENTITIES_DISPLAY, viewUADEntitiesDisplay);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		return "/view_uad_hierarchy.jsp";
	}

	private <T> UADEntity<T> _constructUADEntity(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String applicationKey, T entity, List<Class> containerTypeClasses,
			long selectedUserId, UADDisplay<T> uadDisplay)
		throws Exception {

		String editURL = uadDisplay.getEditURL(
			entity, liferayPortletRequest, liferayPortletResponse);

		String viewURL = null;

		Class<?> typeClass = uadDisplay.getTypeClass();

		if (containerTypeClasses.contains(typeClass)) {
			PortletURL renderURL = liferayPortletResponse.createRenderURL();

			renderURL.setParameter("applicationKey", applicationKey);
			renderURL.setParameter(
				"mvcRenderCommandName", "/view_uad_hierarchy");
			renderURL.setParameter("parentContainerClass", typeClass.getName());
			renderURL.setParameter(
				"parentContainerId",
				String.valueOf(uadDisplay.getPrimaryKey(entity)));
			renderURL.setParameter("p_u_i_d", String.valueOf(selectedUserId));

			viewURL = renderURL.toString();
		}

		UADEntity<T> uadEntity = new UADEntity(
			entity, uadDisplay.getPrimaryKey(entity), editURL, viewURL);

		Map<String, Object> columnFieldValues = uadDisplay.getFieldValues(
			entity, uadDisplay.getColumnFieldNames());

		for (String columnFieldName : uadDisplay.getColumnFieldNames()) {
			uadEntity.addColumnEntry(
				columnFieldName,
				SafeDisplayValueUtil.get(
					columnFieldValues.get(columnFieldName)));
		}

		return uadEntity;
	}

	private SearchContainer<UADEntity> _getSearchContainer(
		RenderRequest renderRequest,
		LiferayPortletResponse liferayPortletResponse,
		String applicationKey, PortletURL currentURL, long[] groupIds,
		User selectedUser, UADHierarchyDisplay uadHierarchyDisplay)
		throws Exception {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		DisplayTerms displayTerms = new DisplayTerms(renderRequest);

		int cur = ParamUtil.getInteger(
			renderRequest, SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_CUR);

		SearchContainer<UADEntity> searchContainer = new SearchContainer<>(
			renderRequest, displayTerms, displayTerms,
			SearchContainer.DEFAULT_CUR_PARAM, cur,
			SearchContainer.DEFAULT_DELTA, currentURL, null,
			"no-entities-remain-of-this-type", null);

		searchContainer.setId("UADEntities");

		String orderByCol = ParamUtil.getString(
			renderRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM);

		String className = ParamUtil.getString(
			renderRequest, "parentContainerClass");

		UADDisplay uadDisplay = _uadRegistry.getUADDisplay(className);

		if (!ArrayUtil.contains(
			uadDisplay.getSortingFieldNames(), orderByCol)) {

			orderByCol = "modifiedDate";
		}

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			renderRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "asc");

		searchContainer.setOrderByType(orderByType);

		Map<String, String> orderableHeaders = new LinkedHashMap<>();

		for (String orderByColumn : uadDisplay.getSortingFieldNames()) {
			orderableHeaders.put(
				TextFormatter.format(orderByColumn, TextFormatter.K),
				orderByColumn);
		}

		searchContainer.setOrderableHeaders(orderableHeaders);

		try {
			Class<?> parentContainerClass = uadDisplay.getTypeClass();

			Serializable parentContainerId = ParamUtil.getLong(
				renderRequest, "parentContainerId");

			List entities = uadHierarchyDisplay.search(
				parentContainerClass, parentContainerId,
				selectedUser.getUserId(), groupIds, displayTerms.getKeywords(),
				searchContainer.getOrderByCol(),
				searchContainer.getOrderByType(), searchContainer.getStart(),
				searchContainer.getEnd());

			List<UADEntity> uadEntities = new ArrayList<>();

			for (Object entity : entities) {
				Object uadEntity = entity;

				if (entity instanceof ContainerDisplay) {
					ContainerDisplay containerDisplay =
						(ContainerDisplay)entity;

					uadEntity = containerDisplay.getContainer();
				}

				uadEntities.add(
					_constructUADEntity(
						liferayPortletRequest, liferayPortletResponse,
						applicationKey, uadEntity,
						_uadRegistry.getContainerTypeClasses(applicationKey),
						selectedUser.getUserId(),
						uadHierarchyDisplay.getUADDisplay(
							uadEntity.getClass())));
			}

			searchContainer.setResults(uadEntities);

			searchContainer.setTotal(uadEntities.size());
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}

			searchContainer.setResults(Collections.emptyList());
			searchContainer.setTotal(0);
		}

		RowChecker rowChecker = new EmptyOnClickRowChecker(
			liferayPortletResponse);

		searchContainer.setRowChecker(rowChecker);

		return searchContainer;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewUADHierarchyMVCRenderCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private SelectedUserHelper _selectedUserHelper;

	@Reference
	private UADRegistry _uadRegistry;

}