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

package com.liferay.account.settings.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class AccountEntryGroupSettingsTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	private Group _group;

	@Test
	public void testGetDefaultAllowedTypes() {
		String[] expectedAllowedTypes = AccountConstants.ACCOUNT_ENTRY_TYPES;

		String[] allowedTypes = _getAllowedTypes();

		Assert.assertEquals(expectedAllowedTypes.length, allowedTypes.length);

		for (String allowedType : allowedTypes) {
			Assert.assertTrue(
				ArrayUtil.contains(expectedAllowedTypes, allowedType));
		}
	}

	@Test
	public void testSetAllowedTypes() throws Exception {
		String[] expectedAllowedTypes = new String[] {
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
		};

		String[] allowedTypes = _getAllowedTypes();

		Assert.assertNotEquals(expectedAllowedTypes.length, allowedTypes.length);

		Assert.assertTrue(
			ArrayUtil.contains(
				expectedAllowedTypes,
				AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST));

		_setAllowedTypes(expectedAllowedTypes);

		allowedTypes = _getAllowedTypes();

		Assert.assertEquals(expectedAllowedTypes.length, allowedTypes.length);

		Assert.assertFalse(
			ArrayUtil.contains(
				expectedAllowedTypes,
				AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST));

		for (String allowedType : allowedTypes) {
			Assert.assertTrue(
				ArrayUtil.contains(expectedAllowedTypes, allowedType));
		}
	}

	@Test(expected = AccountEntryTypeException.class)
	public void testSetInvalidAllowedType() throws Exception {
		_setAllowedTypes(new String[] {"foo", "bar"});
	}

	private void _setAllowedTypes(String[] allowedTypes) throws Exception {
		_accountEntryGroupSettings.setAllowedTypes(
			_group.getGroupId(), allowedTypes);
	}

	private void _assertEquals(String[] expectedAllowedTypes, String[] allowedTypes) {
		Assert.assertEquals(expectedAllowedTypes.length, allowedTypes.length);

		for (String allowedType : allowedTypes) {
			Assert.assertTrue(
				ArrayUtil.contains(expectedAllowedTypes, allowedType));
		}
	}

	private String[] _getAllowedTypes() {
		return _accountEntryGroupSettings.getAllowedTypes(_group.getGroupId());
	}

	@Inject
	private AccountEntryGroupSettings _accountEntryGroupSettings;
}
