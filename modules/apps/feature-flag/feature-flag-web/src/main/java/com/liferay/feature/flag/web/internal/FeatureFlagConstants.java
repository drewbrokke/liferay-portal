package com.liferay.feature.flag.web.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

/**
 * @author Drew Brokke
 */
public class FeatureFlagConstants {

	public static final String FEATURE_FLAG = "feature.flag";

	public static String getKey(String... parts) {
		return StringBundler.concat(
			FEATURE_FLAG, StringPool.PERIOD,
			StringUtil.merge(parts, StringPool.PERIOD));
	}

}
