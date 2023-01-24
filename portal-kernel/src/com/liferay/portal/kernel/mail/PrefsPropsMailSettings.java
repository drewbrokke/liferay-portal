package com.liferay.portal.kernel.mail;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import javax.portlet.PortletPreferences;
import java.util.function.Function;

/**
 * @author Drew Brokke
 */
public class PrefsPropsMailSettings implements MailSettings {

	public PrefsPropsMailSettings(long companyId) {
		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(companyId);
		PortletPreferences systemPortletPreferences =
			PrefsPropsUtil.getPreferences();

		function =
			(String key) -> companyPortletPreferences.getValue(
				key,
				systemPortletPreferences.getValue(key, PropsUtil.get(key)));
	}

	private final Function<String, String> function;
	@Override
	public String advancedProperties() {
		return function.apply(
			PropsKeys.MAIL_SESSION_MAIL_ADVANCED_PROPERTIES);
	}

	@Override
	public String pop3Host() {
		return function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_HOST);

	}

	@Override
	public String pop3Password() {
		return function.apply(
			PropsKeys.MAIL_SESSION_MAIL_POP3_PASSWORD);
	}

	@Override
	public int pop3Port() {
		return GetterUtil.getInteger(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_PORT));
	}

	@Override
	public String pop3User() {
		return function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_USER);
	}

	@Override
	public String smtpHost() {
		return function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_HOST);
	}

	@Override
	public String smtpPassword() {
		return function.apply(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_PASSWORD);
	}

	@Override
	public int smtpPort() {
		return GetterUtil.getInteger(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_PORT));
	}

	@Override
	public boolean smtpStartTLSEnable() {
		return GetterUtil.getBoolean(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_STARTTLS_ENABLE));
	}

	@Override
	public String smtpUser() {
		return function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_USER);
	}

	@Override
	public String storeProtocol() {
		return function.apply(
			PropsKeys.MAIL_SESSION_MAIL_STORE_PROTOCOL);
	}

	@Override
	public String transportProtocol() {
		return function.apply(
			PropsKeys.MAIL_SESSION_MAIL_TRANSPORT_PROTOCOL);
	}
}
