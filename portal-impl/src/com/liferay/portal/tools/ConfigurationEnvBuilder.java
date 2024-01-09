/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Matthew Tambara
 */
public class ConfigurationEnvBuilder {

	public static void main(String[] args) throws IOException {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String[] configurationJavaFileNames = StringUtil.split(
			arguments.get("configuration.java.files"), '\n');

		Path rootPath = Paths.get(arguments.getOrDefault("root.dir", "."));

		Path realPath = rootPath.toRealPath();

		new JSONFactoryUtil(
		).setJSONFactory(
			new JSONFactoryImpl()
		);

		Path languagePropertiesPath = Paths.get(
			realPath.toString(),
			"modules/apps/portal-language/portal-language-lang/src/main" +
				"/resources/content/Language.properties");

		languageProperties.load(
			new FileReader(languagePropertiesPath.toFile()));

		List<ObjectDef> objectDefs = getObjectDefs(
			configurationJavaFileNames, realPath.toString());

		Path path = Paths.get(arguments.get("properties.output.file"));

		String content = new String(Files.readAllBytes(path));

		int index = content.indexOf("##\n## OSGi Configuration Overrides");

		content = content.substring(0, index);

		content = content.concat(buildContent(objectDefs));

		Files.write(path, content.getBytes());

		String jsonString = generateJSONString(objectDefs);

		Files.write(Paths.get(arguments.get("json.schema.output.file")), jsonString.getBytes());
	}

	protected static String buildContent(List<ObjectDef> objectDefs) {
		StringBundler sb = new StringBundler();

		sb.append("##\n## OSGi Configuration Overrides\n##\n");

		for (ObjectDef objectDef : objectDefs) {
			String fullyQualifiedName = objectDef.pid;

			for (AttributeDef attributeDef : objectDef.attributeDefs) {
				String configurationKey = StringBundler.concat(
					"configuration.override.", fullyQualifiedName,
					StringPool.UNDERLINE, attributeDef.name);

				sb.append("\n");
				sb.append("    #\n");
				sb.append("    # Env: ");
				sb.append(
					ToolsUtil.encodeEnvironmentProperty(configurationKey));
				sb.append("\n");
				sb.append("    #\n");
				sb.append("    #");
				sb.append(configurationKey);
				sb.append(StringPool.EQUAL);
			}
		}

		return sb.toString();
	}

	protected static String buildContent(
			String[] configurationJavaFileNames, Path rootPath)
		throws IOException {

		StringBundler sb = new StringBundler();

		sb.append("##\n## OSGi Configuration Overrides\n##\n");

		Matcher matcher = _pattern.matcher("");

		for (String configurationJavaFileName : configurationJavaFileNames) {
			if (configurationJavaFileName.contains(
					"/build/compile-include-sources/")) {

				continue;
			}

			String fullyQualifiedName = configurationJavaFileName.substring(
				configurationJavaFileName.indexOf(
					StringBundler.concat("com", File.separator, "liferay")),
				configurationJavaFileName.indexOf(".java"));

			fullyQualifiedName = StringUtil.replace(
				fullyQualifiedName, File.separator, StringPool.PERIOD);

			Path path = rootPath.resolve(Paths.get(configurationJavaFileName));

			for (String line : Files.readAllLines(path)) {
				if (line.contains("public class")) {
					break;
				}

				matcher.reset(line);

				if (matcher.matches()) {
					String configurationKey = StringBundler.concat(
						"configuration.override.", fullyQualifiedName,
						StringPool.UNDERLINE, matcher.group(1));

					sb.append("\n");
					sb.append("    #\n");
					sb.append("    # Env: ");
					sb.append(
						ToolsUtil.encodeEnvironmentProperty(configurationKey));
					sb.append("\n");
					sb.append("    #\n");
					sb.append("    #");
					sb.append(configurationKey);
					sb.append(StringPool.EQUAL);
				}
			}
		}

		return sb.toString();
	}

