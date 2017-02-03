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

package com.liferay.captcha.configuration.settings.internal;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.captcha.CaptchaSettings;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Pei-Jung Lan
 */
@Component(
	configurationPid = "com.liferay.captcha.configuration.CaptchaConfiguration",
	immediate = true, service = CaptchaSettings.class
)
public class CaptchaSettingsImpl implements CaptchaSettings {

	@Override
	public String getCaptchaEngine() {
		return _captchaConfiguration.captchaEngine();
	}

	@Override
	public boolean getCreateAccountCaptchaEnabled() {
		return _captchaConfiguration.createAccountCaptchaEnabled();
	}

	@Override
	public int getMaxChallenges() {
		return _captchaConfiguration.maxChallenges();
	}

	@Override
	public boolean getMessageBoardsEditCategoryCaptchaEnabled() {
		return _captchaConfiguration.messageBoardsEditCategoryCaptchaEnabled();
	}

	@Override
	public boolean getMessageBoardsEditMessageCaptchaEnabled() {
		return _captchaConfiguration.messageBoardsEditMessageCaptchaEnabled();
	}

	@Override
	public String getReCaptchaNoScriptUrl() {
		return _captchaConfiguration.reCaptchaNoScriptUrl();
	}

	@Override
	public String getReCaptchaPrivateKey() {
		return _captchaConfiguration.reCaptchaPrivateKey();
	}

	@Override
	public String getReCaptchaPublicKey() {
		return _captchaConfiguration.reCaptchaPublicKey();
	}

	@Override
	public String getReCaptchaScriptUrl() {
		return _captchaConfiguration.reCaptchaScriptUrl();
	}

	@Override
	public String getReCaptchaVerifyUrl() {
		return _captchaConfiguration.reCaptchaVerifyUrl();
	}

	@Override
	public boolean getSendPasswordCaptchaEnabled() {
		return _captchaConfiguration.sendPasswordCaptchaEnabled();
	}

	@Override
	public String[] getSimpleCaptchaBackgroundProducers() {
		return _captchaConfiguration.simpleCaptchaBackgroundProducers();
	}

	@Override
	public String[] getSimpleCaptchaGimpyRenderers() {
		return _captchaConfiguration.simpleCaptchaGimpyRenderers();
	}

	@Override
	public int getSimpleCaptchaHeight() {
		return _captchaConfiguration.simpleCaptchaHeight();
	}

	@Override
	public String[] getSimpleCaptchaNoiseProducers() {
		return _captchaConfiguration.simpleCaptchaNoiseProducers();
	}

	@Override
	public String[] getSimpleCaptchaTextProducers() {
		return _captchaConfiguration.simpleCaptchaTextProducers();
	}

	@Override
	public int getSimpleCaptchaWidth() {
		return _captchaConfiguration.simpleCaptchaWidth();
	}

	@Override
	public String[] getSimpleCaptchaWordRenderers() {
		return _captchaConfiguration.simpleCaptchaWordRenderers();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_captchaConfiguration = ConfigurableUtil.createConfigurable(
			CaptchaConfiguration.class, properties);
	}

	private CaptchaConfiguration _captchaConfiguration;

}