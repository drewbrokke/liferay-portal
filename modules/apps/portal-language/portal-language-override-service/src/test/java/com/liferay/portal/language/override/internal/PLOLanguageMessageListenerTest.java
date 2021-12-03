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

package com.liferay.portal.language.override.internal;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Drew Brokke
 */
@RunWith(MockitoJUnitRunner.class)
public class PLOLanguageMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_ploLanguageMessageListener, "_ploLanguageOverrideCache",
			_ploLanguageOverrideCache);
	}

	@Test
	public void testClearsTheCacheOnAMessageToTheConfigurationDestination()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		Locale locale = LocaleUtil.getDefault();

		Message message = new Message();

		message.put("companyId", companyId);
		message.put("locale", locale);

		_ploLanguageMessageListener.doReceive(message);

		Mockito.verify(
			_ploLanguageOverrideCache, Mockito.times(1)
		).clear(
			companyId, locale
		);
	}

	private final PLOLanguageMessageListener _ploLanguageMessageListener =
		new PLOLanguageMessageListener();

	@Mock
	private PLOLanguageOverrideCache _ploLanguageOverrideCache;

}