package com.liferay.portal.kernel.mail;

/**
 * @author Drew Brokke
 */
public interface MailSettings {

	public String advancedProperties();
	public String pop3Host();
	public String pop3Password();
	public int pop3Port();
	public String pop3User();
	public String smtpHost();
	public String smtpPassword();
	public int smtpPort();
	public boolean smtpStartTLSEnable();
	public String smtpUser();
	public String storeProtocol();
	public String transportProtocol();
}
