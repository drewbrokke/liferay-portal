/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.capability;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class CapabilityDirectiveManifestPluginTest {

	@Test
	public void testLeavesLowerCaseDirectivesUntouched() throws Exception {
		Manifest manifest = _createManifest(
			"osgi.ee;filter:=\"(osgi.ee=JavaSE)\",osgi.extender;filter:=\"" +
				"(osgi.extender=osgi.serviceloader.registrar)\";resolution:=" +
					"optional",
			null);

		_mainSet(manifest);

		Attributes attributes = manifest.getMainAttributes();

		Assert.assertEquals(
			"osgi.ee;filter:=\"(osgi.ee=JavaSE)\",osgi.extender;filter:=\"" +
				"(osgi.extender=osgi.serviceloader.registrar)\";resolution:=" +
					"optional",
			attributes.getValue(Constants.REQUIRE_CAPABILITY));
		Assert.assertNull(attributes.getValue(Constants.PROVIDE_CAPABILITY));
	}

	@Test
	public void testLowerCasesUpperCaseDirectives() throws Exception {
		Manifest manifest = _createManifest(
			"osgi.extender;filter:=\"(osgi.extender=osgi.serviceloader." +
				"registrar)\";resolution:=OPTIONAL,osgi.ee;filter:=\"" +
					"(osgi.ee=JavaSE)\"",
			"osgi.serviceloader;osgi.serviceloader=\"com.ctc.wstx.stax." +
				"WstxInputFactory\";effective:=ACTIVE;cardinality:=MULTIPLE");

		_mainSet(manifest);

		Attributes attributes = manifest.getMainAttributes();

		Assert.assertEquals(
			_toString(
				"osgi.extender;filter:=\"(osgi.extender=osgi.serviceloader." +
					"registrar)\";resolution:=optional,osgi.ee;filter:=\"" +
						"(osgi.ee=JavaSE)\""),
			_toString(attributes.getValue(Constants.REQUIRE_CAPABILITY)));
		Assert.assertEquals(
			_toString(
				"osgi.serviceloader;osgi.serviceloader=\"com.ctc.wstx.stax." +
					"WstxInputFactory\";effective:=active;cardinality:=" +
						"multiple"),
			_toString(attributes.getValue(Constants.PROVIDE_CAPABILITY)));
	}

	private Manifest _createManifest(
		String requireCapability, String provideCapability) {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		if (provideCapability != null) {
			attributes.putValue(
				Constants.PROVIDE_CAPABILITY, provideCapability);
		}

		if (requireCapability != null) {
			attributes.putValue(
				Constants.REQUIRE_CAPABILITY, requireCapability);
		}

		return manifest;
	}

	private void _mainSet(Manifest manifest) throws Exception {
		try (Analyzer analyzer = new Analyzer()) {
			CapabilityDirectiveManifestPlugin
				capabilityDirectiveManifestPlugin =
					new CapabilityDirectiveManifestPlugin();

			capabilityDirectiveManifestPlugin.mainSet(analyzer, manifest);
		}
	}

	private String _toString(String header) {
		Parameters parameters = new Parameters(header);

		return parameters.toString();
	}

}