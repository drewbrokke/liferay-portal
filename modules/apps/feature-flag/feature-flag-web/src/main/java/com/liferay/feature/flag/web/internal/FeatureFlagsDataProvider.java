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

package com.liferay.feature.flag.web.internal;

import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = "fds.data.provider.key=" + FeatureFlagFDSConstants.FRONTEND_DATA_SET_KEY,
	service = FDSDataProvider.class
)
public class FeatureFlagsDataProvider
	implements FDSDataProvider<FeatureFlagDisplay> {

	@Override
	public List<FeatureFlagDisplay> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<FeatureFlag>
			featureFlagsList = _getFeatureFlags(fdsKeywords, httpServletRequest);


		Comparator<FeatureFlag> comparator = Comparator.comparing(FeatureFlag::getKey);

		if (sort != null) {
			if (Objects.equals("title", sort.getFieldName())) {
				comparator = Comparator.comparing(FeatureFlag::getTitle);
			}
			else if (Objects.equals("status", sort.getFieldName())) {
				comparator = Comparator.comparing(
					featureFlag -> String.valueOf(featureFlag.getStatus()));
			}
			else if (Objects.equals("enabled", sort.getFieldName())) {
				comparator = Comparator.comparing(FeatureFlag::isEnabled);
			}

			if (sort.isReverse()) {
				comparator = comparator.reversed();
			}
		}

		featureFlagsList.sort(comparator);

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			fdsPagination.getStartPosition(), fdsPagination.getEndPosition(),
			featureFlagsList.size());

		return TransformUtil.transform(
			featureFlagsList.subList(startAndEnd[0], startAndEnd[1]),
			featureFlag -> new FeatureFlagDisplay(
				featureFlag.getTitle(), featureFlag.getDescription(),
				featureFlag.getStatus().toString(), featureFlag.getKey(), featureFlag.isEnabled()));
	}

	private List<FeatureFlag> _getFeatureFlags(
		FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest) {
		Locale locale = _portal.getLocale(httpServletRequest);

		FeatureFlags featureFlags = new FeatureFlags(
			locale,
			_portal.getCompanyId(httpServletRequest));

		if (Validator.isNotNull(fdsKeywords.getKeywords())) {
			return featureFlags.getFeatureFlags(featureFlag ->
				_contains(locale, featureFlag.getDescription(), fdsKeywords.getKeywords()) ||
				_contains(locale, featureFlag.getKey(), fdsKeywords.getKeywords()) ||
				_contains(locale, featureFlag.getTitle(), fdsKeywords.getKeywords()));
		}

		return featureFlags.getFeatureFlags();
	}

	private boolean _contains(Locale locale, String s1, String s2) {
		String normalized = _normalize(locale, s1);
		
		return normalized.contains(_normalize(locale, s2));
	}

	private String _normalize(Locale locale, String string) {
		return StringUtil.toLowerCase(string, locale);
	}

	@Reference
	private Portal _portal;

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<FeatureFlag>
			featureFlagsList = _getFeatureFlags(fdsKeywords, httpServletRequest);

		return featureFlagsList.size();
	}

}