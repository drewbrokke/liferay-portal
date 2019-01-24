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
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.display.HierarchicalUADDisplay;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		if (object instanceof DLFolderContainer) {
			DLFolderContainer dlFolderContainer = (DLFolderContainer)object;

			DLFolder dlFolder = dlFolderContainer.getDlFolder();

			fieldValues = _dlFolderUADDisplay.getFieldValues(
				dlFolder, fieldNames);

			fieldValues.put("count", dlFolderContainer.getCount());

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
		return new Class<?>[] {DLFolderContainer.class, DLFileEntry.class};
	}

	@Override
	public String getTypeClassLabel(Class clazz, Locale locale) {
		if (clazz == DLFolderContainer.class) {
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

	private List<DLFolderContainer> _getDLFolders(
		long userId, long[] groupIds, Serializable parentFolderId,
		String keywords, String orderByField, String orderByType) {

		Map<Long, Integer> topLevelFolders = new HashMap<>();

		try {
			List<DLFolder> dlFolders = _dlFolderUADDisplay.search(
				userId, groupIds, keywords, orderByField, orderByType,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (DLFolder dlFolder : dlFolders) {
				long topLevelFolderId = _getTopLevelFolderId(
					dlFolder, (long)parentFolderId);

				if (dlFolder.getFolderId() == topLevelFolderId) {
					topLevelFolders.putIfAbsent(topLevelFolderId, 0);
				}
				else {
					_incrementCount(topLevelFolders, topLevelFolderId);
				}
			}

			List<DLFileEntry> dlFileEntries = _dlFileEntryUADDisplay.search(
				userId, groupIds, keywords, orderByField, orderByType,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (DLFileEntry dlFileEntry : dlFileEntries) {
				_incrementCount(
					topLevelFolders,
					_getTopLevelFolderId(
						dlFileEntry.getFolder(), (long)parentFolderId));
			}
		}
		catch (PortalException pe) {
			_log.error(pe, pe);
		}

		Set<Map.Entry<Long, Integer>> entrySet = topLevelFolders.entrySet();

		Stream<Map.Entry<Long, Integer>> topLevelFoldersEntryStream =
			entrySet.stream();

		return topLevelFoldersEntryStream.filter(
			entry -> Validator.isNotNull(entry.getKey())
		).map(
			entry -> new DLFolderContainer(
				_dlFolderLocalService.fetchDLFolder(entry.getKey()),
				entry.getValue())
		).collect(
			Collectors.toList()
		);
	}

	private long _getTopLevelFolderId(DLFolder dlFolder, long parentFolderId)
		throws PortalException {

		if ((dlFolder.getFolderId() == parentFolderId) ||
			((parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
			 !StringUtil.contains(
				 dlFolder.getTreePath(), String.valueOf(parentFolderId),
				 "/"))) {

			return 0;
		}

		if ((dlFolder.getParentFolderId() ==
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
			(dlFolder.getParentFolderId() == parentFolderId)) {

			return dlFolder.getFolderId();
		}

		List<Long> ancestorFolderIds = dlFolder.getAncestorFolderIds();

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return ancestorFolderIds.get(ancestorFolderIds.size() - 1);
		}

		return ancestorFolderIds.get(
			ancestorFolderIds.indexOf(parentFolderId) - 1);
	}

	private void _incrementCount(Map<Long, Integer> map, long key) {
		int currentCount = 0;

		if (map.containsKey(key)) {
			currentCount = map.get(key);
		}

		map.put(key, currentCount + 1);
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