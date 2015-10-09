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

package com.liferay.portal.kernel.search.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Andrew Betts
 */
public class ReindexStatusMessageIndexWriterWrapper
	implements InvocationHandler {

	public ReindexStatusMessageIndexWriterWrapper(IndexWriter indexWriter) {
		_indexWriter = indexWriter;
	}

	/*
	public void addDocument(SearchContext searchContext, Document document)
		throws SearchException;

	public void addDocuments(
			SearchContext searchContext, Collection<Document> documents)
		throws SearchException;

	public void deleteDocument(SearchContext searchContext, String uid)
		throws SearchException;

	public void deleteDocuments(
			SearchContext searchContext, Collection<String> uids)
		throws SearchException;

	public void deleteEntityDocuments(
			SearchContext searchContext, String className)
		throws SearchException;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			sendStatusMessage(method.getName(), args);
		}

		return method.invoke(_indexWriter, args);
	}

	public void partiallyUpdateDocument(
			SearchContext searchContext, Document document)
		throws SearchException;

	public void partiallyUpdateDocuments(
			SearchContext searchContext, Collection<Document> documents)
		throws SearchException;

	public void updateDocument(SearchContext searchContext, Document document)
		throws SearchException;

	public void updateDocuments(
			SearchContext searchContext, Collection<Document> documents)
		throws SearchException;

	 */

	private void sendStatusMessage(String methodName, Object[] args) {
		SearchContext searchContext = (SearchContext)args[0];

		ReindexStatusMessageSenderUtil.sendStatusMessage(
			methodName, searchContext);
	}

	private final IndexWriter _indexWriter;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			sendStatusMessage(method.getName(), args);
		}

		return method.invoke(_indexWriter, args);
	}

}