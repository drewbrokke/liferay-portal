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

package com.liferay.portal.service.impl;

import com.liferay.petra.lang.SafeClosable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * @author Minhchau Dang
 */
public class UserReindexManager {

	public static final UserReindexManager INSTANCE = new UserReindexManager();

	public void reindex(final List<User> users) throws SearchException {
		long[] userIds = ListUtil.toLongArray(users, User.USER_ID_ACCESSOR);

		reindex(userIds);
	}

	public void reindex(long... userIds) throws SearchException {
		List<UserIndexerRequest> newUserIndexerRequests = new ArrayList<>();

		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			User.class);

		for (long userId : userIds) {
			UserIndexerRequest userIndexerRequest = new UserIndexerRequest(
				userId, indexer);

			UserIndexerRequest newUserIndexerRequest =
				_queuedIndexerRequests.computeIfAbsent(
					userIndexerRequest, Function.identity());

			if (newUserIndexerRequest == userIndexerRequest) {
				newUserIndexerRequests.add(userIndexerRequest);
			}
		}

		if (ProxyModeThreadLocal.isForceSync()) {
			_reindex(newUserIndexerRequests);
		}
		else {
			_queuedIndexerRequestExecutorService.submit(
				() -> _syncReindex(newUserIndexerRequests));
		}
	}

	public void reindex(final User user) throws SearchException {
		reindex(user.getUserId());
	}

	private void _reindex(List<UserIndexerRequest> userIndexerRequests) {
		int i = 0;

		for (UserIndexerRequest userIndexerRequest : userIndexerRequests) {
			i++;

			if (userIndexerRequest.isExecuting()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Currently executing indexer request ", i,
							" in another thread: ", userIndexerRequest));
				}

				continue;
			}

			synchronized (userIndexerRequest) {
				if (userIndexerRequest.isIgnore()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Ignoring indexer request", i,
								" due to flag set in another thread ",
								userIndexerRequest));
					}

					continue;
				}

				if (userIndexerRequest.isExecuted()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Already executed indexer request", i,
								"in another thread: ", userIndexerRequest));
					}

					continue;
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Executing indexer request ", i, ": ",
							userIndexerRequest));
				}

				try {
					userIndexerRequest.execute();
				}
				finally {
					_queuedIndexerRequests.remove(userIndexerRequest);
				}
			}
		}
	}

	private void _syncReindex(List<UserIndexerRequest> userIndexerRequests) {
		try (SafeClosable safeClosable =
				ProxyModeThreadLocal.setWithSafeClosable(true)) {

			_reindex(userIndexerRequests);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserReindexManager.class);

	private final ExecutorService _queuedIndexerRequestExecutorService =
		Executors.newSingleThreadExecutor();
	private final ConcurrentMap<UserIndexerRequest, UserIndexerRequest>
		_queuedIndexerRequests = new ConcurrentHashMap<>();

	private class UserIndexerRequest {

		public UserIndexerRequest(long userId, Indexer<User> indexer) {
			_userId = userId;
			_indexer = indexer;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof UserIndexerRequest)) {
				return false;
			}

			UserIndexerRequest other = (UserIndexerRequest)o;

			return _userId.equals(other._userId);
		}

		public void execute() {
			if (_ignore) {
				return;
			}

			try {
				_executing = true;

				_indexer.reindex(User.class.getName(), _userId);

				_executed = true;
			}
			catch (SearchException se) {
				_log.error(se, se);
			}
			finally {
				_executing = false;
			}
		}

		@Override
		public int hashCode() {
			return _userId.hashCode();
		}

		public void ignore() {
			_ignore = true;
		}

		public boolean isExecuted() {
			return _executed;
		}

		public boolean isExecuting() {
			return _executing;
		}

		public boolean isIgnore() {
			return _ignore;
		}

		private boolean _executed;
		private boolean _executing;
		private boolean _ignore;
		private final Indexer<User> _indexer;
		private final Long _userId;

	}

}