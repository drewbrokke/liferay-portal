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

package com.liferay.bulk.rest.client.resource.v1_0;

import com.liferay.bulk.rest.client.http.HttpInvoker;

import java.util.logging.Logger;

import javax.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class TaxonomyCategoryResource {

	public void patchTaxonomyCategoryBatch(
			com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategoryBulkSelection
				taxonomyCategoryBulkSelection)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.PATCH);

		httpInvoker.path(
			"http://localhost:8080/o/bulk-rest/v1.0/taxonomy-categories/batch");

		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		String content = httpResponse.getContent();

		_logger.fine("HTTP response content: " + content);

		_logger.fine("HTTP response message: " + httpResponse.getMessage());
		_logger.fine("HTTP response status: " + httpResponse.getStatus());
	}

	public void putTaxonomyCategoryBatch(
			com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategoryBulkSelection
				taxonomyCategoryBulkSelection)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.PUT);

		httpInvoker.path(
			"http://localhost:8080/o/bulk-rest/v1.0/taxonomy-categories/batch");

		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		String content = httpResponse.getContent();

		_logger.fine("HTTP response content: " + content);

		_logger.fine("HTTP response message: " + httpResponse.getMessage());
		_logger.fine("HTTP response status: " + httpResponse.getStatus());
	}

	private static final Logger _logger = Logger.getLogger(
		TaxonomyCategoryResource.class.getName());

}