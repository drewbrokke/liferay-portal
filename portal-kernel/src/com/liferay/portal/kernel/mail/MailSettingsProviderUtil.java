package com.liferay.portal.kernel.mail;

import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Drew Brokke
 */
public class MailSettingsProviderUtil {

	public static MailSettings getMailSettings(long companyId) {
		if (_mailSettingsProvider != null) {
			return _mailSettingsProvider.get(companyId);
		}

		return new PrefsPropsMailSettings(companyId);
	}

	public static MailSettings getObfuscatedMailSettings(long companyId) {
		return new ObfuscatedMailSettings(getMailSettings(companyId));
	}

	private static volatile MailSettingsProvider _mailSettingsProvider =
		ServiceProxyFactory.newServiceTrackedInstance(
			MailSettingsProvider.class, MailSettingsProviderUtil.class,
			"_mailSettingsProvider", false, true);

}
