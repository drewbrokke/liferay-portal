package com.liferay.feature.flag.web.internal;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagsDisplayContextFactory.class)
public class FeatureFlagsDisplayContextFactory {

	public FeatureFlagsDisplayContext create(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse
	) {
		FeatureFlagsDisplayContext featureFlagsDisplayContext =
			new FeatureFlagsDisplayContext();

		PortletRequest portletRequest = (PortletRequest)httpServletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse = (PortletResponse)httpServletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE);

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(portletRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(portletResponse);

		SearchContainer<FeatureFlagDisplay> searchContainer =
			new SearchContainer<>(
				portletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest, liferayPortletResponse),
				null, "no-feature-flags-found");

		searchContainer.setId(
			"accountEntryAccountGroupsSearchContainer");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-type", "asc"));

		Locale locale = _portal.getLocale(httpServletRequest);

		FeatureFlags featureFlags = new FeatureFlags(
			locale,
			_portal.getCompanyId(httpServletRequest));

		Predicate<FeatureFlag> predicate = _getKeywordsPredicate(
			locale, ParamUtil.getString(portletRequest, "keywords"));

		for (FeatureFlagsManagementToolbarDisplayContext.Filter filter : FeatureFlagsManagementToolbarDisplayContext.FILTERS) {
			predicate = predicate.and(filter.getPredicate(httpServletRequest));
		}

		List<FeatureFlagDisplay> featureFlagDisplays = TransformUtil.transform(
			featureFlags.getFeatureFlags(
				predicate),
			FeatureFlagDisplay::new);

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(),
			featureFlagDisplays.size());

		searchContainer.setResultsAndTotal(
			() -> featureFlagDisplays.subList(startAndEnd[0], startAndEnd[1]),
			featureFlagDisplays.size());

		featureFlagsDisplayContext.setSearchContainer(searchContainer);

		featureFlagsDisplayContext.setManagementToolbarDisplayContext(
			new FeatureFlagsManagementToolbarDisplayContext(
				httpServletRequest, liferayPortletRequest,
				liferayPortletResponse, searchContainer));

		return featureFlagsDisplayContext;
	}

	private Predicate<FeatureFlag> _getKeywordsPredicate(Locale locale, String keywords) {
		if (Validator.isNotNull(keywords)) {
			return featureFlag ->
				_contains(locale, featureFlag.getDescription(), keywords) ||
				_contains(locale, featureFlag.getKey(), keywords) ||
				_contains(locale, featureFlag.getTitle(), keywords);
		}

		return featureFlag -> true;
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

}
