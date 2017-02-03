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

package com.liferay.portal.kernel.captcha;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Pei-Jung Lan
 */
@ProviderType
public interface CaptchaSettings {

	public String getCaptchaEngine();

	public boolean getCreateAccountCaptchaEnabled();

	public int getMaxChallenges();

	public boolean getMessageBoardsEditCategoryCaptchaEnabled();

	public boolean getMessageBoardsEditMessageCaptchaEnabled();

	public String getReCaptchaNoScriptUrl();

	public String getReCaptchaPrivateKey();

	public String getReCaptchaPublicKey();

	public String getReCaptchaScriptUrl();

	public String getReCaptchaVerifyUrl();

	public boolean getSendPasswordCaptchaEnabled();

	public String[] getSimpleCaptchaBackgroundProducers();

	public String[] getSimpleCaptchaGimpyRenderers();

	public int getSimpleCaptchaHeight();

	public String[] getSimpleCaptchaNoiseProducers();

	public String[] getSimpleCaptchaTextProducers();

	public int getSimpleCaptchaWidth();

	public String[] getSimpleCaptchaWordRenderers();

}