/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.util.role;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public enum EmployeeRoles {

	CUSTOMER_EXPERIENCE_MANAGER(
		"C_CUSTOMER_EXPERIENCE_MANAGER", "Customer Experience Manager",
		"account"),
	LIFERAY_SALES("C_LIFERAY_SALES", "Liferay Sales", "account"),
	PRIMARY_CONTACT("C_PRIMARY_CONTACT", "Primary Contact", "account"),
	SECONDARY_CONTACT("C_SECONDARY_CONTACT", "Secondary Contact", "account"),
	SOLUTION_ARCHITECT("C_SOLUTION_ARCHITECT", "Solution Architect", "account");

	public static List<String> getNames() {
		List<String> names = new ArrayList<>();

		for (EmployeeRoles employeeRole : values()) {
			names.add(employeeRole.getName());
		}

		return names;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getName() {
		return _name;
	}

	public String getRoleType() {
		return _roleType;
	}

	private EmployeeRoles(
		String externalReferenceCode, String name, String roleType) {

		_externalReferenceCode = externalReferenceCode;
		_name = name;
		_roleType = roleType;
	}

	private final String _externalReferenceCode;
	private final String _name;
	private final String _roleType;

}