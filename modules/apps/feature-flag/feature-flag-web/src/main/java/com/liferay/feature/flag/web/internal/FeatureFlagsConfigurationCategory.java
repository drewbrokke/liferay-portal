package com.liferay.feature.flag.web.internal;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationCategory.class)
public class FeatureFlagsConfigurationCategory
	implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "flag-full";
	}

	@Override
	public String getCategoryKey() {
		return FeatureFlagsConfigurationConstants.CONFIGURATION_CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}
}
