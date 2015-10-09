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

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;

/**
 * @author Andrew Betts
 */
public class ReindexStatusMessageSenderImpl
	implements ReindexStatusMessageSender {

	@Override
	public void sendStatusMessage(String message) {
		ReindexBackgroundTaskStatusMessage reindexBackgroundTaskStatusMessage =
			new ReindexBackgroundTaskStatusMessage(message);

		_backgroundTaskStatusMessageSender.setBackgroundTaskStatusMessage(
			reindexBackgroundTaskStatusMessage);
	}

	@Override
	public void sendStatusMessage(
		String methodName, SearchContext searchContext) {

		ReindexBackgroundTaskStatusMessage reindexBackgroundTaskStatusMessage =
			new ReindexBackgroundTaskStatusMessage(methodName, searchContext);

		_backgroundTaskStatusMessageSender.setBackgroundTaskStatusMessage(
			reindexBackgroundTaskStatusMessage);
	}

	public void setBackgroundTaskStatusMessageSender(
		BackgroundTaskStatusMessageSender backgroundTaskStatusMessageSender) {

		_backgroundTaskStatusMessageSender = backgroundTaskStatusMessageSender;
	}

	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

}