	protected static ObjectDef constructObjectDef(
		String configurationFilePath, String rootDir) {

		List<String> lines;

		try {
			lines = Files.readAllLines(
				Paths.get(rootDir, configurationFilePath));
		}
		catch (IOException ioException) {
			_log.error(
				String.format(
					"Could not read configuration file %s%n",
					configurationFilePath),
				ioException);

			return null;
		}

		ObjectDef objectDef = new ObjectDef();

		AttributeDef attributeDef = new AttributeDef();

		for (String line : lines) {
			if (objectDef.interfaceName == null) {
				withMatcher(line, objectDef, objectDefCategoryPattern);
				withMatcher(
					line, objectDef, objectDefDescriptionPattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.description =
							languageProperties.getProperty(
								curObjectDef.description));
				withMatcher(
					line, objectDef, objectDefMetaAnnotationPattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.hasMetaAnnotation = true);
				withMatcher(line, objectDef, objectDefPidPattern);
				withMatcher(
					line, objectDef, objectDefScopePattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.scope = StringUtil.lowerCase(
							curObjectDef.scope));
				withMatcher(
					line, objectDef, objectDefTitlePattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.title = languageProperties.getProperty(
							curObjectDef.title));

				withMatcher(line, objectDef, objectDefInterfaceNamePattern);

				continue;
			}

			withMatcher(line, objectDef, objectDefExtendsInterfaceNamePattern);

			if (!StringUtil.startsWith(objectDef.pid, "com.liferay")) {
				String fullyQualifiedName = configurationFilePath.substring(
					configurationFilePath.indexOf(
						StringBundler.concat("com", File.separator, "liferay")),
					configurationFilePath.indexOf(".java"));

				fullyQualifiedName = StringUtil.replace(
					fullyQualifiedName, File.separator, StringPool.PERIOD);

				objectDef.pid = fullyQualifiedName;
			}

			withMatcher(line, attributeDef, attributeDefaultValuePattern);
			withMatcher(
				line, attributeDef, attributeDeprecatedPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.deprecated = true);
			withMatcher(
				line, attributeDef, attributeDescriptionPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.description =
						languageProperties.getProperty(
							curAttributeDef.description));
			withMatcher(line, attributeDef, attributeMaxPattern);
			withMatcher(
				line, attributeDef, attributeDefMetaAnnotationPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.hasMetaAnnotation = true);
			withMatcher(line, attributeDef, attributeMinPattern);
			withMatcher(line, attributeDef, attributeOptionLabelsPattern);
			withMatcher(line, attributeDef, attributeOptionValuesPattern);
			withMatcher(line, attributeDef, attributeRequiredInputPattern);
			withMatcher(line, attributeDef, attributeRequiredPattern);
			withMatcher(
				line, attributeDef, attributeTitlePattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.title = languageProperties.getProperty(
						curAttributeDef.title));

			withMatcher(line, attributeDef, attributeTypeNamePattern);

			if (attributeDef.name == null) {
				continue;
			}

			if (attributeDef.requiredInput) {
				attributeDef.required = true;
			}

			if (attributeDef.defaultValue != null) {
				if (attributeDef.isBoolean()) {
					attributeDef.defaultValue = toBoolean(
						attributeDef.defaultValue);
				}

				if (attributeDef.isNumber()) {
					attributeDef.defaultValue = toNumber(
						String.valueOf(attributeDef.defaultValue));
				}

				if (StringUtil.startsWith(
						String.valueOf(attributeDef.defaultValue), "${")) {

					attributeDef.defaultValue = null;
				}
			}

			if (ArrayUtil.isNotEmpty(attributeDef.optionValues) &&
				attributeDef.isNumber()) {

				Number[] optionValues = {};

				for (Object optionValue : attributeDef.optionValues) {
					optionValues = ArrayUtil.append(
						optionValues, toNumber(String.valueOf(optionValue)));
				}

				attributeDef.optionValues = optionValues;
			}

			if (ArrayUtil.isNotEmpty(attributeDef.optionValues) &&
				attributeDef.isNumber()) {

				Number[] optionValues = {};

				for (Object optionValue : attributeDef.optionValues) {
					optionValues = ArrayUtil.append(
						optionValues, toNumber(String.valueOf(optionValue)));
				}

				attributeDef.optionValues = optionValues;
			}

			if (objectDef.hasMetaAnnotation || attributeDef.hasMetaAnnotation) {
				objectDef.attributeDefs.add(attributeDef);
			}

			attributeDef = new AttributeDef();
		}

		if (ListUtil.isEmpty(objectDef.attributeDefs)) {
			return null;
		}

		return objectDef;
	}

