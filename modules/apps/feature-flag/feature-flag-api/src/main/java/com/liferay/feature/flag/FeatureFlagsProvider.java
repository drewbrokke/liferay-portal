package com.liferay.feature.flag;

/**
 * @author Drew Brokke
 */
public interface FeatureFlagsProvider {

	public boolean isEnabled(long companyId, String key);

	public boolean isEnabled(String key);

}
