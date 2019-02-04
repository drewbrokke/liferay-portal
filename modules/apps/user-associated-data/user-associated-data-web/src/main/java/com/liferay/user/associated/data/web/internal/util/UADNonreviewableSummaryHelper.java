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

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.display.UADNonreviewableSummaryDisplay;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(immediate = true, service = UADNonreviewableSummaryHelper.class)
public class UADNonreviewableSummaryHelper {

	public SearchContainer<UADNonreviewableSummaryDisplay>
		createSearchContainer(
			RenderRequest renderRequest, RenderResponse renderResponse,
			long userId) {

		PortletRequest portletRequest =
			(PortletRequest)renderRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(
				(PortletResponse)renderRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE));

		PortletURL currentURL = PortletURLUtil.getCurrent(
			_portal.getLiferayPortletRequest(portletRequest),
			liferayPortletResponse);

		SearchContainer<UADNonreviewableSummaryDisplay> searchContainer =
			new SearchContainer<>(portletRequest, currentURL, null, null);

		searchContainer.setId("uadNonreviewableSummaryDisplays");

		searchContainer.setOrderByCol(getOrderByCol(renderRequest));
		searchContainer.setOrderByType(getOrderByType(renderRequest));

		Predicate<UADNonreviewableSummaryDisplay> predicate = getPredicate(
			getNavigation(renderRequest));

		List<UADNonreviewableSummaryDisplay> uadNonreviewableSummaryDisplays =
			getUADNonreviewableSummaryDisplays(portletRequest, userId);

		Supplier<Stream<UADNonreviewableSummaryDisplay>> streamSupplier =
			() -> {
				Stream<UADNonreviewableSummaryDisplay> stream =
					uadNonreviewableSummaryDisplays.stream();

				return stream.filter(predicate);
			};

		Stream<UADNonreviewableSummaryDisplay> summaryDisplayStream =
			streamSupplier.get();

		List<UADNonreviewableSummaryDisplay> results =
			summaryDisplayStream.sorted(
				getComparator(
					searchContainer.getOrderByCol(),
					searchContainer.getOrderByType())
			).skip(
				searchContainer.getStart()
			).limit(
				searchContainer.getDelta()
			).collect(
				Collectors.toList()
			);

		searchContainer.setResults(results);

		summaryDisplayStream = streamSupplier.get();

		searchContainer.setTotal((int)summaryDisplayStream.count());

