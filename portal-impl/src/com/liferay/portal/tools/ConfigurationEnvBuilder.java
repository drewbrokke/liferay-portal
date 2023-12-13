/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	private static ObjectDef _constructObjectDef(
			String configurationFilePath, String rootDir,
			Properties languageProperties)
		throws IOException {

		ObjectDef objectDef = new ObjectDef();

		Stream<String> stream = Files.lines(
			Paths.get(rootDir, configurationFilePath));

		AtomicReference<AttributeDef> attributeDefAtomicReference =
			new AtomicReference<>(new AttributeDef());

		stream.forEach(
			line -> {
				AttributeDef attributeDef = attributeDefAtomicReference.get();

				if (objectDef.interfaceName == null) {
					_withMatcher(
						line, objectDef,
						Pattern.compile("\\bid = \"(?<pid>com\\..+)\""), null);
					_withMatcher(
						line, objectDef,
						Pattern.compile(
							"\\bcategory = \"(?<category>[^\"]*)\""),
						null);
					_withMatcher(
						line, objectDef,
						Pattern.compile(
							"\\bdescription = \"(?<description>[^\"]*)\""),
						() ->
							objectDef.description =
								languageProperties.getProperty(
									objectDef.description));
					_withMatcher(
						line, objectDef,
						Pattern.compile("\\bname = \"(?<title>[^\"]*)\""),
						() ->
							objectDef.title = languageProperties.getProperty(
								objectDef.title));
					_withMatcher(
						line, objectDef,
						Pattern.compile(
							"public @?interface (?<interfaceName>[A-Z][A-Za-z\\d]+)\\b"),
						null);

					return;
				}

				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\bdeflt = \"(?<defaultValue>[^\"]*)\""),
					null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile(
						"\\bdescription = \"(?<description>[^\"]*)\""),
					() ->
						attributeDef.description =
							languageProperties.getProperty(
								attributeDef.description));
				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\bmax = \"(?<max>[^\"]+)\""), null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\bmin = \"(?<min>[^\"]+)\""), null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\bname = \"(?<title>[^\"]*)\""),
					() ->
						attributeDef.title = languageProperties.getProperty(
							attributeDef.title));
				_withMatcher(
					line, attributeDef,
					Pattern.compile(
						"\\boptionLabels = \\{(?<optionLabels>[^{}]*)}"),
					null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile(
						"\\boptionValues = \\{(?<optionValues>[^{}]*)}"),
					null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile(
						"\\brequiredInput = (?<requiredInput>true|false)"),
					null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\brequired = (?<required>true|false)"),
					null);
				_withMatcher(
					line, attributeDef,
					Pattern.compile("\\b(?<deprecated>@Deprecated)"),
					() -> attributeDef.deprecated = true);
				_withMatcher(
					line, attributeDef,
					Pattern.compile(
						"\\s+public(default)? (?<type>\\w+|\\S+) (?<name>\\w+)\\(\\)"),
					() -> {
						if (attributeDef.requiredInput) {
							attributeDef.required = true;
						}

						if (attributeDef.defaultValue != null) {
							if (attributeDef.isBoolean()) {
								attributeDef.defaultValue =
									attributeDef.defaultValue == "true";
							}

							if (attributeDef.isNumber()) {
								attributeDef.defaultValue = _stringToNumber(
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
									_stringToNumber(
										String.valueOf(optionValue)));
							}

							attributeDef.optionValues = optionValues;
						}

						objectDef.attributeDefs.add(attributeDef);

						attributeDefAtomicReference.set(new AttributeDef());
					});
			});

		if (objectDef.pid != null) {
			return objectDef;
		}

		return null;
	}

	private static String _generateJSONString(
			String[] configurationFilePaths, String rootDir)
		throws Exception {

		Properties languageProperties = new Properties();

		Path languagePropertiesPath = Paths.get(
			rootDir,
			"modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties");

		languageProperties.load(
			new FileReader(languagePropertiesPath.toFile()));

		List<ObjectDef> objectDefs = new ArrayList<>();

		for (String configurationFilePath : configurationFilePaths) {
			ObjectDef objectDef = _constructObjectDef(
				configurationFilePath, rootDir, languageProperties);

			if (objectDef != null) {
				objectDefs.add(objectDef);
			}
		}

		JSONObject schema = jsonObject(
			jsonObject -> jsonObject.put(
				"oneOf", jsonArray()
			).put(
				"properties",
				jsonObject(
					propertiesJSONObject -> propertiesJSONObject.put(
						"pid",
						jsonObject(
							pidJSONObject -> pidJSONObject.put(
								"enum", jsonArray()))))
			));

		for (ObjectDef objectDef : objectDefs) {
			JSONObject typeSchema = jsonObject(
				jsonObject -> jsonObject.put(
					"description", () -> objectDef.description
				).put(
					"properties",
					jsonObject(
						propertiesJSONObject -> propertiesJSONObject.put(
							"pid",
							jsonObject(
								pid -> pid.put(
									"const", objectDef.pid
								).put(
									"description", () -> objectDef.description
								).put(
									"title", () -> objectDef.title
								))))
				).put(
					"required", jsonArray("pid")
				).put(
					"title", () -> objectDef.title
				));

			for (AttributeDef attributeDef : objectDef.attributeDefs) {
				JSONObject propertySchema = jsonObject(
					jsonObject -> jsonObject.put(
						"default", () -> attributeDef.defaultValue
					).put(
						"deprecated", () -> attributeDef.deprecated
					).put(
						"description", () -> attributeDef.description
					).put(
						"title", () -> attributeDef.title
					).put(
						"type", () -> attributeDef.type
					));

				if (attributeDef.isArray()) {
					propertySchema.put(
						"items",
						JSONFactoryUtil.createJSONObject(
							Collections.singletonMap("type", "string")));
				}

				if (attributeDef.isObject()) {
					propertySchema.put(
						"properties", JSONFactoryUtil.createJSONObject());
				}

				if (attributeDef.isNumber()) {
					propertySchema.put(
						"max", () -> attributeDef.max
					).put(
						"min", () -> attributeDef.min
					);
				}

				if (attributeDef.isString()) {
					propertySchema.put(
						"maxLength", () -> attributeDef.max
					).put(
						"minLength", () -> attributeDef.min
					);
				}

				if (ArrayUtil.isNotEmpty(attributeDef.optionValues)) {
					JSONArray optionValuesJSONArray = jsonArray(
						attributeDef.optionValues);

					if (attributeDef.isArray()) {
						propertySchema.getJSONObject(
							"items"
						).put(
							"enum", optionValuesJSONArray
						);
					}
					else {
						propertySchema.put("enum", optionValuesJSONArray);
					}
				}

				if (attributeDef.required) {
					typeSchema.getJSONArray(
						"required"
					).put(
						attributeDef.name
					);
				}

				typeSchema.getJSONObject(
					"properties"
				).put(
					attributeDef.name, propertySchema
				);
			}

			schema.getJSONArray(
				"oneOf"
			).put(
				typeSchema
			);
			schema.getJSONObject(
				"properties"
			).getJSONObject(
				"pid"
			).getJSONArray(
				"enum"
			).put(
				objectDef.pid
			);
		}

		return schema.toString();
	}

	private static void _setFieldValue(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static Number _stringToNumber(String s) {
		if (Validator.isBlank(s)) {
			return 0;
		}

		if (s.contains(".")) {
			return GetterUtil.getFloat(s);
		}

		return GetterUtil.getInteger(s);
	}

	private static void _withMatcher(
		String s, Object o, Pattern pattern, Runnable runnable) {

		Matcher matcher = pattern.matcher(s);

		if (!matcher.find()) {
			return;
		}

		Class<?> clazz = o.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			String value;

			try {
				value = matcher.group(field.getName());
			}
			catch (Exception exception) {
				continue;
			}

			Class<?> typeClass = field.getType();

			if (ArrayUtil.contains(
					new Class<?>[] {Object[].class, String[].class},
					typeClass)) {

				_setFieldValue(
					field, o,
					value.replaceAll(
						"[ \"]", ""
					).split(
						","
					));
			}
			else if (Objects.equals(typeClass, Number.class)) {
				_setFieldValue(field, o, _stringToNumber(value));
			}
			else if (Objects.equals(typeClass, boolean.class)) {
				_setFieldValue(field, o, value.equals("true"));
			}
			else if (Objects.equals(field.getName(), "type")) {
				_setFieldValue(field, o, schemaDataTypes.get(value));
			}
			else {
				_setFieldValue(field, o, value);
			}
		}

		if (runnable != null) {
			runnable.run();
		}
	}

	protected static JSONArray jsonArray(Object... items) {
		return JSONFactoryUtil.createJSONArray(items);
	}

	protected static JSONObject jsonObject(Consumer<JSONObject> consumer) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		consumer.accept(jsonObject);

		return jsonObject;
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\s*public .* ([^\\s]+)\\(\\);");

	protected static class ObjectDef {

		public List<AttributeDef> attributeDefs = new ArrayList<>();
		public String pid;
		public String category;
		public String description;
		public String title;
		public String interfaceName;

	}

	private static final Map<String, String> schemaDataTypes =
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

	protected static class AttributeDef {

		public Object defaultValue;
		public String description;
		public Number max;
		public Number min;
		public String title;
		public String[] optionLabels;
		public Object[] optionValues;
		public boolean requiredInput;
		public boolean required = true;
		public Boolean deprecated;
		public String type;
		public String name;

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

	}

}