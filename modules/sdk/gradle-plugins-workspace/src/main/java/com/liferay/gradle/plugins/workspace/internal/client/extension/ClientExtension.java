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

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientExtension {

	public Map<String, Object> toJSONMap(String pid) {
		Map<String, Object> jsonMap = new HashMap<>();

		Map<String, Object> configMap = new HashMap<>();

		configMap.put(
			"baseURL",
			unmappedProperties.getOrDefault(
				"baseURL", "${portalURL}/o/" + projectName));

		if (Objects.equals(classification, "configuration")) {
			configMap.put(
				"baseURL",
				unmappedProperties.getOrDefault(
					"baseURL",
					"$[conf:.serviceScheme]://$[conf:.serviceAddress]"));
		}

		configMap.put("description", description);
		configMap.put(
			"dxp.lxc.liferay.com.virtualInstanceId", virtualInstanceId);
		configMap.put("name", name);
		configMap.put("projectName", projectName);
		configMap.put("sourceCodeURL", sourceCodeURL);
		configMap.put("type", type);
		configMap.put(
			"webContextPath",
			unmappedProperties.getOrDefault(
				"webContextPath", "/" + projectName));

		configMap.putAll(unmappedProperties);

		if (Objects.equals(classification, "frontend")) {
			configMap.put("properties", _encode(properties));
			configMap.put("typeSettings", _encode(unmappedProperties));
		}

		jsonMap.put(pid + "~" + id, configMap);

		return jsonMap;
	}

	@JsonAnySetter
	public void unmappedProperty(String name, Object value) {
		unmappedProperties.put(name, value);
	}

	public String classification = "static";
	public String description = "";
	public String id;
	public String name = "";
	public String projectName;
	public Map<String, Object> properties = Collections.emptyMap();
	public String sourceCodeURL = "";
	public String type;

	@JsonIgnore
	public Map<String, Object> unmappedProperties = new HashMap<>();

	@JsonProperty("dxp.lxc.liferay.com.virtualInstanceId")
	public String virtualInstanceId = "default";

	private List<String> _encode(Map<String, Object> map) {
		Set<Map.Entry<String, Object>> set = map.entrySet();

		Stream<Map.Entry<String, Object>> stream = set.stream();

		return stream.map(
			entry -> {
				Object value = entry.getValue();

				if (value instanceof List) {
					value = StringUtil.merge(
						(List<?>)value, StringPool.NEW_LINE);
				}

				return StringBundler.concat(entry.getKey(), "=", value);
			}
		).collect(
			Collectors.toList()
		);
	}

}