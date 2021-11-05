<%--
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
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
	taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
	taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
	taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
	taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
	taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
	taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
	taglib uri="http://liferay.com/tld/text-localizer" prefix="liferay-text-localizer" %><%@
	taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
	taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
	taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
	page import="com.liferay.expando.kernel.model.ExpandoColumn" %><%@
	page import="com.liferay.expando.util.ExpandoAttributesUtil" %><%@
	page import="com.liferay.petra.portlet.url.builder.PortletURLBuilder" %><%@
	page import="com.liferay.petra.string.StringPool" %><%@
	page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
	page import="com.liferay.portal.kernel.exception.DuplicateRoleException" %><%@
	page import="com.liferay.portal.kernel.exception.RoleNameException" %><%@
	page import="com.liferay.portal.kernel.exception.UserEmailAddressException" %><%@
	page import="com.liferay.portal.kernel.exception.UserScreenNameException" %><%@
	page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
	page import="com.liferay.portal.kernel.model.Address" %><%@
	page import="com.liferay.portal.kernel.model.Group" %><%@
	page import="com.liferay.portal.kernel.model.ListType" %><%@
	page import="com.liferay.portal.kernel.model.ListTypeConstants" %><%@
	page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
	page import="com.liferay.portal.kernel.model.Organization" %><%@
	page import="com.liferay.portal.kernel.model.Phone" %><%@
	page import="com.liferay.portal.kernel.model.Role" %><%@
	page import="com.liferay.portal.kernel.model.User" %><%@
	page import="com.liferay.portal.kernel.model.role.RoleConstants" %><%@
	page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
	page import="com.liferay.portal.kernel.portlet.PortletProvider" %><%@
	page import="com.liferay.portal.kernel.portlet.PortletProviderUtil" %><%@
	page import="com.liferay.portal.kernel.security.auth.ScreenNameValidator" %><%@
	page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
	page import="com.liferay.portal.kernel.service.AddressLocalServiceUtil" %><%@
	page import="com.liferay.portal.kernel.service.ListTypeLocalServiceUtil" %><%@
	page import="com.liferay.portal.kernel.service.permission.PortletPermissionUtil" %><%@
	page import="com.liferay.portal.kernel.service.permission.UserPermissionUtil" %><%@
	page import="com.liferay.portal.kernel.util.Constants" %><%@
	page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
	page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
	page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
	page import="com.liferay.portal.kernel.util.HttpUtil" %><%@
	page import="com.liferay.portal.kernel.util.ListUtil" %><%@
	page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
	page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
	page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
	page import="com.liferay.portal.kernel.util.PrefsParamUtil" %><%@
	page import="com.liferay.portal.kernel.util.StringUtil" %><%@
	page import="com.liferay.portal.kernel.util.UnicodeFormatter" %><%@
	page import="com.liferay.portal.kernel.util.Validator" %><%@
	page import="com.liferay.portal.kernel.util.WebKeys" %><%@
	page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
	page import="com.liferay.portal.security.auth.ScreenNameValidatorFactory" %><%@
	page import="com.liferay.portal.util.PropsValues" %><%@
	page import="com.liferay.taglib.search.ResultRow" %><%@
	page import="com.liferay.users.admin.configuration.UserFileUploadsConfiguration" %>

<%@ page import="java.util.Collections" %><%@
	page import="java.util.List" %><%@
	page import="java.util.Map" %><%@
	page import="java.util.Objects" %><%@
	page import="java.util.Optional" %>

<%@ page import="javax.portlet.PortletURL" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />