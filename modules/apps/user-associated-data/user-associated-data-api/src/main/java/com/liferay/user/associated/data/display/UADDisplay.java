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

package com.liferay.user.associated.data.display;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.user.associated.data.component.UADComponent;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author William Newbury
 */
public interface UADDisplay<T> extends UADComponent<T> {

	public long count(long userId);

	public T get(Serializable primaryKey) throws Exception;

	public default String[] getColumnFieldNames() {
		return getDisplayFieldNames();
	}

	public String[] getDisplayFieldNames();

	public default String getEditURL(
			T t, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return null;
	}

	public Map<String, Object> getFieldValues(T t, String[] fieldNames);

	public Serializable getPrimaryKey(T t);

	public List<T> getRange(long userId, int start, int end);

	/**
	 * Returns an array of strings defining the field names to be used as table
	 * column headers when sorting a list of entities of type {@code T}
	 *
	 * @return an array of field names used for column headers for sorting
	 * @reivew
	 */
	public String[] getSortingFieldNames();

	public String getTypeName(Locale locale);

	/**
	 * Returns an ordered range of all the entities of type {@code T} that match
	 * the keywords with the given userId and groupIds.
	 *
	 * @param userId the userId whose data to search
	 * @param groupIds the primary keys of the groups that the entities are
	 *                 associated with
	 * @param keywords the keywords which may occur in the entity's fields
	 * @param orderByField the field to sort the entities by
	 * @param orderByType the direction to sort the entities by, ascending or
	 *                    descending
	 * @param start the starting index of the result set
	 * @param end the ending index of the result set
	 * @return the ordered range of matching entities with the userId and
	 * gorupIds
	 * @review
	 */
	public List<T> search(
		long userId, long[] groupIds, String keywords, String orderByField,
		String orderByType, int start, int end);

	/**
	 * Returns a count of the number of entities of type {@code T} that match
	 * the keywords with the given userId and groupIds.
	 *
	 * @param userId the userId whose data to search
	 * @param groupIds the primary keys of the groups that the entities are
	 *                 associated with
	 * @param keywords the keywords which may occur in the entity's fields
	 * @return the number of matching entities with the userId and groupIds
	 * @review
	 */
	public long searchCount(long userId, long[] groupIds, String keywords);

}