		return searchContainer;
	}

	public List<UADAnonymizer> getApplicationUADAnonymizers(
		String applicationKey) {

		Stream<UADAnonymizer> uadAnonymizerStream =
			_uadRegistry.getApplicationUADAnonymizerStream(applicationKey);

		return uadAnonymizerStream.map(
			UADAnonymizer::getTypeClass
		).map(
			Class::getName
		).map(
			key -> _uadRegistry.getUADAnonymizer(key)
		).collect(
			Collectors.toList()
		);
	}

	public Comparator<UADNonreviewableSummaryDisplay> getComparator(
		String orderByColumn, String orderByType) {

		Comparator<UADNonreviewableSummaryDisplay> comparator =
			Comparator.comparing(
				UADNonreviewableSummaryDisplay::getApplicationKey);

		if (orderByColumn.equals("items") || orderByColumn.equals("status")) {
			comparator = Comparator.comparingInt(
				UADNonreviewableSummaryDisplay::getCount);
		}

		if (orderByType.equals("desc")) {
			comparator = comparator.reversed();
		}

		return comparator;
	}

	public String getDefaultUADRegistryKey(String applicationKey) {
		List<UADAnonymizer> uadAnonymizers =
			_uadRegistry.getApplicationUADAnonymizers(applicationKey);

		UADAnonymizer uadAnonymizer = uadAnonymizers.get(0);

		if (uadAnonymizer == null) {
			return null;
		}

		Class<?> typeClass = uadAnonymizer.getTypeClass();

		return typeClass.getName();
	}

	public String getNavigation(RenderRequest renderRequest) {
		return ParamUtil.getString(renderRequest, "navigation", "all");
	}

	public String getOrderByCol(RenderRequest renderRequest) {
		return ParamUtil.getString(renderRequest, "orderByCol", "name");
	}

	public String getOrderByType(RenderRequest renderRequest) {
		return ParamUtil.getString(renderRequest, "orderByType", "asc");
	}

	public Predicate<UADNonreviewableSummaryDisplay> getPredicate(
		String navigation) {

		if (navigation.equals("pending")) {
			return display -> display.getCount() > 0;
		}
		else if (navigation.equals("done")) {
			return display -> display.getCount() <= 0;
		}

		return display -> true;
	}

	public int getReviewableUADEntitiesCount(
		Stream<UADAnonymizer> uadAnonymizerStream, long userId) {

		return uadAnonymizerStream.mapToInt(
			uadAnonymizer -> {
				try {
					return (int)uadAnonymizer.count(userId);
				}
				catch (PortalException pe) {
					return 0;
				}
			}
		).sum();
	}

	public int getTotalReviewableUADEntitiesCount(long userId) {
		return getReviewableUADEntitiesCount(
			_uadRegistry.getUADAnonymizerStream(), userId);
	}

	public UADNonreviewableSummaryDisplay getUADNonreviewableSummaryDisplay(
		PortletRequest portletRequest, String applicationKey, long userId) {

		UADNonreviewableSummaryDisplay uadNonreviewableSummaryDisplay =
			new UADNonreviewableSummaryDisplay();

		Collection<UADAnonymizer> applicationUADAnonymizers =
			_uadRegistry.getApplicationUADAnonymizers(applicationKey);

		List<UADAnonymizer> nonreviewableUADAnonymizers = new ArrayList(
			applicationUADAnonymizers);

		List<Class> applicationUADDisplayTypeClasses = new ArrayList<>();

		Collection<UADDisplay> applicationUADDisplays =
			_uadRegistry.getApplicationUADDisplays(applicationKey);

		if (applicationUADDisplays != null) {
			for (UADDisplay applicationUADDisplay : applicationUADDisplays) {
				applicationUADDisplayTypeClasses.add(
					applicationUADDisplay.getTypeClass());
			}
		}

		for (UADAnonymizer applicationUADAnonymizer :
				applicationUADAnonymizers) {

			if (applicationUADDisplayTypeClasses.contains(
					applicationUADAnonymizer.getTypeClass())) {

				nonreviewableUADAnonymizers.remove(applicationUADAnonymizer);
			}
		}

		int count = getReviewableUADEntitiesCount(
			nonreviewableUADAnonymizers.stream(), userId);

		uadNonreviewableSummaryDisplay.setCount(count);

		uadNonreviewableSummaryDisplay.setApplicationKey(applicationKey);

		if (count > 0) {
			uadNonreviewableSummaryDisplay.setViewURL(
				getViewURL(portletRequest, applicationKey, userId));
		}

		return uadNonreviewableSummaryDisplay;
	}

	public List<UADNonreviewableSummaryDisplay>
		getUADNonreviewableSummaryDisplays(
			PortletRequest portletRequest, long userId) {

		List<UADNonreviewableSummaryDisplay> uadNonreviewableSummaryDisplays =
			new ArrayList<>();

		Set<String> applicationUADAnonymizerKeySet =
			_uadRegistry.getApplicationUADAnonymizersKeySet();

		Iterator<String> iterator = applicationUADAnonymizerKeySet.iterator();

		while (iterator.hasNext()) {
			String applicationKey = iterator.next();

			uadNonreviewableSummaryDisplays.add(
				getUADNonreviewableSummaryDisplay(
					portletRequest, applicationKey, userId));
		}

		uadNonreviewableSummaryDisplays.sort(
			(uadNonreviewableSummaryDisplay,
			 uadNonreviewableSummaryDisplay2) -> {
			String applicationKey1 =
				uadNonreviewableSummaryDisplay.getApplicationKey();

			return applicationKey1.compareTo(
				uadNonreviewableSummaryDisplay2.getApplicationKey());
		});

		return uadNonreviewableSummaryDisplays;
	}

	public String getViewURL(
		PortletRequest portletRequest, String applicationKey, long userId) {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			portletRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/view_uad_entities");
		liferayPortletURL.setParameter("p_u_i_d", String.valueOf(userId));
		liferayPortletURL.setParameter("applicationKey", applicationKey);
		liferayPortletURL.setParameter(
			"uadRegistryKey", getDefaultUADRegistryKey(applicationKey));

		return liferayPortletURL.toString();
	}

	@Reference
	private Portal _portal;

	@Reference
	private UADRegistry _uadRegistry;

}