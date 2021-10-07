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

package com.liferay.terms.of.use.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.terms.of.use.web.internal.confirmation.manager.TermsOfUseConfirmationManager;
import com.liferay.terms.of.use.web.internal.constants.TermsOfUseWebKeys;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = DynamicInclude.class)
public class TermsOfUseJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (_termsOfUseConfirmationManager.isShowTermsOfUse(
				_portal.getUserId(httpServletRequest))) {

			StringBundler sb = new StringBundler();

			Locale locale = _portal.getLocale(httpServletRequest);

			for (TermsOfUseEntry termsOfUseEntry :
					new TermsOfUseEntry[] {
						new CommerceTermsOfUseEntry(),
						new LiferayEnterpriseSearchTermsOfUseEntry()
					}) {

				sb.append("<div>");
				sb.append("<h4>");
				sb.append(termsOfUseEntry.getDisplayName(locale));
				sb.append("</h3>");
				sb.append("<div>");
				sb.append(termsOfUseEntry.getBodyHTML(locale));
				sb.append("</div>");
				sb.append("</div>");
				sb.append("</br>");
			}

			httpServletRequest.setAttribute(
				TermsOfUseWebKeys.MODAL_BODY_HTML, sb.toString());

			super.include(httpServletRequest, httpServletResponse, key);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/terms_of_use.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.terms.of.use.web)",
		unbind = "-"
	)
	protected void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TermsOfUseJSPDynamicInclude.class);

	@Reference
	private Portal _portal;

	@Reference
	private TermsOfUseConfirmationManager _termsOfUseConfirmationManager;

}