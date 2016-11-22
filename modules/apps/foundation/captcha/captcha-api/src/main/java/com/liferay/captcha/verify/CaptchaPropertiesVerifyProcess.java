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

package com.liferay.captcha.verify;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.verify.VerifyProcess;

import java.util.Dictionary;

import javax.portlet.PortletPreferences;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	immediate = true,
	property = {"verify.process.name=com.liferay.captcha.verify"},
	service = VerifyProcess.class
)
public class CaptchaPropertiesVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		upgradeConfiguration();
	}

	@Reference(unbind = "-")
	protected void setPrefsProps(PrefsProps prefsProps) {
		_prefsProps = prefsProps;
	}

	protected void upgradeConfiguration() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			Dictionary<String, Object> dictionary = new HashMapDictionary<>();

			dictionary.put(
				"captchaEngine",
				_prefsProps.getString(
					LEGACY_CAPTCHA_ENGINE_IMPL, StringPool.BLANK));

			dictionary.put(
				"reCaptchaPrivateKey",
				_prefsProps.getString(
					LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PRIVATE,
					StringPool.BLANK));

			dictionary.put(
				"reCaptchaPublicKey",
				_prefsProps.getString(
					LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PUBLIC,
					StringPool.BLANK));

			PortletPreferences portletPreferences =
				_prefsProps.getPreferences();

			portletPreferences.reset(LEGACY_CAPTCHA_ENGINE_IMPL);
			portletPreferences.reset(
				LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PRIVATE);
			portletPreferences.reset(
				LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PUBLIC);

			Configuration configuration = _configurationAdmin.getConfiguration(
				CaptchaConfiguration.class.getName());

			configuration.update(dictionary);
		}
	}

	protected static final String LEGACY_CAPTCHA_ENGINE_IMPL =
		"captcha.engine.impl";

	protected static final String LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PRIVATE =
		"captcha.engine.recaptcha.key.private";

	protected static final String LEGACY_CAPTCHA_ENGINE_RECAPTCHA_KEY_PUBLIC =
		"captcha.engine.recaptcha.key.public";

	private static final Log _log = LogFactoryUtil.getLog(
		CaptchaPropertiesVerifyProcess.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private PrefsProps _prefsProps;

}