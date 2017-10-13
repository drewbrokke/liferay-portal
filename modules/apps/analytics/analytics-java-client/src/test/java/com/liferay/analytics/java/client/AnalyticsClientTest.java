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

package com.liferay.analytics.java.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eduardo Garcia
 */
public class AnalyticsClientTest {

	@Test
	public void testAnalyticsEventCreation() {
		AnalyticsEventsMessage analyticsEventsMessage =
			new AnalyticsEventsMessage();

		analyticsEventsMessage.setApplicationId("AT");
		analyticsEventsMessage.setChannel("web");

		AnalyticsEventsMessage.Context context =
			new AnalyticsEventsMessage.Context();

		context.setInstanceId(1234);
		context.setLanguageId("en_US");
		context.setUrl("http://www.liferay.com");
		context.setUserId(1234);

		analyticsEventsMessage.setContext(context);

		AnalyticsEventsMessage.Event event = new AnalyticsEventsMessage.Event();

		event.setEvent("view");
		event.setTimestamp(new Date());

		AnalyticsEventsMessage.Properties properties =
			new AnalyticsEventsMessage.Properties();

		properties.setElementId("banner1");

		event.setProperties(properties);

		List<AnalyticsEventsMessage.Event> events = new ArrayList();

		events.add(event);

		analyticsEventsMessage.setEvents(events);

		analyticsEventsMessage.setMessageFormat("AT");

		Response response = _analyticsClient.sendAnalytics(
			analyticsEventsMessage);

		Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
	}

	private final AnalyticsClient _analyticsClient = new AnalyticsClient();

}