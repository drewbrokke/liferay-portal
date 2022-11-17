package com.liferay.feature.flag.web.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsManagementToolbarDisplayContext extends
	SearchContainerManagementToolbarDisplayContext {


	public FeatureFlagsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		LabelItemList labelItemList = new LabelItemList();

		for (Filter filter : FILTERS) {
			String currentValue = filter.getCurrentValue(httpServletRequest);

			if (Objects.equals("all", currentValue)) {
				continue;
			}

			labelItemList.add(labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						filter.getParameterName(), (String)null
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					String.format(
						"%s: %s", langGet(filter.getName()),
						langGet(currentValue)));
			});
		}
		return labelItemList;
	}

	public static class Filter {
		public Filter(String name, String[] values, BiPredicate<FeatureFlag, String> biPredicate) {
			_name = name;
			_values = values;
			_biPredicate = biPredicate;
		}

		public String getCurrentValue(HttpServletRequest request) {
			return ParamUtil.get(
				request, getParameterName(), "all");
		}

		public String getParameterName() {
			return "filter-" + _name;
		}


		public String getName() {
			return _name;
		}

		public String[] getValues() {
			return _values;
		}


		public Predicate<FeatureFlag> getPredicate(HttpServletRequest request) {
			String currentValue = getCurrentValue(request);

			return featureFlag -> {
				if (Objects.equals("all", currentValue) ||
					_biPredicate.test(featureFlag, currentValue)) {

					return true;
				}

				return false;
			};
		}

		private final BiPredicate<FeatureFlag, String> _biPredicate;
		private final String _name;
		private final String[] _values;
	}

	public static final Filter[] FILTERS  = {
		new Filter(
			"status", new String[] {"dev", "beta", "release"},
			(featureFlag, currentValue) -> Objects.equals(
				currentValue, featureFlag.getStatusString())),
		new Filter(
			"enabled", new String[] {"enabled", "disabled"},
			(featureFlag, currentValue) -> {
				if ((currentValue == null) || Objects.equals(currentValue, "all")) {
					return true;
				}

				if (Objects.equals(currentValue, "enabled") &&
					featureFlag.isEnabled()) {

					return true;
				}

				if (Objects.equals(currentValue, "disabled") &&
					!featureFlag.isEnabled()) {

					return true;
				}

				return false;
			})
	};

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"status", "key", "enabled"};
	}

	@Override
	protected String getFilterNavigationDropdownItemsLabel() {
		return null;
	}

	protected String langGet(String key, String... args) {
		if (ArrayUtil.isEmpty(args)) {
			return LanguageUtil.get(httpServletRequest, key);
		}

		return LanguageUtil.format(httpServletRequest, key, args);
	}

	@Override
	public List<DropdownItem> getFilterNavigationDropdownItems() {
		DropdownItemList dropdownItemList = new DropdownItemList();

		for (Filter filter : FILTERS) {
			dropdownItemList.addGroup(
				dropdownGroupItem -> dropdownGroupItem.setLabel(
					langGet("filter-by-x", filter.getName())));

			String currentValue = filter.getCurrentValue(httpServletRequest);

			PortletURL portletURL = getPortletURL();

			for (String value : filter.getValues()) {
				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.setActive(Objects.equals(currentValue, value));
						dropdownItem.setLabel(langGet(value));
						dropdownItem.setHref(portletURL, filter.getParameterName(), value);
					}
				);
			}
		}

		return dropdownItemList;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}
}
