/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.capability;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.service.ManifestPlugin;

import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Lower cases the values of the standard capability directives so that
 * embedded jars compiled against the enum based bnd annotations (bnd 6 and
 * earlier) do not leak enum constant names such as {@code OPTIONAL} into the
 * generated headers, which bnd 7 then rejects during verification.
 *
 * @author Drew Brokke
 */
public class CapabilityDirectiveManifestPlugin implements ManifestPlugin {

	@Override
	public void mainSet(Analyzer analyzer, Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();

		for (String headerName : _HEADER_NAMES) {
			String headerValue = attributes.getValue(headerName);

			if (headerValue == null) {
				continue;
			}

			Parameters parameters = new Parameters(headerValue, analyzer, true);

			boolean modified = false;

			for (Attrs attrs : parameters.values()) {
				for (String directiveName : _DIRECTIVE_NAMES) {
					String directiveValue = attrs.get(directiveName);

					if (directiveValue == null) {
						continue;
					}

					String lowerCaseDirectiveValue = directiveValue.toLowerCase(
						Locale.ROOT);

					if (lowerCaseDirectiveValue.equals(directiveValue)) {
						continue;
					}

					attrs.put(directiveName, lowerCaseDirectiveValue);

					modified = true;
				}
			}

			if (modified) {
				attributes.putValue(headerName, parameters.toString());
			}
		}
	}

	private static final String[] _DIRECTIVE_NAMES = {
		"cardinality:", "effective:", "resolution:"
	};

	private static final String[] _HEADER_NAMES = {
		Constants.PROVIDE_CAPABILITY, Constants.REQUIRE_CAPABILITY
	};

}