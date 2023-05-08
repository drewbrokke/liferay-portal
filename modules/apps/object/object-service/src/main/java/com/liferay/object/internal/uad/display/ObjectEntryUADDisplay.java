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

package com.liferay.object.internal.uad.display;

import com.liferay.object.internal.deployer.ObjectDefinitionClassHelper;
import com.liferay.object.internal.uad.constants.ObjectUADConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.BaseModelUADDisplay;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryUADDisplay<T extends ObjectEntry> extends BaseModelUADDisplay<T> {

	public ObjectEntryUADDisplay(
		GroupLocalService groupLocalService, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService, Portal portal,
		ObjectDefinitionClassHelper objectDefinitionClassHelper) {

		_groupLocalService = groupLocalService;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_portal = portal;
		_objectDefinitionClassHelper = objectDefinitionClassHelper;
	}

	private final ObjectDefinitionClassHelper _objectDefinitionClassHelper;

	@Override
	public T get(Serializable primaryKey) throws Exception {
		return (T)_objectDefinitionClassHelper.wrap(_objectEntryLocalService.getObjectEntry(
			Long.valueOf(primaryKey.toString())));
	}

	@Override
	public String[] getDisplayFieldNames() {
		List<String> displayFieldNames = new ArrayList<>();

		ListUtil.isNotEmptyForEach(
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId()),
			objectField -> displayFieldNames.add(objectField.getName()));

		return displayFieldNames.toArray(new String[0]);
	}

	@Override
	public String getEditURL(
			ObjectEntry objectEntry,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest,
				_groupLocalService.fetchGroup(objectEntry.getGroupId()),
				objectDefinition.getPortletId(), 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/object_entries/edit_object_entry"
		).setBackURL(
			_portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).buildString();
	}

	@Override
	public Map<String, Object> getFieldValues(
		ObjectEntry objectEntry, String[] fieldNames, Locale locale) {

		Map<String, Object> fieldValues = objectEntry.getModelAttributes();

		try {
			fieldValues.putAll(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return fieldValues;
	}

	@Override
	public String getName(ObjectEntry objectEntry, Locale locale) {
		return String.valueOf(objectEntry.getObjectEntryId());
	}

	@Override
	public Class<T> getTypeClass() {
		return (Class<T>)_objectDefinitionClassHelper.getObjectClass();
	}

	@Override
	public String getTypeName(Locale locale) {
		return _objectDefinition.getShortName();
	}

	@Override
	public boolean isSiteScoped() {
		return false;
	}

	@Override
	public boolean isUserOwned(ObjectEntry objectEntry, long userId) {
		if (objectEntry.getUserId() == userId) {
			return true;
		}

		return false;
	}

	@Override
	protected long doCount(DynamicQuery dynamicQuery) {
		return _objectEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	@Override
	protected DynamicQuery doGetDynamicQuery() {
		return _objectEntryLocalService.dynamicQuery();
	}

	@Override
	protected List<T> doGetRange(
		DynamicQuery dynamicQuery, int start, int end) {

		return (List<T>)TransformUtil.transform(
			_objectEntryLocalService.dynamicQuery(dynamicQuery, start, end),
			_objectDefinitionClassHelper::wrap);
	}

	@Override
	protected String[] doGetUserIdFieldNames() {
		return ObjectUADConstants.USER_ID_FIELD_NAMES_OBJECT_ENTRY;
	}

	@Override
	protected DynamicQuery getDynamicQuery(long userId) {
		DynamicQuery dynamicQuery = _objectEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.and(
				RestrictionsFactoryUtil.eq(
					"objectDefinitionId",
					_objectDefinition.getObjectDefinitionId()),
				RestrictionsFactoryUtil.or(
					RestrictionsFactoryUtil.eq("userId", userId),
					RestrictionsFactoryUtil.eq("statusByUserId", userId))));

		return dynamicQuery;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryUADDisplay.class);

	private final GroupLocalService _groupLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final Portal _portal;

}