	protected static String generateJSONString(List<ObjectDef> objectDefs) {
		JSONObject schemaJSONObject = jsonObject(
			jsonObject -> jsonObject.put(
				"oneOf", jsonArray()
			).put(
				"properties", jsonObject("pid", jsonObject("enum", jsonArray()))
			));

		for (ObjectDef objectDef : objectDefs) {
			if (!Objects.equals(objectDef.scope, "company")) {
				if (_log.isInfoEnabled()) {
					_log.info(
						String.format(
							"Scope for %s is %s, SKIPPING %n", objectDef.pid,
							objectDef.scope));
				}

				continue;
			}

			JSONObject typeSchemaJSONObject = jsonObject(
				"description", () -> objectDef.description
			).put(
				"properties",
				jsonObject(
					"pid",
					jsonObject(
						"const", objectDef.pid
					).put(
						"description", () -> objectDef.description
					).put(
						"title", () -> objectDef.title
					))
			).put(
				"required", jsonArray("pid")
			).put(
				"title", () -> objectDef.title
			);

			for (AttributeDef attributeDef : objectDef.attributeDefs) {
				JSONObject propertySchemaJSONObject = jsonObject(
					"default", () -> attributeDef.defaultValue
				).put(
					"deprecated", () -> attributeDef.deprecated
				).put(
					"description", () -> attributeDef.description
				).put(
					"title", () -> attributeDef.title
				).put(
					"type", () -> attributeDef.type
				);

				if (attributeDef.isArray()) {
					propertySchemaJSONObject.put(
						"items", jsonObject("type", "string"));
				}

				if (attributeDef.isObject()) {
					propertySchemaJSONObject.put(
						"properties", JSONFactoryUtil.createJSONObject());
				}

				if (attributeDef.isNumber()) {
					propertySchemaJSONObject.put(
						"max", () -> attributeDef.max
					).put(
						"min", () -> attributeDef.min
					);
				}

				if (attributeDef.isString()) {
					propertySchemaJSONObject.put(
						"maxLength", () -> attributeDef.max
					).put(
						"minLength", () -> attributeDef.min
					);
				}

				if (ArrayUtil.isNotEmpty(attributeDef.optionValues)) {
					JSONArray optionValuesJSONArray = jsonArray(
						attributeDef.optionValues);

					if (attributeDef.isArray()) {
						propertySchemaJSONObject.getJSONObject(
							"items"
						).put(
							"enum", optionValuesJSONArray
						);
					}
					else {
						propertySchemaJSONObject.put(
							"enum", optionValuesJSONArray);
					}
				}

				if (attributeDef.required) {
					typeSchemaJSONObject.getJSONArray(
						"required"
					).put(
						attributeDef.name
					);
				}

				typeSchemaJSONObject.getJSONObject(
					"properties"
				).put(
					attributeDef.name, propertySchemaJSONObject
				);
			}

			JSONArray oneOfJSONArray = schemaJSONObject.getJSONArray("oneOf");

			oneOfJSONArray.put(typeSchemaJSONObject);

			JSONObject propertiesJSONObject = schemaJSONObject.getJSONObject(
				"properties");

			propertiesJSONObject.getJSONObject(
				"pid"
			).getJSONArray(
				"enum"
			).put(
				objectDef.pid
			);
		}

		return schemaJSONObject.toString();
	}

	protected static List<ObjectDef> getObjectDefs(
		String[] configurationFilePaths, String rootDir) {

		List<ObjectDef> objectDefs = new ArrayList<>();

		for (String configurationFilePath : configurationFilePaths) {
			ObjectDef objectDef = constructObjectDef(
				configurationFilePath, rootDir);

			if (objectDef != null) {
				objectDefs.add(objectDef);
			}
		}

		return postProcessObjectDefs(objectDefs);
	}

