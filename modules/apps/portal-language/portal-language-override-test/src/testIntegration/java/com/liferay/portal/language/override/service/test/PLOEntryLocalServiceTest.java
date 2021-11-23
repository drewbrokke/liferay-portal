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

package com.liferay.portal.language.override.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.exception.NoSuchPLOEntryException;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.language.override.service.PLOEntryLocalServiceUtil;
import com.liferay.portal.language.override.service.persistence.PLOEntryPersistence;
import com.liferay.portal.language.override.service.persistence.PLOEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.CLASS)
@RunWith(Arquillian.class)
public class PLOEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddPLOEntry() throws Exception {
		String key = RandomTestUtil.randomString();
		Locale locale = LocaleUtil.getDefault();
		String value = RandomTestUtil.randomString();

		// Assert key not present

		Assert.assertEquals(LanguageResources.getMessage(locale, key), null);

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(locale);

		Set<String> keySet = resourceBundle.keySet();

		Assert.assertFalse(keySet.contains(key));

		_ploEntryLocalService.addPLOEntry(
			TestPropsValues.getCompanyId(), key,
			LanguageUtil.getLanguageId(locale), value);

		Assert.assertEquals(LanguageResources.getMessage(locale, key), value);

		resourceBundle = LanguageResources.getResourceBundle(locale);

		keySet = resourceBundle.keySet();

		Assert.assertTrue(keySet.contains(key));
	}

	@Test
	public void testOverrideExistingLanguageKey() throws Exception {
		// Assert key not present
	}

	private void _assertLanguageValue(Locale locale, String key, String expected) {
		Assert.assertEquals(
			expected,
			LanguageUtil.get(locale, key));
	}

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;
}