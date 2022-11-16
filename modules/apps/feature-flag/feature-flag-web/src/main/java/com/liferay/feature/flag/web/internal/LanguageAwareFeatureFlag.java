package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.language.Language;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class LanguageAwareFeatureFlag extends FeatureFlagWrapper {

	public LanguageAwareFeatureFlag(
		FeatureFlag featureFlag, Language language, Locale locale) {

		super(featureFlag);

		_language = language;
		_locale = locale;
	}

	@Override
	public String getDescription() {
		return _language.get(
			_locale, _getKey("description"), super.getDescription());
	}

	@Override
	public String getTitle() {
		return _language.get(_locale, _getKey("title"), super.getTitle());
	}

	private String _getKey(String suffix) {
		return "feature.flag." + getKey() + "." + suffix;
	}

	private final Language _language;
	private final Locale _locale;

}