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

package com.liferay.announcements.web.internal.uad;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.user.associated.data.web.model.UADAsset;
import com.liferay.user.associated.data.web.model.UADService;
import com.liferay.user.associated.data.web.model.impl.UADServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author William Newbury
 */
@Component(
	immediate = true,
	property = {
		"uad.service.key=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
	},
	service = UADService.class
)
public class AnnouncementsUADServiceImpl extends UADServiceImpl {

	public AnnouncementsUADServiceImpl(String name) {
		super(name);
	}

	@Override
	public void process(long userId) {
		System.out.println("############################### process");

		List<UADAsset> announcementsUADAssets = new ArrayList<UADAsset>();

		uadAssets = announcementsUADAssets;
	}

	@Override
	public void autoAnonymize(UADAsset uadAsset) {
		System.out.println("############################### autoAnonymize");
	}

	@Override
	public void delete(UADAsset uadAsset) {
		System.out.println("############################### delete");
	}

	@Override
	public void export(UADAsset uadAsset) {
		System.out.println("############################### export");
	}

}