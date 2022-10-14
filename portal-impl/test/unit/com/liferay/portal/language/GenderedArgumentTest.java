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

package com.liferay.portal.language;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class GenderedArgumentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testCreate() {
		String argument = "foo";

		GenderedArgument genderedArgument = GenderedArgument.create(argument);

		Assert.assertFalse(genderedArgument.isGendered());
		Assert.assertNull(genderedArgument.getMasculine());
		Assert.assertEquals(argument, genderedArgument.getText());

		genderedArgument = GenderedArgument.create(
			GenderedArgument.markAsFeminine(argument));

		Assert.assertTrue(genderedArgument.isGendered());
		Assert.assertFalse(genderedArgument.getMasculine());
		Assert.assertEquals(argument, genderedArgument.getText());

		genderedArgument = GenderedArgument.create(
			GenderedArgument.markAsMasculine(argument));

		Assert.assertTrue(genderedArgument.isGendered());
		Assert.assertTrue(genderedArgument.getMasculine());
		Assert.assertEquals(argument, genderedArgument.getText());
	}

	@Test
	public void testMarkAsFeminine() {
		String argument = RandomTestUtil.randomString();

		Assert.assertFalse(argument.endsWith(GenderedArgument.SUFFIX_FEMININE));

		argument = GenderedArgument.markAsFeminine(argument);

		Assert.assertTrue(argument.endsWith(GenderedArgument.SUFFIX_FEMININE));
	}

	@Test
	public void testMarkAsMasculine() {
		String argument = RandomTestUtil.randomString();

		Assert.assertFalse(
			argument.endsWith(GenderedArgument.SUFFIX_MASCULINE));

		argument = GenderedArgument.markAsMasculine(argument);

		Assert.assertTrue(argument.endsWith(GenderedArgument.SUFFIX_MASCULINE));
	}

}