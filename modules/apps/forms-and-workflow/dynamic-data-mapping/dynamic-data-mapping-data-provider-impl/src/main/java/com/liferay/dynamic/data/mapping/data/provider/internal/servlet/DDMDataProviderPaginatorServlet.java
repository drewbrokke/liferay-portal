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

package com.liferay.dynamic.data.mapping.data.provider.internal.servlet;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderContext;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderContextFactory;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInvoker;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderOutputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderParameterSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-data-provider-paginator",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.data.provider.internal.servlet.DDMDataProviderPaginatorServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-data-provider-paginator/*"
	},
	service = {DDMDataProviderPaginatorServlet.class, Servlet.class}
)
public class DDMDataProviderPaginatorServlet extends HttpServlet {

	protected void addParametersFromRequest(
			DDMDataProviderRequest ddmDataProviderRequest,
			HttpServletRequest request)
		throws Exception {

		JSONObject inputParametersJSONObject = getInputParametersJSONObject(
			request);

		inputParametersJSONObject.keys().forEachRemaining(
			inputParameterName -> {
				ddmDataProviderRequest.queryString(
					inputParameterName,
					inputParametersJSONObject.getString(inputParameterName));
			});
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		List<Map<String, String>> dataProviderResult = executeDataProvider(
			request, response);

		if (dataProviderResult == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		response.setContentType(ContentTypes.APPLICATION_JSON);
		response.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			response, jsonSerializer.serializeDeep(dataProviderResult));
	}

	protected List<Map<String, String>> executeDataProvider(
		HttpServletRequest request, HttpServletResponse response) {

		String dataProviderInstanceUUID = ParamUtil.getString(
			request, "dataProviderInstanceUUID");

		String outputParameterName = ParamUtil.getString(
			request, "outputParameterName");

		int start = ParamUtil.getInteger(request, "start");
		int end = ParamUtil.getInteger(request, "end");

		if (Validator.isNull(dataProviderInstanceUUID) ||
			Validator.isNull(outputParameterName) || (start < 0) ||
			(end < 0) || (end < start) || (start == end)) {

			return null;
		}

		List<Map<String, String>> dataProviderResult = new ArrayList<>();

		try {
			DDMDataProviderContext ddmDataProviderContext =
				_ddmDataProviderContextFactory.create(dataProviderInstanceUUID);

			DDMDataProviderRequest ddmDataProviderRequest =
				new DDMDataProviderRequest(ddmDataProviderContext, request);

			addParametersFromRequest(ddmDataProviderRequest, request);

			ddmDataProviderRequest.queryString(
				"paginationStart", String.valueOf(start));
			ddmDataProviderRequest.queryString(
				"paginationEnd", String.valueOf(end));

			DDMDataProviderResponse ddmDataProviderResponse =
				_ddmDataProviderInvoker.invoke(ddmDataProviderRequest);

			String[] keyValue = getKeyValue(
				outputParameterName, ddmDataProviderContext);

			String key = keyValue[0];
			String value = keyValue[1];

			for (Map<Object, Object> map : ddmDataProviderResponse.getData()) {
				Map<String, String> result = new HashMap<>();

				result.put("label", String.valueOf(map.get(value)));
				result.put("value", String.valueOf(map.get(key)));

				dataProviderResult.add(result);
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return dataProviderResult;
	}

	protected JSONObject getInputParametersJSONObject(
			HttpServletRequest request)
		throws JSONException {

		String inputParameters = ParamUtil.getString(
			request, "inputParameters");

		if (Validator.isNull(inputParameters)) {
			return _jsonFactory.createJSONObject();
		}

		return _jsonFactory.createJSONObject(inputParameters);
	}

	protected String[] getKeyValue(
		String ddmDataProviderOutput,
		DDMDataProviderContext ddmDataProviderContext) {

		String[] keyValue = getKeyValuePathsFromDataProviderOutputParameter(
			ddmDataProviderOutput, ddmDataProviderContext);

		if (keyValue == null) {
			keyValue =
				new String[] {ddmDataProviderOutput, ddmDataProviderOutput};
		}

		return keyValue;
	}

	protected String[] getKeyValuePaths(
		String ddmDataProviderOutput,
		DDMDataProviderParameterSettings ddmDataProviderParemeterSettings) {

		for (DDMDataProviderOutputParametersSettings
				ddmDataProviderOutputParametersSetting :
					ddmDataProviderParemeterSettings.outputParameters()) {

			if (ddmDataProviderOutput.equals(
					ddmDataProviderOutputParametersSetting.
						outputParameterName())) {

				String[] paths = StringUtil.split(
					ddmDataProviderOutputParametersSetting.
						outputParameterPath(),
					CharPool.SEMICOLON);

				if (paths.length == 1) {
					paths = ArrayUtil.append(paths, paths[0]);
				}

				return paths;
			}
		}

		return null;
	}

	protected String[] getKeyValuePathsFromDataProviderOutputParameter(
		String ddmDataProviderOutput,
		DDMDataProviderContext ddmDataProviderContext) {

		if (ddmDataProviderContext.getType() == null) {
			return null;
		}

		DDMDataProviderParameterSettings ddmDataProviderParemeterSettings =
			ddmDataProviderContext.getSettingsInstance(
				DDMDataProviderParameterSettings.class);

		return getKeyValuePaths(
			ddmDataProviderOutput, ddmDataProviderParemeterSettings);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderPaginatorServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMDataProviderContextFactory _ddmDataProviderContextFactory;

	@Reference
	private DDMDataProviderInvoker _ddmDataProviderInvoker;

	@Reference
	private JSONFactory _jsonFactory;

}