	protected static List<ObjectDef> postProcessObjectDefs(
		List<ObjectDef> objectDefs) {

		List<ObjectDef> newObjectDefs = new ArrayList<>();

		for (ObjectDef objectDef : objectDefs) {
			if (!objectDef.hasMetaAnnotation) {
				continue;
			}

			if (!Validator.isBlank(objectDef.extendsInterfaceName)) {
				objectDefs.stream(
				).filter(
					objectDef::extendsObjectDef
				).findFirst(
				).ifPresent(
					superObjectDef -> {
						for (AttributeDef attributeDef :
								superObjectDef.attributeDefs) {

							if (!objectDef.attributeDefs.contains(
									attributeDef)) {

								objectDef.attributeDefs.add(attributeDef);

								Collections.sort(objectDef.attributeDefs);
							}
						}
					}
				);
			}

			newObjectDefs.add(objectDef);
		}

		Collections.sort(newObjectDefs);

		return newObjectDefs;
	}

	protected static JSONArray jsonArray(Object... items) {
		return JSONFactoryUtil.createJSONArray(items);
	}

	protected static JSONObject jsonObject(Consumer<JSONObject> consumer) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		consumer.accept(jsonObject);

		return jsonObject;
	}

	protected static JSONObject jsonObject(String key, Object value) {
		return jsonObject(jsonObject -> jsonObject.put(key, value));
	}

	protected static JSONObject jsonObject(
		String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

		return jsonObject(
			jsonObject -> jsonObject.put(key, valueUnsafeSupplier));
	}

	protected static void setFieldValue(
		Field field, Object object, Object value) {

		try {
			field.set(object, value);
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new RuntimeException(illegalAccessException);
		}
	}

	protected static boolean toBoolean(Object object) {
		if (Objects.equals(
				String.valueOf(object), String.valueOf(Boolean.TRUE))) {

			return true;
		}

		return false;
	}

	protected static Number toNumber(Object object) {
		String s = String.valueOf(object);

		if (Validator.isBlank(s)) {
			return 0;
		}

		if (s.contains(".")) {
			return GetterUtil.getFloat(s);
		}

		return GetterUtil.getInteger(s);
	}

	protected static void withMatcher(
		String s, Object object, Pattern pattern) {

		withMatcher(s, object, pattern, null);
	}

	protected static <T> void withMatcher(
		String s, T target, Pattern pattern, Consumer<T> consumer) {

		Matcher matcher = pattern.matcher(s);

		if (!matcher.find()) {
			return;
		}

		Class<?> clazz = target.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			String value;

			try {
				value = matcher.group(field.getName());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				continue;
			}

			Class<?> typeClass = field.getType();

			if (ArrayUtil.contains(
					new Class<?>[] {Object[].class, String[].class},
					typeClass)) {

				setFieldValue(
					field, target,
					value.replaceAll(
						"[ \"]", ""
					).split(
						","
					));
			}
			else if (Objects.equals(typeClass, Number.class)) {
				setFieldValue(field, target, toNumber(value));
			}
			else if (Objects.equals(typeClass, boolean.class)) {
				setFieldValue(field, target, toBoolean(value));
			}
			else if (Objects.equals(field.getName(), "type")) {
				setFieldValue(field, target, schemaDataTypes.get(value));
			}
			else {
				setFieldValue(field, target, value);
			}
		}

		if (consumer != null) {
			consumer.accept(target);
		}
	}

	protected static final Pattern attributeDefaultValuePattern =
		Pattern.compile("\\bdeflt = \"(?<defaultValue>[^\"]*)\"");
	protected static final Pattern attributeDeprecatedPattern = Pattern.compile(
		"\\b(?<deprecated>@Deprecated)");
	protected static final Pattern attributeDescriptionPattern =
		Pattern.compile("\\bdescription = \"(?<description>[^\"]*)\"");
	protected static final Pattern attributeMaxPattern = Pattern.compile(
		"\\bmax = \"(?<max>[^\"]+)\"");
	protected static final Pattern attributeMinPattern = Pattern.compile(
		"\\bmin = \"(?<min>[^\"]+)\"");
	protected static final Pattern attributeOptionLabelsPattern =
		Pattern.compile("\\boptionLabels = \\{(?<optionLabels>[^{}]*)}");
	protected static final Pattern attributeOptionValuesPattern =
		Pattern.compile("\\boptionValues = \\{(?<optionValues>[^{}]*)}");
	protected static final Pattern attributeRequiredInputPattern =
		Pattern.compile("\\brequiredInput = (?<requiredInput>true|false)");
	protected static final Pattern attributeRequiredPattern = Pattern.compile(
		"\\brequired = (?<required>true|false)");
	protected static final Pattern attributeTitlePattern = Pattern.compile(
		"\\bname = \"(?<title>[^\"]*)\"");
	protected static final Pattern attributeTypeNamePattern = Pattern.compile(
		"\\s+public(default)? (?<type>\\w+|\\S+) (?<name>\\w+)\\(\\)");
	protected static final Properties languageProperties = new Properties();
	protected static final Pattern objectDefCategoryPattern = Pattern.compile(
		"\\bcategory = \"(?<category>[^\"]*)\"");
	protected static final Pattern objectDefDescriptionPattern =
		Pattern.compile("\\bdescription = \"(?<description>[^\"]*)\"");
	protected static final Pattern objectDefScopePattern = Pattern.compile(
		"\\bscope = ExtendedObjectClassDefinition\\.Scope\\." +
			"(?<scope>SYSTEM|COMPANY|GROUP|PORTLET_INSTANCE)\\b");
	protected static final Pattern objectDefInterfaceNamePattern =
		Pattern.compile(" @?interface (?<interfaceName>[A-Z][A-Za-z\\d]+)\\b");
	protected static final Pattern objectDefExtendsInterfaceNamePattern =
		Pattern.compile(
			"\\bextends (?<extendsInterfaceName>[A-Z][A-Za-z\\d]+)\\b");
	protected static final Pattern objectDefPidPattern = Pattern.compile(
		"\\bid = \"?(?<pid>[\\w.]+)\"?");
	protected static final Pattern objectDefTitlePattern = Pattern.compile(
		"\\bname = \"(?<title>[^\"]*)\"");
	protected static final Pattern objectDefMetaAnnotationPattern =
		Pattern.compile("@Meta.OCD\\b");
	protected static final Pattern attributeDefMetaAnnotationPattern =
		Pattern.compile("@Meta.AD\\b");
	protected static final Map<String, String> schemaDataTypes =
		HashMapBuilder.put(
			"boolean", "boolean"
		).put(
			"float", "number"
		).put(
			"int", "number"
		).put(
			"LocalizedValuesMap", "object"
		).put(
			"long", "number"
		).put(
			"String", "string"
		).put(
			"String[]", "array"
		).build();

	protected static class AttributeDef implements Comparable<AttributeDef> {

		@Override
		public int compareTo(AttributeDef attributeDef) {
			return name.compareTo(attributeDef.name);
		}

		@Override
		public boolean equals(Object object) {
			if (!Objects.equals(AttributeDef.class, object.getClass())) {
				return false;
			}

			AttributeDef attributeDef = (AttributeDef)object;

			return Objects.equals(name, attributeDef.name);
		}

		public boolean isArray() {
			return Objects.equals(type, "array");
		}

		public boolean isBoolean() {
			return Objects.equals(type, "boolean");
		}

		public boolean isNumber() {
			return Objects.equals(type, "number");
		}

		public boolean isObject() {
			return Objects.equals(type, "object");
		}

		public boolean isString() {
			return Objects.equals(type, "string");
		}

		public Object defaultValue;
		public Boolean deprecated;
		public String description;
		public boolean hasMetaAnnotation;
		public Number max;
		public Number min;
		public String name;
		public String[] optionLabels;
		public Object[] optionValues;
		public boolean required = true;
		public boolean requiredInput;
		public String title;
		public String type;

	}

	protected static class ObjectDef implements Comparable<ObjectDef> {

		@Override
		public int compareTo(ObjectDef objectDef) {
			return pid.compareTo(objectDef.pid);
		}

		public boolean extendsObjectDef(ObjectDef superObjectDef) {
			if (Objects.equals(
					extendsInterfaceName, superObjectDef.interfaceName)) {

				return true;
			}

			return false;
		}

		public List<AttributeDef> attributeDefs = new ArrayList<>();
		public String category;
		public String description;
		public String extendsInterfaceName;
		public boolean hasMetaAnnotation;
		public String interfaceName;
		public String pid;
		public String scope = "system";
		public String title;

	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationEnvBuilder.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\s*public .* ([^\\s]+)\\(\\);");

}