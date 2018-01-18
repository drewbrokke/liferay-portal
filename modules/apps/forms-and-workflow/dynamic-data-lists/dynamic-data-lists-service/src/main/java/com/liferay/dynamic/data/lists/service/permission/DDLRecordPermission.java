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

package com.liferay.dynamic.data.lists.service.permission;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @deprecated As of 1.2.0, with no direct replacement
 */
@Component(immediate = true)
@Deprecated
public class DDLRecordPermission {

	public static void check(
			PermissionChecker permissionChecker, DDLRecord record,
			String actionId)
		throws PortalException {

		_ddlRecordSetModelResourcePermission.check(
			permissionChecker, record.getRecordSetId(), actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long recordId, String actionId)
		throws PortalException {

		DDLRecord record = _ddlRecordLocalService.getDDLRecord(recordId);

		_ddlRecordSetModelResourcePermission.check(
			permissionChecker, record.getRecordSetId(), actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, DDLRecord record,
			String actionId)
		throws PortalException {

		return _ddlRecordSetModelResourcePermission.contains(
			permissionChecker, record.getRecordSetId(), actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long recordId, String actionId)
		throws PortalException {

		DDLRecord record = _ddlRecordLocalService.getDDLRecord(recordId);

		return _ddlRecordSetModelResourcePermission.contains(
			permissionChecker, record.getRecordSetId(), actionId);
	}

	@Reference(unbind = "-")
	protected void setDDLRecordLocalService(
		DDLRecordLocalService ddlRecordLocalService) {

		_ddlRecordLocalService = ddlRecordLocalService;
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecordSet)",
		unbind = "-"
	)
	protected void setModelResourcePermission(
		ModelResourcePermission<DDLRecordSet> modelResourcePermission) {

		_ddlRecordSetModelResourcePermission = modelResourcePermission;
	}

	private static DDLRecordLocalService _ddlRecordLocalService;
	private static ModelResourcePermission<DDLRecordSet>
		_ddlRecordSetModelResourcePermission;

}