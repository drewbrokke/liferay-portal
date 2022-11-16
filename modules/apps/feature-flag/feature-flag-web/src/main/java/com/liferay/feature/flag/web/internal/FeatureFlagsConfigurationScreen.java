package com.liferay.feature.flag.web.internal;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationScreen.class)
public class FeatureFlagsConfigurationScreen implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "feature-flags";
	}

	@Override
	public String getKey() {
		return "feature-flags";
	}

	@Override
	public String getName(Locale locale) {
		return "feature-flags";
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public void render(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_featureFlagsDisplayContextFactory.create(
				httpServletRequest, httpServletResponse));

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

	@Reference
	private FeatureFlagsDisplayContextFactory _featureFlagsDisplayContextFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

}
