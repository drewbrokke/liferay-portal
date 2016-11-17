/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.captcha.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.BaseVerifyProcessTestCase;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.util.Dictionary;

import javax.portlet.PortletPreferences;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CaptchaPropertiesVerifyProcessTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(&(objectClass=" + VerifyProcess.class.getName() +
				")(verify.process.name=com.liferay.captcha.verify))");

		_serviceTracker = registry.trackServices(filter);

		_serviceTracker.open();

		Bundle bundle = FrameworkUtil.getBundle(
			CaptchaPropertiesVerifyProcessTest.class);

		_bundleContext = bundle.getBundleContext();

		ServiceReference<ConfigurationAdmin>
			configurationAdminServiceReference =
				_bundleContext.getServiceReference(ConfigurationAdmin.class);

		_configurationAdmin = _bundleContext.getService(
			configurationAdminServiceReference);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_serviceTracker.close();
	}

	@Test
	public void testVerifyConfigurations() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences();

		portletPreferences.setValue(
			"captcha.engine.impl", RandomTestUtil.randomString());
		portletPreferences.setValue(
			"captcha.engine.recaptcha.key.private",
			RandomTestUtil.randomString());
		portletPreferences.setValue(
			"captcha.engine.recaptcha.key.public",
			RandomTestUtil.randomString());

		portletPreferences.store();

		doVerify();

		Assert.assertNull(PrefsPropsUtil.getString("captcha.engine.impl"));
		Assert.assertNull(
			PrefsPropsUtil.getString("captcha.engine.recaptcha.key.private"));
		Assert.assertNull(
			PrefsPropsUtil.getString("captcha.engine.recaptcha.key.public"));

		Configuration configuration = _configurationAdmin.getConfiguration(
			CaptchaConfiguration.class.getName());

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertEquals(
			PrefsPropsUtil.getString(portletPreferences, "captcha.engine.impl"),
			properties.get("captchaEngine"));
		Assert.assertEquals(
			PrefsPropsUtil.getString(
				portletPreferences, "captcha.engine.recaptcha.key.private"),
			properties.get("reCaptchaPrivateKey"));
		Assert.assertEquals(
			PrefsPropsUtil.getString(
				portletPreferences, "captcha.engine.recaptcha.key.public"),
			properties.get("reCaptchaPublicKey"));
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return _serviceTracker.getService();
	}

	private static BundleContext _bundleContext;
	private static ConfigurationAdmin _configurationAdmin;
	private static ServiceTracker<VerifyProcess, VerifyProcess> _serviceTracker;

}