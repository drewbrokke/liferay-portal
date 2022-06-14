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

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = "model.class.name=*",
	service = ConfigurationModelListener.class
)
public class GlobalConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(
		String pid, Dictionary<String, Object> properties) {

		System.out.format("Processing pid: %s\n", pid);

		try {
			for (ExtendedObjectClassDefinition.Scope scope :
				ExtendedObjectClassDefinition.Scope.values()) {

				Object portableIdentifier = properties.get(
					scope.getPortablePropertyKey());

				if (portableIdentifier == null) {
					continue;
				}

				Serializable internalIdentifier = _getInternalIdentifier(
					scope, (Serializable) portableIdentifier);

				if (internalIdentifier == null) {
					break;
				}

				properties.put(scope.getPropertyKey(), internalIdentifier);

				break;
			}
		}
		catch (PortalException portalException) {
			// TODO log the exception. The dev should know if something went wrong
		}
	}

	private Serializable _getInternalIdentifier(
			ExtendedObjectClassDefinition.Scope scope,
			Serializable portableIdentifier)
		throws PortalException {


		if (scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY)) {
			Company company = CompanyLocalServiceUtil.getCompanyByWebId(
				(String)portableIdentifier);

			return company.getCompanyId();
		}

		if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			String[] parts = StringUtil.split((String)portableIdentifier, "--");

			String webId = parts[0];

			long companyId = GetterUtil.getLong(
				_getInternalIdentifier(
					ExtendedObjectClassDefinition.Scope.COMPANY, webId));

			if (companyId == 0L) {
				return null;
			}

			String groupKey = parts[1];

			Group group = GroupLocalServiceUtil.getGroup(companyId, groupKey);

			return group.getGroupId();
		}

		return null;
	}

}