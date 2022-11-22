package com.liferay.feature.flag.web.internal;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
@Component
public class FeatureFlagsConfigurationScreenRegistrar {

	private final List<ServiceRegistration<ConfigurationScreen>> serviceRegistrations = new ArrayList<>();

	@Activate
	protected void activate(BundleContext bundleContext) {
		for (FeatureFlag.Status status : FeatureFlag.Status.values()) {
			serviceRegistrations.add(bundleContext.registerService(
				ConfigurationScreen.class,
				new FeatureFlagsConfigurationScreen(status),
				new HashMapDictionary<>()));
		}
	}

	@Deactivate
	protected void deactivate() {
		serviceRegistrations.forEach(ServiceRegistration::unregister);

		serviceRegistrations.clear();
	}

	private class FeatureFlagsConfigurationScreen
		implements ConfigurationScreen {

		private final FeatureFlag.Status _status;

		public FeatureFlagsConfigurationScreen(FeatureFlag.Status status) {
			_status = status;
		}


		@Override
		public String getCategoryKey() {
			return FeatureFlagsConfigurationConstants.CONFIGURATION_CATEGORY_KEY;
		}

		@Override
		public String getKey() {
			return FeatureFlagsConfigurationConstants.getEntryKey(_status);
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, _status.toString());
		}

		@Override
		public String getScope() {
			return "company";
		}

		@Override
		public boolean isVisible() {
			return FeatureFlagsPropsUtil.isUIEnabled(_status);
		}

		@Override
		public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				_featureFlagsDisplayContextFactory.create(
					httpServletRequest, httpServletResponse, _status));

			try {
				RequestDispatcher requestDispatcher =
					_servletContext.getRequestDispatcher("/feature_flags.jsp");

				requestDispatcher.include(httpServletRequest, httpServletResponse);
			}
			catch (Exception exception) {
				throw new IOException(
					"Unable to render feature_flags.jsp", exception);
			}
		}
	}

	@Reference
	private FeatureFlagsDisplayContextFactory _featureFlagsDisplayContextFactory;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

}
