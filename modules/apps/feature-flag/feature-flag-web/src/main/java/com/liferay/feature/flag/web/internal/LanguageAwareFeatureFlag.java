package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class LanguageAwareFeatureFlag extends FeatureFlagWrapper {

	public LanguageAwareFeatureFlag(FeatureFlag featureFlag) {
		super(featureFlag);
	}

	@Override
	public String getDescription(Locale locale) {
		return LanguageUtil.get(
			locale, _getKey("description"), super.getDescription(locale));
	}

	@Override
	public String getTitle(Locale locale) {
		return LanguageUtil.get(locale, _getKey("title"), super.getTitle(locale));
	}

	private String _getKey(String suffix) {
		return "feature.flag." + getKey() + "." + suffix;
	}

}