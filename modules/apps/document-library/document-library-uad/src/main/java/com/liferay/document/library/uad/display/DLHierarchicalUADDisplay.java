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

package com.liferay.document.library.uad.display;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.uad.util.HierarchicalDLFolderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.user.associated.data.display.HierarchicalUADDisplay;
import com.liferay.user.associated.data.display.UADContainerEntity;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(immediate = true, service = HierarchicalUADDisplay.class)
public class DLHierarchicalUADDisplay implements HierarchicalUADDisplay {

	@Override
	public long countAll(long userId) {
		long fileEntriesCount = _dlFileEntryUADDisplay.count(userId);
		long foldersCount = _dlFolderUADDisplay.count(userId);

		return fileEntriesCount + foldersCount;
	}

	@Override
	public String[] getDisplayFieldNames() {
		return new String[] {"name", "count", "type"};
	}

	@Override
	public Map<String, Object> getFieldValues(
		Object object, String[] fieldNames) {

		Map<String, Object> fieldValues = new HashMap<>();

		if (object instanceof UADContainerEntity) {
			UADContainerEntity<DLFolder> uadContainerEntity =
				(UADContainerEntity<DLFolder>)object;

			DLFolder dlFolder = uadContainerEntity.getEntity();

			fieldValues = _dlFolderUADDisplay.getFieldValues(
				dlFolder, fieldNames);

			fieldValues.put("count", uadContainerEntity.getCount());

			fieldValues.put("type", "--");
		}

		if (object instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)object;

			fieldValues = _dlFileEntryUADDisplay.getFieldValues(
				dlFileEntry, fieldNames);

			fieldValues.put("count", "--");

			try {
				DLFileEntryType dlFileEntryType =
					dlFileEntry.getDLFileEntryType();

				fieldValues.put("type", dlFileEntryType);
			}
			catch (PortalException pe) {
				_log.error(pe, pe);
			}
		}

		return fieldValues;
	}

	@Override
	public String[] getSortingFieldNames() {
		return new String[] {"name", "createDate", "modifiedDate"};
	}

	@Override
	public Class[] getTypeClasses() {
		return new Class<?>[] {DLFolder.class, DLFileEntry.class};
	}

	@Override
	public String getTypeClassLabel(Class clazz, Locale locale) {
		if (clazz == DLFolder.class) {
			return LanguageUtil.get(locale, "folders");
		}

		if (clazz == DLFileEntry.class) {
			return LanguageUtil.get(locale, "documents");
		}

		return clazz.getSimpleName();
	}

	@Override
	public List search(
		long userId, long[] groupIds, Serializable parentId, String keywords,
		String orderByField, String orderByType, int start, int end) {

		List results = new ArrayList<>();

		List dlFolders = _getDLFolders(
			userId, groupIds, parentId, keywords, orderByField, orderByType);

		results.addAll(dlFolders);

		List dlFileEntries = _getDLFileEntries(
			userId, groupIds, parentId, keywords, orderByField, orderByType);

		results.addAll(dlFileEntries);

		return ListUtil.subList(results, start, end);
	}

	private List<DLFileEntry> _getDLFileEntries(
		long userId, long[] groupIds, Serializable folderId, String keywords,
		String orderByField, String orderByType) {

		return _dlFileEntryUADDisplay.search(
			userId, groupIds, folderId, keywords, orderByField, orderByType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	private List<UADContainerEntity<DLFolder>> _getDLFolders(
		long userId, long[] groupIds, Serializable parentFolderId,
		String keywords, String orderByField, String orderByType) {

		List<DLFileEntry> dlFileEntries = _dlFileEntryUADDisplay.search(
			userId, groupIds, keywords, orderByField, orderByType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<DLFolder> dlFolders = _dlFolderUADDisplay.search(
			userId, groupIds, keywords, orderByField, orderByType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<Long, Integer> topLevelFolders = new HashMap<>();

		try {
			topLevelFolders =
				HierarchicalDLFolderUtil.getTopLevelFoldersAndCount(
					dlFileEntries, dlFolders, (long)parentFolderId);
		}
		catch (PortalException pe) {
			_log.error(pe, pe);
		}

		List<UADContainerEntity<DLFolder>> topLevelFoldersList =
			new ArrayList<>();

		topLevelFolders.forEach(
			(folderId, count) -> {
				topLevelFoldersList.add(
					new UADContainerEntity<>(
						_dlFolderLocalService.fetchDLFolder(folderId), count));
			});

		return topLevelFoldersList;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLHierarchicalUADDisplay.class);

	@Reference
	private DLFileEntryUADDisplay _dlFileEntryUADDisplay;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DLFolderUADDisplay _dlFolderUADDisplay;

}