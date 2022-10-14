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
public class GenderedTranslationHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testDecorateTranslation() {
		String genderedTranslation = "[El|La] {} es [nuevo|nueva].";
		String ungenderedTranslation = RandomTestUtil.randomString();

		GenderedTranslationHelper genderedTranslationHelper =
			new GenderedTranslationHelper(null);

		Assert.assertEquals(
			genderedTranslation,
			genderedTranslationHelper.decorateTranslation(genderedTranslation));
		Assert.assertEquals(
			ungenderedTranslation,
			genderedTranslationHelper.decorateTranslation(
				ungenderedTranslation));

		genderedTranslationHelper = new GenderedTranslationHelper(true);

		Assert.assertEquals(
			ungenderedTranslation,
			genderedTranslationHelper.decorateTranslation(
				ungenderedTranslation));
		Assert.assertEquals(
			"El {} es nuevo.",
			genderedTranslationHelper.decorateTranslation(genderedTranslation));

		genderedTranslationHelper = new GenderedTranslationHelper(false);

		Assert.assertEquals(
			ungenderedTranslation,
			genderedTranslationHelper.decorateTranslation(
				ungenderedTranslation));
		Assert.assertEquals(
			"La {} es nueva.",
			genderedTranslationHelper.decorateTranslation(genderedTranslation));
	}

}