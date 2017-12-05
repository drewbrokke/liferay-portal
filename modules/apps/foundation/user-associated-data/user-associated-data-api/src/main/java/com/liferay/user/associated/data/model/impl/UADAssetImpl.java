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

package com.liferay.user.associated.data.model.impl;

import com.liferay.user.associated.data.model.UADAsset;

import java.net.URL;

import java.util.List;

/**
 * @author William Newbury
 */
public abstract class UADAssetImpl implements UADAsset {

	public UADAssetImpl(long userId, List<UADAsset> childrenUADAssets) {
		_userId = userId;

		_childrenUADAssets = childrenUADAssets;
	}

	@Override
	public List<UADAsset> getChildrenUADAssets() {
		return _childrenUADAssets;
	}

	@Override
	public abstract URL getEditURL();

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setChildrenUADAssets(List<UADAsset> childrenUADAssets) {
		_childrenUADAssets = childrenUADAssets;
	}

	private List<UADAsset> _childrenUADAssets;
	private final long _userId;

}