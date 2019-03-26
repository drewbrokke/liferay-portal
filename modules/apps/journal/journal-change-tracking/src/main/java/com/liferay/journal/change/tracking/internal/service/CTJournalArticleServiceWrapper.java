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

package com.liferay.journal.change.tracking.internal.service;

import com.liferay.change.tracking.CTManager;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.service.JournalArticleServiceWrapper;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltan Csaszi
 */
@Component(immediate = true, service = ServiceWrapper.class)
public class CTJournalArticleServiceWrapper
	extends JournalArticleServiceWrapper {

	public CTJournalArticleServiceWrapper() {
		super(null);
	}

	public CTJournalArticleServiceWrapper(
		JournalArticleServiceWrapper journalFolderService) {

		super(journalFolderService);
	}

	@Override
	public List<JournalArticle> getArticlesByArticleId(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> obc) {

		return _journalArticleLocalService.dynamicQuery(
			_getArticlesByArticleIdDynamicQuery(groupId, articleId), start, end,
			obc);
	}

	@Override
	public int getArticlesCountByArticleId(long groupId, String articleId) {
		return (int)_journalArticleLocalService.dynamicQueryCount(
			_getArticlesByArticleIdDynamicQuery(groupId, articleId));
	}

	private DynamicQuery _getArticlesByArticleIdDynamicQuery(
		long groupId, String articleId) {

		long userId = PrincipalThreadLocal.getUserId();

		DynamicQuery query = _journalArticleLocalService.dynamicQuery();

		query.add(RestrictionsFactoryUtil.eq("groupId", groupId));
		query.add(RestrictionsFactoryUtil.eq("articleId", articleId));

		JournalArticleResource journalArticleResource =
			_journalArticleResourceLocalService.fetchArticleResource(
				groupId, articleId);

		if (journalArticleResource != null) {
			_ctManager.getActiveCTCollectionOptional(userId);

			List<CTEntry> ctEntries = _ctManager.getModelChangeCTEntries(
				userId, journalArticleResource.getResourcePrimKey());

			Stream<CTEntry> ctEntryStream = ctEntries.stream();

			List<Long> classPKs = ctEntryStream.map(
				CTEntry::getModelClassPK
			).collect(
				Collectors.toList()
			);

			query.add(RestrictionsFactoryUtil.in("id", classPKs));
		}

		return query;
	}

	@Reference
	private CTManager _ctManager;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

}