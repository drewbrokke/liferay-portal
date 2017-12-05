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

package com.liferay.user.associated.data.web.model.impl;

import com.liferay.user.associated.data.web.model.UADAsset;

import java.net.URL;

/**
 * @author William Newbury
 */
public abstract class UADAssetImpl implements UADAsset {

	public UADAssetImpl(long userId, UADAsset parent, UADAsset child) {
		_userId = userId;

		_parentUADAsset = parent;
		_childUADAsset = child;
	}

	@Override
	public UADAsset getChildUADAsset() {
		return _childUADAsset;
	}

	@Override
	public abstract URL getEditURL();

	@Override
	public UADAsset getParentUADAsset() {
		return _parentUADAsset;
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setChildUADAsset(UADAsset uadAsset) {
		_childUADAsset = uadAsset;
	}

	@Override
	public void setParentUADAsset(UADAsset uadAsset) {
		_parentUADAsset = uadAsset;
	}

	private UADAsset _childUADAsset;
	private UADAsset _parentUADAsset;
	private final long _userId;

}