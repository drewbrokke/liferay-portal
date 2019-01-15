/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.user.associated.data.display;

/**
 * @author Pei-Jung Lan
 */
public class UADContainerEntity<T> {

	public UADContainerEntity(T entity) {
		_entity = entity;
		_count = 0;
	}

	public UADContainerEntity(T entity, long count) {
		_entity = entity;
		_count = count;
	}

	public long getCount() {
		return _count;
	}

	public T getEntity() {
		return _entity;
	}

	public Class getTypeClass() {
		return _entity.getClass();
	}

	private final long _count;
	private final T _entity;

}