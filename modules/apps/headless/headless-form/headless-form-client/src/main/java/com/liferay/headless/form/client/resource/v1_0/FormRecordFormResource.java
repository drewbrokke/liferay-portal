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

package com.liferay.headless.form.client.resource.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormRecordForm;
import com.liferay.headless.form.client.http.HttpInvoker;
import com.liferay.headless.form.client.serdes.v1_0.FormRecordFormSerDes;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormRecordFormResource {

	public com.liferay.headless.form.client.dto.v1_0.FormRecord putFormRecord(
			Long formRecordId, FormRecordForm formRecordForm)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			FormRecordFormSerDes.toJSON(formRecordForm), "application/json");

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.PUT);

		httpInvoker.path(
			"http://localhost:8080/o/headless-form/v1.0/form-records/{formRecordId}",
			formRecordId);

		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		String content = httpResponse.getContent();

		_logger.fine("HTTP response content: " + content);

		_logger.fine("HTTP response message: " + httpResponse.getMessage());
		_logger.fine("HTTP response status: " + httpResponse.getStatus());

		try {
			return com.liferay.headless.form.client.serdes.v1_0.
				FormRecordSerDes.toDTO(content);
		}
		catch (Exception e) {
			_logger.log(
				Level.WARNING, "Unable to process HTTP response: " + content,
				e);

			throw e;
		}
	}

	public com.liferay.headless.form.client.dto.v1_0.FormRecord
			postFormFormRecord(Long formId, FormRecordForm formRecordForm)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			FormRecordFormSerDes.toJSON(formRecordForm), "application/json");

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);

		httpInvoker.path(
			"http://localhost:8080/o/headless-form/v1.0/forms/{formId}/form-records",
			formId);

		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		String content = httpResponse.getContent();

		_logger.fine("HTTP response content: " + content);

		_logger.fine("HTTP response message: " + httpResponse.getMessage());
		_logger.fine("HTTP response status: " + httpResponse.getStatus());

		try {
			return com.liferay.headless.form.client.serdes.v1_0.
				FormRecordSerDes.toDTO(content);
		}
		catch (Exception e) {
			_logger.log(
				Level.WARNING, "Unable to process HTTP response: " + content,
				e);

			throw e;
		}
	}

	private static final Logger _logger = Logger.getLogger(
		FormRecordFormResource.class.getName());

}