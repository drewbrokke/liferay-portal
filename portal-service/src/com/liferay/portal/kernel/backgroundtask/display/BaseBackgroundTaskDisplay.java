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

package com.liferay.portal.kernel.backgroundtask.display;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Writer;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrew Betts
 */
public abstract class BaseBackgroundTaskDisplay
	implements BackgroundTaskDisplay {

	public BaseBackgroundTaskDisplay(BackgroundTask backgroundTask) {
		_backgroundTask = backgroundTask;

		_backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
				backgroundTask.getBackgroundTaskId());
	}

	@Override
	public String getDetailsBody() {
		return getDetailsBody(LocaleUtil.getDefault());
	}

	@Override
	public String getDetailsBody(Locale locale) {
		TemplateManager templateManager =
			TemplateManagerUtil.getTemplateManager(
				TemplateConstants.LANG_TYPE_FTL);

		Template template = templateManager.getTemplate(
			getTemplateResource(), true);

		JSONObject jsonObject = getDetailsJSONObject(locale);

		template.put("backgroundTask", _backgroundTask);
		template.put("backgroundTaskStatus", _backgroundTaskStatus);
		template.put("backgroundTaskDisplay", this);
		template.put("jsonObject", jsonObject);

		template.putAll(getTemplateVars());

		Writer writer = new UnsyncStringWriter();

		try {
			template.processTemplate(writer);
		}
		catch (TemplateException te) {
			if (_log.isDebugEnabled()) {
				_log.debug(te, te);
			}

			return "";
		}

		return writer.toString();
	}

	@Override
	public String getMessage() {
		return getMessage(Locale.getDefault());
	}

	@Override
	public String getMessage(Locale locale) {
		return LanguageUtil.get(locale, createMessageKey());
	}

	@Override
	public abstract int getPercentage();

	@Override
	public boolean hasDetails() {
		if (Validator.isNotNull(_backgroundTask.getStatusMessage())) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasMessage() {
		if (Validator.isNotNull(createMessageKey())) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPercentage() {
		if (getPercentage() >= 0) {
			return true;
		}

		return false;
	}

	protected abstract String createMessageKey();

	protected BackgroundTask getBackgroundTask() {
		return _backgroundTask;
	}

	protected BackgroundTaskStatus getBackgroundTaskStatus() {
		return _backgroundTaskStatus;
	}

	protected long getBackgroundTaskStatusAttributeLong(String attributeKey) {
		return GetterUtil.getLong(
			_backgroundTaskStatus.getAttribute(attributeKey));
	}

	protected String getBackgroundTaskStatusAttributeString(
		String attributeKey) {

		return GetterUtil.getString(
			_backgroundTaskStatus.getAttribute(attributeKey));
	}

	protected JSONObject getDetailsJSONObject(Locale locale) {
		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(
				_backgroundTask.getStatusMessage());
		}
		catch (JSONException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return jsonObject;
	}

	protected abstract TemplateResource getTemplateResource();

	protected abstract Map<String, ?> getTemplateVars();

	protected boolean hasBackgroundTaskStatus() {
		if (_backgroundTaskStatus != null) {
			return true;
		}

		return false;
	}

	protected JSONArray translateJSON(JSONArray jsonArray, Locale locale) {
		JSONArray translatedJSON = JSONFactoryUtil.createJSONArray();

		for (Object obj : jsonArray) {
			if (obj instanceof JSONObject) {
				translatedJSON.put(translateJSON((JSONObject)obj, locale));
			}
			else if (obj instanceof JSONArray) {
				translatedJSON.put(translateJSON((JSONArray)obj, locale));
			}
			else if (obj instanceof String) {
				translatedJSON.put(LanguageUtil.get(locale, (String)obj));
			}
			else {
				translatedJSON.put(obj);
			}
		}

		return translatedJSON;
	}

	protected JSONObject translateJSON(JSONObject jsonObject, Locale locale) {
		JSONObject translatedJSON = JSONFactoryUtil.createJSONObject();

		Iterator<String> itr = jsonObject.keys();

		while (itr.hasNext()) {
			String key = itr.next();

			Object obj = jsonObject.get(key);

			if (obj instanceof JSONObject) {
				translatedJSON.put(key, translateJSON((JSONObject)obj, locale));
			}
			else if (obj instanceof JSONArray) {
				translatedJSON.put(key, translateJSON((JSONArray)obj, locale));
			}
			else if (obj instanceof String) {
				translatedJSON.put(key, LanguageUtil.get(locale, (String)obj));
			}
			else {
				translatedJSON.put(key, obj);
			}
		}

		return translatedJSON;
	}

	protected static final int PERCENTAGE_MAX = 100;

	protected static final int PERCENTAGE_NONE = -1;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseBackgroundTaskDisplay.class);

	private final BackgroundTask _backgroundTask;
	private final BackgroundTaskStatus _backgroundTaskStatus;

}