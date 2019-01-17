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

package com.liferay.document.library.uad.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.uad.test.DLFileEntryUADTestUtil;
import com.liferay.document.library.uad.test.DLFolderUADTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.display.HierarchicalUADDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class DLHierarchicalUADDisplayTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		long defaultUserId = TestPropsValues.getUserId();
		long userId = _user.getUserId();

		DLFolder folder1 = _addFolder(defaultUserId);

		DLFolder folder1Subfolder1 = _addFolder(
			defaultUserId, folder1.getFolderId());
		DLFolder folder1Subfolder2 = _addFolder(userId, folder1.getFolderId());

		_addFile(defaultUserId, folder1);
		_addFile(defaultUserId, folder1Subfolder1);
		_addFile(defaultUserId, folder1Subfolder2);
		_addFile(userId, folder1);
		_addFile(userId, folder1Subfolder1);
		_addFile(userId, folder1Subfolder2);

		_userFolderAndItemCountMap.put(folder1.getFolderId(), 3);
		_userFolderAndItemCountMap.put(folder1Subfolder1.getFolderId(), 1);
		_userFolderAndItemCountMap.put(folder1Subfolder2.getFolderId(), 1);

		DLFolder folder2 = _addFolder(userId);

		DLFolder folder2Subfolder1 = _addFolder(
			defaultUserId, folder2.getFolderId());
		DLFolder folder2Subfolder2 = _addFolder(userId, folder2.getFolderId());

		_addFile(defaultUserId, folder2);
		_addFile(defaultUserId, folder2Subfolder1);
		_addFile(userId, folder2);
		_addFile(userId, folder2);
		_addFile(userId, folder2Subfolder1);

		_userFolderAndItemCountMap.put(folder2.getFolderId(), 4);
		_userFolderAndItemCountMap.put(folder2Subfolder1.getFolderId(), 1);
		_userFolderAndItemCountMap.put(folder2Subfolder2.getFolderId(), 0);

		DLFolder folder3 = _addFolder(defaultUserId);

		_userFolderAndItemCountMap.put(folder3.getFolderId(), 0);

		_userFolderAndItemCountMap.put(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, 2);
	}

	@After
	public void tearDown() throws Exception {
		_dlFileEntries.clear();
		_dlFolders.clear();
		_userFolderAndItemCountMap.clear();

		DLFileEntryUADTestUtil.cleanUpDependencies(
			_dlAppLocalService, _dlFileEntryLocalService, _dlFolderLocalService,
			_dlFileEntries);
	}

	@Test
	public void testSearch() {
		for (DLFolder dlFolder : _dlFolders) {
			List items = _dlHierarchicalUADDisplay.search(
				_user.getUserId(), null, dlFolder.getFolderId(), "", "name",
				"asc", QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				(int)_userFolderAndItemCountMap.get(dlFolder.getFolderId()),
				items.size());
		}
	}

	private DLFileEntry _addFile(long userId, DLFolder dlFolder)
		throws Exception {

		DLFileEntry dlFileEntry = DLFileEntryUADTestUtil.addDLFileEntry(
			_dlAppLocalService, _dlFileEntryLocalService, dlFolder, userId);

		_dlFileEntries.add(dlFileEntry);

		return dlFileEntry;
	}

	private DLFolder _addFolder(long userId) throws Exception {
		DLFolder dlFolder = DLFolderUADTestUtil.addDLFolder(
			_dlFolderLocalService, userId);

		_dlFolders.add(dlFolder);

		return dlFolder;
	}

	private DLFolder _addFolder(long userId, long parentFolderId)
		throws Exception {

		DLFolder dlFolder = DLFolderUADTestUtil.addDLFolder(
			_dlFolderLocalService, userId, parentFolderId);

		_dlFolders.add(dlFolder);

		return dlFolder;
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private final List<DLFileEntry> _dlFileEntries = new ArrayList<>();

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	private final List<DLFolder> _dlFolders = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.document.library.uad.display.DLHierarchicalUADDisplay"
	)
	private HierarchicalUADDisplay _dlHierarchicalUADDisplay;

	@DeleteAfterTestRun
	private User _user;

	private final Map<Long, Integer> _userFolderAndItemCountMap =
		new HashMap<>();

}