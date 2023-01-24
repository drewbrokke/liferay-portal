package com.liferay.portal.kernel.mail;

/**
 * @author Drew Brokke
 */
public interface MailSettingsProvider {

	public MailSettings get(long companyId);

}
