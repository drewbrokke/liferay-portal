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

package com.liferay.announcements.uad;

import com.liferay.user.associated.data.model.UADAsset;
import com.liferay.user.associated.data.model.impl.UADAssetImpl;

import java.net.URL;

import java.util.List;

/**
 * @author William Newbury
 */
public class AnnouncementsUADAssetImpl extends UADAssetImpl {

	public AnnouncementsUADAssetImpl(
		long userId, List<UADAsset> childUADAssets) {

		super(userId, childUADAssets);
	}

	@Override
	public URL getEditURL() {
		System.out.println("############## Edit URL");

		return null;
	}

}