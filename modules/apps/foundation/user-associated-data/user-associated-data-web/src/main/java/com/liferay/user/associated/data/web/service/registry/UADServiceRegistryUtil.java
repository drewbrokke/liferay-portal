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

package com.liferay.user.associated.data.web.service.registry;

import com.liferay.user.associated.data.web.model.UADService;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author William Newbury
 */
@Component
public class UADServiceRegistryUtil {

	public static UADService getSiteGenerator(String key) {
		return _uadServiceRegistry.getUADService(key);
	}

	public static Collection<UADService> getSiteGenerators() {
		return _uadServiceRegistry.getUADServices();
	}

	public static void notify(long userId) {
		_uadServiceRegistry.notify(userId);
	}

	@Reference(unbind = "-")
	protected void setSiteGeneratorRegistry(
		UADServiceRegistry uadServiceRegistry) {

		_uadServiceRegistry = uadServiceRegistry;
	}

	private static UADServiceRegistry _uadServiceRegistry;

}