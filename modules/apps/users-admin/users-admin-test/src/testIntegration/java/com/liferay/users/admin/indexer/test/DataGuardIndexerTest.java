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

package com.liferay.users.admin.indexer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataGuardIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFirst() throws Exception {
		OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, _SEARCH_TERM,
			false);
		UserTestUtil.addUser(_SEARCH_TERM);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		userGroup.setName(_SEARCH_TERM);

		UserGroupLocalServiceUtil.updateUserGroup(userGroup);
	}

	@Test
	public void testSecondOrganization() throws Exception {
		_assertSearch(
			OrganizationLocalServiceUtil.search(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				_SEARCH_TERM, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));
	}

	@Test
	public void testSecondUser() throws Exception {
		_assertSearch(
			UserLocalServiceUtil.search(
				TestPropsValues.getCompanyId(), _SEARCH_TERM,
				WorkflowConstants.STATUS_APPROVED, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, (Sort)null));
	}

	@Test
	public void testSecondUserGroup() throws Exception {
		_assertSearch(
			UserGroupLocalServiceUtil.search(
				TestPropsValues.getCompanyId(), _SEARCH_TERM, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort)null));
	}

	private void _assertSearch(Hits hits) {
		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	private static final String _SEARCH_TERM = RandomTestUtil.randomString();

}