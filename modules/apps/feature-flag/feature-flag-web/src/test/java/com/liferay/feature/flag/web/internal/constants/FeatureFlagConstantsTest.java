package com.liferay.feature.flag.web.internal.constants;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class FeatureFlagConstantsTest {

	@Test
	public void testGetKey() {
		Assert.assertEquals("feature.flag", FeatureFlagConstants.getKey());
		Assert.assertEquals(
			"feature.flag.foo.bar", FeatureFlagConstants.getKey("foo", "bar"));
	}
}