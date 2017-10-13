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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Eduardo Garcia
 */
public class AnalyticsClient {

	public Response sendAnalytics(
		AnalyticsEventsMessage analyticsEventMessage) {

		WebTarget webTarget = _client.target(_ANALYTICS_GATEWAY_URL);

		Invocation.Builder request = webTarget.request(
			MediaType.APPLICATION_JSON);

		return request.post(
			Entity.entity(analyticsEventMessage, MediaType.APPLICATION_JSON));
	}

	private static final String _ANALYTICS_GATEWAY_URL =
		"http://54.235.215.13:8095/api/analyticsgateway/send-analytics-events";

	private final Client _client = ClientBuilder.newClient();

}