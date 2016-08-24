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

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Pei-Jung Lan
 */
@ExtendedObjectClassDefinition(category = "foundation")
@Meta.OCD(
	id = "com.liferay.captcha.configuration.CaptchaConfiguration",
	localization = "content/Language", name = "captcha"
)
public interface CaptchaConfiguration {

	@Meta.AD(
		deflt = "com.liferay.portal.captcha.simplecaptcha.SimpleCaptchaImpl",
		optionLabels = {"SimpleCaptcha", "reCAPTCHA"},
		optionValues = {
			"com.liferay.portal.captcha.simplecaptcha.SimpleCaptchaImpl",
			"com.liferay.portal.captcha.recaptcha.ReCaptchaImpl"
		},
		required = false
	)
	public String captchaEngine();

	@Meta.AD(name = "recaptcha-public-key", required = false)
	public String reCaptchaPublicKey();

	@Meta.AD(name = "recaptcha-private-key", required = false)
	public String reCaptchaPrivateKey();

}