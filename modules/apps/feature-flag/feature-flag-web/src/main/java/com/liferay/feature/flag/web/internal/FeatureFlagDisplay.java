package com.liferay.feature.flag.web.internal;

/**
 * @author Drew Brokke
 */
public class FeatureFlagDisplay {

	private final String _title;
	private final String _description;
	private final String _status;

	public String getKey() {
		return _key;
	}

	public String getId() {
		return _id;
	}

	private final String _id;

	private final String _key;
	private final boolean _enabled;

	public FeatureFlagDisplay(
		String title, String description, String status, String key, boolean enabled) {
		_title = title;
		_description = description;
		_status = status;
		_key = key;
		_enabled = enabled;

		_id = key;
	}

	public String getTitle() {
		return _title;
	}

	public String getDescription() {
		return _description;
	}

	public String getStatus() {
		return _status;
	}

	public boolean isEnabled() {
		return _enabled;
	}
}
