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

package com.liferay.user.associated.data.registry;

import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.anonymizer.UADEntityAnonymizer;
import com.liferay.user.associated.data.display.UADEntityDisplay;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.exporter.UADEntityExporter;
import com.liferay.user.associated.data.util.UADEntitySetComposite;
import com.liferay.user.associated.data.util.UADEntityTypeComposite;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author William Newbury
 */
@Component(immediate = true)
public class UADRegistryUtil {

	public static UADEntityAggregator getUADEntityAggregator(String key) {
		return _uadRegistry.getUADEntityAggregator(key);
	}

	public static UADEntityAggregator getUADEntityAggregator(
		UADEntity uadEntity) {

		return _uadRegistry.getUADEntityAggregator(uadEntity);
	}

	public static Set<String> getUADEntityAggregatorKeySet() {
		return _uadRegistry.getUADEntityAggregatorKeySet();
	}

	public static Collection<UADEntityAggregator> getUADEntityAggregators() {
		return _uadRegistry.getUADEntityAggregators();
	}

	public static UADEntityAnonymizer getUADEntityAnonymizer(String key) {
		return _uadRegistry.getUADEntityAnonymizer(key);
	}

	public static UADEntityAnonymizer getUADEntityAnonymizer(
		UADEntity uadEntity) {

		return _uadRegistry.getUADEntityAnonymizer(uadEntity);
	}

	public static Set<String> getUADEntityAnonymizerKeySet() {
		return _uadRegistry.getUADEntityAnonymizerKeySet();
	}

	public static Collection<UADEntityAnonymizer> getUADEntityAnonymizers() {
		return _uadRegistry.getUADEntityAnonymizers();
	}

	public static UADEntityDisplay getUADEntityDisplay(String key) {
		return _uadRegistry.getUADEntityDisplay(key);
	}

	public static UADEntityDisplay getUADEntityDisplay(UADEntity uadEntity) {
		return _uadRegistry.getUADEntityDisplay(uadEntity);
	}

	public static Set<String> getUADEntityDisplayKeySet() {
		return _uadRegistry.getUADEntityDisplayKeySet();
	}

	public static UADEntityExporter getUADEntityExporter(String key) {
		return _uadRegistry.getUADEntityExporter(key);
	}

	public static UADEntityExporter getUADEntityExporter(UADEntity uadEntity) {
		return _uadRegistry.getUADEntityExporter(uadEntity);
	}

	public static Set<String> getUADEntityExporterKeySet() {
		return _uadRegistry.getUADEntityExporterKeySet();
	}

	public static Collection<UADEntityExporter> getUADEntityExporters() {
		return _uadRegistry.getUADEntityExporters();
	}

	public static List<UADEntitySetComposite> getUADEntitySetComposites(
		long userId) {

		return _uadRegistry.getUADEntitySetComposites(userId);
	}

	public static UADEntityTypeComposite getUADEntityTypeComposite(
		long userId, String key, int start, int end) {

		return _uadRegistry.getUADEntityTypeComposite(userId, key, start, end);
	}

	public static List<UADEntityTypeComposite> getUADEntityTypeComposites(
		long userId, String uadEntitySetName) {

		return _uadRegistry.getUADEntityTypeComposites(
			userId, uadEntitySetName);
	}

	@Reference(unbind = "-")
	protected void setUADRegistry(UADRegistry uadRegistry) {
		_uadRegistry = uadRegistry;
	}

	private static UADRegistry _uadRegistry;

}