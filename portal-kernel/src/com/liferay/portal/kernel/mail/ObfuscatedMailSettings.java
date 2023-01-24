package com.liferay.portal.kernel.mail;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class ObfuscatedMailSettings implements MailSettings {

	public ObfuscatedMailSettings(MailSettings mailSettings) {
		_mailSettings = mailSettings;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isOmniadmin()) {
			_allowServerProperties = true;
		}
	}

	private boolean _allowServerProperties;

	@Override
	public String advancedProperties() {
		return _mailSettings.advancedProperties();
	}

	@Override
	public String pop3Host() {
		// Restricted
		return _obfuscateValue(PropsKeys.MAIL_SESSION_MAIL_POP3_HOST, _mailSettings.pop3Host());
	}

	private String _obfuscateValue(String propertyKey, String value) {
		if (_allowServerProperties ||
			!Objects.equals(value, PropsUtil.get(propertyKey))) {
			
			return value;
		}
		
		return StringPool.BLANK;
	}
	
	private int _obfuscateValue(String propertyKey, int value) {
		String stringValue = _obfuscateValue(propertyKey, String.valueOf(value));

		if (Validator.isNotNull(stringValue)) {
			return value;
		}

		return 0;
	}

	@Override
	public String pop3Password() {
		return _obfuscatePassword(_mailSettings.pop3Password());
	}

	@Override
	public int pop3Port() {
		// Restricted

		return _obfuscateValue(PropsKeys.MAIL_SESSION_MAIL_POP3_PORT, _mailSettings.pop3Port());
	}

	@Override
	public String pop3User() {
		// Restricted
		return _obfuscateValue(PropsKeys.MAIL_SESSION_MAIL_POP3_USER, _mailSettings.pop3User());
	}

	@Override
	public String smtpHost() {
		// Restricted
		return _obfuscateValue(PropsKeys.MAIL_SESSION_MAIL_SMTP_HOST, _mailSettings.smtpHost());
	}

	@Override
	public String smtpPassword() {
		return _obfuscatePassword(_mailSettings.smtpPassword());
	}

	@Override
	public int smtpPort() {
		// Restricted
		return _obfuscateValue(PropsKeys.MAIL_SESSION_MAIL_SMTP_PORT, _mailSettings.smtpPort());
	}

	@Override
	public boolean smtpStartTLSEnable() {
		return _mailSettings.smtpStartTLSEnable();
	}

	@Override
	public String smtpUser() {
		// Restricted
		return _mailSettings.smtpUser();
	}

	@Override
	public String storeProtocol() {
		return _mailSettings.storeProtocol();
	}

	@Override
	public String transportProtocol() {
		return _mailSettings.transportProtocol();
	}

	private String _obfuscatePassword(String password) {
		if (Validator.isNotNull(password)) {
			return Portal.TEMP_OBFUSCATION_VALUE;
		}

		return StringPool.BLANK;
	}

	private final MailSettings _mailSettings;


}
