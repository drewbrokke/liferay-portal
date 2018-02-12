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

package com.liferay.user.associated.data.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.entity.UADEntity;

/**
 * @author Noah Sherrill
 */
public class UADEntityChunkedCommandUtil {

	public static void executeChunkedCommand(
			long userId, UADEntityAggregator uadEntityAggregator,
			UnsafeConsumer<UADEntity, PortalException> uadEntityUnsafeConsumer)
		throws PortalException {

		executeChunkedCommand(
			userId, uadEntityAggregator, uadEntityUnsafeConsumer,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public static void executeChunkedCommand(
			long userId, UADEntityAggregator uadEntityAggregator,
			UnsafeConsumer<UADEntity, PortalException> uadEntityUnsafeConsumer,
			int start, int end)
		throws PortalException {

		int chunk_end = _CHUNK_SIZE;
		int chunk_start = 0;

		while (chunk_start < end) {
			if (chunk_end > end) {
				chunk_end = end;
			}

			for (UADEntity uadEntity :
					uadEntityAggregator.getUADEntities(
						userId, chunk_start, chunk_end)) {

				uadEntityUnsafeConsumer.accept(uadEntity);
			}

			chunk_start = chunk_end;
			chunk_end += _CHUNK_SIZE;
		}
	}

	private static final int _CHUNK_SIZE = 100;

}