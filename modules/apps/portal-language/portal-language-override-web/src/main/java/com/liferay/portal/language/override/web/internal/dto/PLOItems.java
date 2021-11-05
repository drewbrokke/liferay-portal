/*
 * *
 *  * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package com.liferay.portal.language.override.web.internal.dto;

import java.util.List;

/**
 * @author Drew Brokke
 */
public class PLOItems {

	public PLOItems(
		List<PLOItemDTO> ploItemList, int total) {

		_ploItemList = ploItemList;
		_total = total;
	}

	private final List<PLOItemDTO> _ploItemList;
	private final int _total;

	public List<PLOItemDTO> getPLOItemList() {
		return _ploItemList;
	}

	public int getTotal() {
		return _total;
	}
}
