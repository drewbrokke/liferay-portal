/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one;

import com.liferay.one.jira.service.OrganizationSynchronizer;
import com.liferay.one.service.OrganizationService;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Drew Brokke
 */
@RequestMapping("/object/action/organization/update")
@RestController
public class ObjectActionOrganizationUpdateRestController
	extends OneBaseRestController {

	@PostMapping
	public void post(@RequestBody String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);

		_organizationSynchronizer.syncOrganization(
			_organizationService.getOrganization(
				jsonObject.getLong("classPK")));
	}

	@Autowired
	private OrganizationService _organizationService;

	@Autowired
	private OrganizationSynchronizer _organizationSynchronizer;

}