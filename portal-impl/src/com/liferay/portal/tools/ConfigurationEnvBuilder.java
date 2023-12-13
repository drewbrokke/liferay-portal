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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Matthew Tambara
 */
public class ConfigurationEnvBuilder {

	public static String buildContent(
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

	public static void main(String[] args) throws IOException {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String[] configurationJavaFileNames = StringUtil.split(
			arguments.get("configuration.java.files"), '\n');

		Path rootPath = Paths.get(arguments.getOrDefault("root.dir", "."));

		Path path = Paths.get(arguments.get("output.file"));

		String content = new String(Files.readAllBytes(path));

		int index = content.indexOf("##\n## OSGi Configuration Overrides");

		content = content.substring(0, index);

		Path realPath = rootPath.toRealPath();

		content = content.concat(
			buildContent(configurationJavaFileNames, realPath));

		new JSONFactoryUtil(
		).setJSONFactory(
			new JSONFactoryImpl()
		);

		try {
			String jsonString = _generateJSONString(
				configurationJavaFileNames, realPath.toString());

			Files.write(Paths.get(".", "schema.json"), jsonString.getBytes());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		Files.write(path, content.getBytes());
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
				setFieldValue(field, target, _schemaDataTypes.get(value));
			}
			else {
				setFieldValue(field, target, value);
			}
		}

		if (consumer != null) {
			consumer.accept(target);
		}
	}

	protected static class AttributeDef {

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

	protected static class ObjectDef {

		public List<AttributeDef> attributeDefs = new ArrayList<>();
		public String category;
		public String description;
		public String interfaceName;
		public String pid;
		public String title;

	}

	private static Optional<ObjectDef> _constructObjectDef(
			String configurationFilePath, String rootDir,
			Properties languageProperties) throws Exception {

		ObjectDef objectDef = new ObjectDef();

		AttributeDef attributeDef = new AttributeDef();

		for (String line : Files.readAllLines(
			Paths.get(rootDir, configurationFilePath))) {

			if (objectDef.interfaceName == null) {
				withMatcher(
					line, objectDef,
					Pattern.compile("\\bid = \"(?<pid>com\\..+)\""));
				withMatcher(
					line, objectDef,
					Pattern.compile(
						"\\bcategory = \"(?<category>[^\"]*)\""));
				withMatcher(
					line, objectDef,
					Pattern.compile(
						"\\bdescription = \"(?<description>[^\"]*)\""),
					(ObjectDef objectDef1) ->
						objectDef1.description =
							languageProperties.getProperty(
								objectDef1.description));
				withMatcher(
					line, objectDef,
					Pattern.compile("\\bname = \"(?<title>[^\"]*)\""),
					(ObjectDef objectDef1) ->
						objectDef1.title = languageProperties.getProperty(
							objectDef1.title));
				withMatcher(
					line, objectDef,
					Pattern.compile(
						"public @?interface (?<interfaceName>[A-Z][A-Za-z\\d]+)\\b"));

				continue;
			}

			if (objectDef.pid == null) {
				return Optional.empty();
			}

			withMatcher(
				line, attributeDef,
				Pattern.compile("\\bdeflt = \"(?<defaultValue>[^\"]*)\""));
			withMatcher(
				line, attributeDef,
				Pattern.compile(
					"\\bdescription = \"(?<description>[^\"]*)\""),
				(AttributeDef attributeDef1) ->
					attributeDef1.description =
						languageProperties.getProperty(
							attributeDef1.description));
			withMatcher(
				line, attributeDef,
				Pattern.compile("\\bmax = \"(?<max>[^\"]+)\""));
			withMatcher(
				line, attributeDef,
				Pattern.compile("\\bmin = \"(?<min>[^\"]+)\""));
			withMatcher(
				line, attributeDef,
				Pattern.compile("\\bname = \"(?<title>[^\"]*)\""),
				(AttributeDef attributeDef1) ->
					attributeDef1.title = languageProperties.getProperty(
						attributeDef1.title));
			withMatcher(
				line, attributeDef,
				Pattern.compile(
					"\\boptionLabels = \\{(?<optionLabels>[^{}]*)}"));
			withMatcher(
				line, attributeDef,
				Pattern.compile(
					"\\boptionValues = \\{(?<optionValues>[^{}]*)}"));
			withMatcher(
				line, attributeDef,
				Pattern.compile(
					"\\brequiredInput = (?<requiredInput>true|false)"));
			withMatcher(
				line, attributeDef,
				Pattern.compile("\\brequired = (?<required>true|false)"));
			withMatcher(
				line, attributeDef,
				Pattern.compile("\\b(?<deprecated>@Deprecated)"),
				(AttributeDef attributeDef1) -> attributeDef1.deprecated = true);
			withMatcher(
				line, attributeDef,
				Pattern.compile(
					"\\s+public(default)? (?<type>\\w+|\\S+) (?<name>\\w+)\\(\\)"));

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
					String.valueOf(attributeDef.defaultValue),
					"${")) {

					attributeDef.defaultValue = null;
				}
			}

			if (ArrayUtil.isNotEmpty(attributeDef.optionValues) &&
				attributeDef.isNumber()) {

				Number[] optionValues = {};

				for (Object optionValue :
					attributeDef.optionValues) {

					optionValues = ArrayUtil.append(
						optionValues,
						toNumber(String.valueOf(optionValue)));
				}

				attributeDef.optionValues = optionValues;
			}

			if (ArrayUtil.isNotEmpty(attributeDef.optionValues) &&
				attributeDef.isNumber()) {

				Number[] optionValues = {};

				for (Object optionValue :
					attributeDef.optionValues) {

					optionValues = ArrayUtil.append(
						optionValues,
						toNumber(String.valueOf(optionValue)));
				}

				attributeDef.optionValues = optionValues;
			}

			objectDef.attributeDefs.add(attributeDef);

			attributeDef = new AttributeDef();
		}

		return Optional.of(objectDef);
	}

	private static String _generateJSONString(
			String[] configurationFilePaths, String rootDir)
		throws Exception {

		Properties languageProperties = new Properties();

		Path languagePropertiesPath = Paths.get(
			rootDir,
			"modules/apps/portal-language/portal-language-lang/src/main/" +
				"resources/content/Language.properties");

		languageProperties.load(
			new FileReader(languagePropertiesPath.toFile()));

		List<ObjectDef> objectDefs = new ArrayList<>();

		for (String configurationFilePath : configurationFilePaths) {
			Optional<ObjectDef> objectDefOptional = _constructObjectDef(
				configurationFilePath, rootDir, languageProperties);

			objectDefOptional.ifPresent(objectDefs::add);
		}

		JSONObject schemaJSONObject = jsonObject(
			jsonObject -> jsonObject.put(
				"oneOf", jsonArray()
			).put(
				"properties", jsonObject("pid", jsonObject("enum", jsonArray()))
			));

		for (ObjectDef objectDef : objectDefs) {
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

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationEnvBuilder.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\s*public .* ([^\\s]+)\\(\\);");
	private static final Map<String, String> _schemaDataTypes =
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

}