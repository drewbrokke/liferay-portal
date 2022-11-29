package com.liferay.feature.flag.web.internal;

import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsPropsHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsEnabled() {
		CentralizedThreadLocal<Long> companyIdThreadLocal =
			ReflectionTestUtil.getFieldValue(
				CompanyThreadLocal.class, "_companyId");

		try (SafeCloseable safeCloseable =
				 companyIdThreadLocal.setWithSafeCloseable(
					 CompanyConstants.SYSTEM)) {

			String key = "DEF-12345";

			withFeatureFlagPropsHelper(featureFlagsPropsHelper -> {
				Set<String> keySet = featureFlagsPropsHelper.getKeySet();

				Assert.assertFalse(keySet.contains(key));
				Assert.assertFalse(featureFlagsPropsHelper.isEnabled(key));
			});

			PropsTestUtil.setProps(FeatureFlagConstants.getKey(key), "false");

			withFeatureFlagPropsHelper(featureFlagsPropsHelper -> {
				Set<String> keySet = featureFlagsPropsHelper.getKeySet();

				Assert.assertTrue(keySet.contains(key));
				Assert.assertFalse(featureFlagsPropsHelper.isEnabled(key));
			});

			PropsTestUtil.setProps(FeatureFlagConstants.getKey(key), "true");

			withFeatureFlagPropsHelper(featureFlagsPropsHelper -> {
				Set<String> keySet = featureFlagsPropsHelper.getKeySet();

				Assert.assertTrue(keySet.contains(key));
				Assert.assertTrue(featureFlagsPropsHelper.isEnabled(key));
			});
		}

	}

	protected void withFeatureFlagPropsHelper(Consumer<FeatureFlagsPropsHelper> featureFlagsPropsHelperConsumer) {
		featureFlagsPropsHelperConsumer.accept(new FeatureFlagsPropsHelper());
	}
}