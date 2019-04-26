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

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletRequest;

/**
 * @author Drew Brokke
 */
public class JavascriptComponentNamespaceUtil {

	public static String namespace(PortletRequest portletRequest, String s) {
		StringBundler sb = new StringBundler((_PARAMS.length * 2) + 1);

		for (String param : _PARAMS) {
			String value = ParamUtil.get(
				portletRequest, param, StringPool.BLANK);

			if (Validator.isNotNull(value)) {
				sb.append(
					StringUtil.replace(
						value, CharPool.PERIOD, CharPool.UNDERLINE));
				sb.append(StringPool.UNDERLINE);
			}
		}

		sb.append(s);

		return sb.toString();
	}

	private static final String[] _PARAMS = {
		"scope", "applicationKey", "uadRegistryKey"
	};

}