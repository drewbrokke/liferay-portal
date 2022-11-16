package com.liferay.feature.flag.web.internal;


/**
 * @author Drew Brokke
 */
public class PropertyFeatureFlag implements FeatureFlag {

	public PropertyFeatureFlag(
		String key, boolean enabled, Status status, String title,
		String description) {

		_key = key;
		_enabled = enabled;
		_status = status;
		_title = title;
		_description = description;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String getKey() {
		return _key;
	}

	@Override
	public Status getStatus() {
		return _status;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	private final String _description;
	private final boolean _enabled;
	private final String _key;
	private final Status _status;
	private final String _title;

}