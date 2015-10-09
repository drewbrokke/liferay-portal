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

package com.liferay.portal.search.backgroundtask;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessage;
import com.liferay.portal.kernel.search.SearchContext;

/**
 * @author Andrew Betts
 */
public class ReindexBackgroundTaskStatusMessage
	extends BackgroundTaskStatusMessage {

	public ReindexBackgroundTaskStatusMessage(String message) {
		put("message", message);
	}

	public ReindexBackgroundTaskStatusMessage(
		String methodName, SearchContext searchContext) {

		String[] entryClassNames = searchContext.getEntryClassNames();
		String entryClassName = entryClassNames[0];

		put("message", methodName + entryClassName);
	}

}