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

package com.liferay.frontend.taglib.liferay.ui.view.state.internal;

import com.liferay.frontend.taglib.liferay.ui.view.state.SearchContainerViewState;

/**
 * @author  Neil Griffin
 */
public class SearchContainerViewStateImpl implements SearchContainerViewState {

	public SearchContainerViewStateImpl(
		long categoryId, int cur, int delta, String displayStyle, int end,
		String keywords, String navigation, String orderByCol,
		String orderByType, boolean resetCur, int start, String tag) {

		_categoryId = categoryId;
		_cur = cur;
		_delta = delta;
		_displayStyle = displayStyle;
		_end = end;
		_keywords = keywords;
		_navigation = navigation;
		_orderByCol = orderByCol;
		_orderByType = orderByType;
		_resetCur = resetCur;
		_start = start;
		_tag = tag;
	}

	@Override
	public long getCategoryId() {
		return _categoryId;
	}

	@Override
	public int getCur() {
		return _cur;
	}

	@Override
	public int getDelta() {
		return _delta;
	}

	@Override
	public String getDisplayStyle() {
		return _displayStyle;
	}

	@Override
	public int getEnd() {
		return _end;
	}

	@Override
	public String getKeywords() {
		return _keywords;
	}

	@Override
	public String getNavigation() {
		return _navigation;
	}

	@Override
	public String getOrderByCol() {
		return _orderByCol;
	}

	@Override
	public String getOrderByType() {
		return _orderByType;
	}

	@Override
	public boolean getResetCur() {
		return _resetCur;
	}

	@Override
	public int getStart() {
		return _start;
	}

	@Override
	public String getTag() {
		return _tag;
	}

	private final long _categoryId;
	private final int _cur;
	private final int _delta;
	private final String _displayStyle;
	private final int _end;
	private final String _keywords;
	private final String _navigation;
	private final String _orderByCol;
	private final String _orderByType;
	private final boolean _resetCur;
	private final int _start;
	private final String _tag;

}