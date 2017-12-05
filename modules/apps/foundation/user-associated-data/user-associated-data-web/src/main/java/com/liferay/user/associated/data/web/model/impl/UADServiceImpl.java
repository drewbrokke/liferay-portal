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
import com.liferay.user.associated.data.web.model.UADService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author William Newbury
 */
public abstract class UADServiceImpl implements UADService {

	public UADServiceImpl(String name) {
		_name = name;

		_uadAssetsMap = new HashMap<>();
		uadAssets = new ArrayList<>();
	}

	@Override
	public abstract void autoAnonymize(UADAsset uadAsset);

	@Override
	public void autoAnonymizeAll(long userId) {
		for (UADAsset uadAsset : getUADAssets(userId)) {
			autoAnonymize(uadAsset);
		}
	}

	@Override
	public long count(long userId) {
		List<UADAsset> uadAssets = getUADAssets(userId);

		return uadAssets.size();
	}

	@Override
	public abstract void delete(UADAsset uadAsset);

	@Override
	public void deleteAll(long userId) {
		for (UADAsset uadAsset : getUADAssets(userId)) {
			delete(uadAsset);
		}
	}

	@Override
	public abstract void export(UADAsset uadAsset);

	@Override
	public void exportAll(long userId) {
		for (UADAsset uadAsset : getUADAssets(userId)) {
			export(uadAsset);
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public List<UADAsset> getUADAssets(long userId) {
		if (_uadAssetsMap.containsKey(userId)) {
			return _uadAssetsMap.get(userId);
		}

		List<UADAsset> uadAssets = new ArrayList<>();

		for (UADAsset uadAsset : uadAssets) {
			if (uadAsset.getUserId() == userId) {
				uadAssets.add(uadAsset);
			}
		}

		_uadAssetsMap.put(userId, uadAssets);

		return uadAssets;
	}

	@Override
	public abstract void process(long userId);

	protected List<UADAsset> uadAssets;

	private final String _name;
	private final Map<Long, List<UADAsset>> _uadAssetsMap;

}