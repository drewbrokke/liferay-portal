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

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.factory.CETImplFactory;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Drew Brokke
 */
public class CETImplFactories {

	public CETImplFactories() {
		_cetImplFactories = HashMapBuilder.<String, CETImplFactory<?>>put(
			ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT,
			new CustomElementCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER,
			new FDSCellRendererCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			new GlobalCSSCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
			new GlobalJSCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_IFRAME,
			new IFrameCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_JS_IMPORT_MAPS_ENTRY,
			new JSImportMapsEntryCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_STATIC_CONTENT,
			new StaticContentCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_CSS,
			new ThemeCSSCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP,
			new ThemeSpritemapCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON,
			new ThemeFaviconCETImplFactoryImpl()

			// TODO

		/*).put(
			ClientExtensionEntryConstants.TYPE_THEME_JS,
			new ThemeJSCETImplFactoryImpl()*/

		).build();
	}

	public CETImplFactory<?> getCETImplFactory(String key) {
		if (_isDisabled(key)) {
			return null;
		}

		return _cetImplFactories.get(key);
	}

	public Set<String> getTypes() {
		SortedSet<String> types = new TreeSet<>(_cetImplFactories.keySet());

		types.removeIf(this::_isDisabled);

		return Collections.unmodifiableSortedSet(types);
	}

	private boolean _isDisabled(String key) {
		if ((key.equals(ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER) &&
			 !FeatureFlagManagerUtil.isEnabled("LPS-172904")) ||
			(key.equals(
				ClientExtensionEntryConstants.TYPE_JS_IMPORT_MAPS_ENTRY) &&
			 !FeatureFlagManagerUtil.isEnabled("LPS-172903")) ||
			(key.equals(ClientExtensionEntryConstants.TYPE_STATIC_CONTENT) &&
			 !FeatureFlagManagerUtil.isEnabled("LPS-177027")) ||
			(key.equals(ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP) &&
			 !FeatureFlagManagerUtil.isEnabled("LPS-166479"))) {

			return true;
		}

		return false;
	}

	private final Map<String, CETImplFactory<?>> _cetImplFactories;

}