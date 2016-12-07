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

package com.liferay.captcha.configuration;

import com.liferay.portal.captcha.CaptchaImpl;
import com.liferay.portal.captcha.recaptcha.ReCaptchaImpl;
import com.liferay.portal.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.lang.DoPrivilegedBean;
import com.liferay.portal.util.PrefsPropsUtil;

import java.util.Dictionary;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.captcha.configuration.CaptchaConfiguration"
	},
	service = ConfigurationModelListener.class
)
public class CaptchaConfigurationModelListener
	implements ConfigurationModelListener {

	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try {
			updateCaptcha(properties);
		}
		catch (CaptchaConfigurationException cce) {
			throw new ConfigurationModelListenerException(
				cce.getMessage(), CaptchaConfiguration.class,
				CaptchaConfigurationModelListener.class, properties);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					getResourceBundle(),
					"an-error-occurred-while-updating-the-captcha-engine"),
				CaptchaConfiguration.class,
				CaptchaConfigurationModelListener.class, properties);
		}
	}

	protected ResourceBundle getResourceBundle() {
		if (_resourceBundle == null) {
			Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

			return ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());
		}

		return _resourceBundle;
	}

	protected void updateCaptcha(Dictionary<String, Object> properties)
		throws Exception {

		String captchaEngine = (String)properties.get("captchaEngine");
		String reCaptchaPublicKey = (String)properties.get(
			"reCaptchaPublicKey");
		String reCaptchaPrivateKey = (String)properties.get(
			"reCaptchaPrivateKey");

		Captcha captcha = null;

		if (captchaEngine.equals(SimpleCaptchaImpl.class.getName())) {
			captcha = new SimpleCaptchaImpl();
		}
		else {
			captcha = new ReCaptchaImpl();

			validateReCaptchaKeys(reCaptchaPublicKey, reCaptchaPrivateKey);
		}

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences();

		Class<?> clazz = captcha.getClass();

		portletPreferences.setValue(
			PropsKeys.CAPTCHA_ENGINE_IMPL, clazz.getName());

		portletPreferences.setValue(
			PropsKeys.CAPTCHA_ENGINE_RECAPTCHA_KEY_PRIVATE,
			reCaptchaPrivateKey);
		portletPreferences.setValue(
			PropsKeys.CAPTCHA_ENGINE_RECAPTCHA_KEY_PUBLIC, reCaptchaPublicKey);

		portletPreferences.store();

		CaptchaImpl captchaImpl = null;

		Captcha currentCaptcha = CaptchaUtil.getCaptcha();

		if (currentCaptcha instanceof DoPrivilegedBean) {
			DoPrivilegedBean doPrivilegedBean =
				(DoPrivilegedBean)currentCaptcha;

			captchaImpl = (CaptchaImpl)doPrivilegedBean.getActualBean();
		}
		else {
			captchaImpl = (CaptchaImpl)currentCaptcha;
		}

		captchaImpl.setCaptcha(captcha);
	}

	protected void validateReCaptchaKeys(
			String reCaptchaPublicKey, String reCaptchaPrivateKey)
		throws Exception {

		if (Validator.isNull(reCaptchaPublicKey)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					getResourceBundle(),
					"the-recaptcha-public-key-is-not-valid"));
		}

		if (Validator.isNull(reCaptchaPrivateKey)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					getResourceBundle(),
					"the-recaptcha-private-key-is-not-valid"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CaptchaConfigurationModelListener.class);

	private ResourceBundle _resourceBundle;

}