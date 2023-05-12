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

package com.liferay.roles.admin.web.internal.display.context;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Drew Brokke
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NavItem {

	public static NavItem create(
		String label, Consumer<NavItem> navItemConsumer) {

		NavItem navItem = new NavItem(label);

		navItemConsumer.accept(navItem);

		return navItem;
	}

	public NavItem(String label) {
		this.label = label;
	}

	public void addItems(Collection<NavItem> navItem) {
		items.addAll(navItem);
	}

	public void addItems(NavItem... navItems) {
		addItems(Arrays.asList(navItems));
	}

	public void put(String key, Object value) {
		properties.put(key, value);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setInitialExpanded(boolean initialExpanded) {
		this.initialExpanded = initialExpanded;
	}

	@JsonProperty
	protected boolean active;

	@JsonProperty
	protected boolean initialExpanded;

	@JsonProperty
	protected List<NavItem> items = new ArrayList<>();

	@JsonProperty
	protected final String label;

	@JsonAnyGetter
	protected final Map<String, Object> properties = new HashMap<>();

}