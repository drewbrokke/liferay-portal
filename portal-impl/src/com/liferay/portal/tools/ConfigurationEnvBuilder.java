/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.BufferedInputStream;
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
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
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

		try {
			_generateJSONString(configurationJavaFileNames, realPath.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		Files.write(path, content.getBytes());
	}

	private static Number _stringToNumber(String s) {
		if (Validator.isBlank(s)) {
			return 0;
		}

		if (s.contains(".")) {
			return Float.parseFloat(s);
		}

		return Integer.parseInt(s);
	}

	private static String _generateJSONString(String[] configurationFilePaths, String rootDir) throws Exception {
		String jsonString  = "";

		Properties languageProperties = new Properties();

		Path languagePropertiesPath = Paths.get(
			rootDir,
			"modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties");

		languageProperties.load(new FileReader(languagePropertiesPath.toFile()));

		List<ObjectDef> objectDefs = new ArrayList<>();

		for (String configurationFilePath : configurationFilePaths) {
			ObjectDef objectDef = _constructObjectDef(configurationFilePath, rootDir,
				languageProperties);

			if (objectDef != null) {
				objectDefs.add(objectDef);
			}
		}

		return jsonString;
	}

	private static ObjectDef _constructObjectDef(
		String configurationFilePath, String rootDir,
		Properties languageProperties)
		throws IOException {

		System.out.printf("%s\n\n", configurationFilePath);

		ObjectDef objectDef = new ObjectDef();

		Stream<String> stream =
			Files.lines(Paths.get(rootDir, configurationFilePath));

		AtomicReference<AttributeDef> attributeDefAtomicReference = new AtomicReference<>(new AttributeDef());

		stream.forEach(line -> {
			AttributeDef attributeDef = attributeDefAtomicReference.get();

			if (objectDef.interfaceName == null) {
				_withMatcher(line, objectDef, Pattern.compile("\\bid = \"(?<pid>com\\..+)\""), null);
				_withMatcher(line, objectDef, Pattern.compile("\\bcategory = \"(?<category>[^\"]*)\""), null);
				_withMatcher(line, objectDef, Pattern.compile("\\bdescription = \"(?<description>[^\"]*)\""), () -> objectDef.description = languageProperties.getProperty(objectDef.description));
				_withMatcher(line, objectDef, Pattern.compile("\\bname = \"(?<title>[^\"]*)\""), () -> objectDef.title = languageProperties.getProperty(objectDef.title));
				_withMatcher(line, objectDef, Pattern.compile("public @?interface (?<interfaceName>[A-Z][A-Za-z\\d]+)\\b"), null);

				return;
			}

			_withMatcher(line, attributeDef, Pattern.compile("\\bdeflt = \"(?<defaultValue>[^\"]*)\""), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\bdescription = \"(?<description>[^\"]*)\""), () -> attributeDef.description = languageProperties.getProperty(attributeDef.description));
			_withMatcher(line, attributeDef, Pattern.compile("\\bmax = \"(?<max>[^\"]+)\""), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\bmin = \"(?<min>[^\"]+)\""), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\bname = \"(?<title>[^\"]*)\""), () -> attributeDef.title = languageProperties.getProperty(attributeDef.title));
			_withMatcher(line, attributeDef, Pattern.compile("\\boptionLabels = \\{(?<optionLabels>[^{}]*)}"), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\boptionValues = \\{(?<optionValues>[^{}]*)}"), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\brequiredInput = (?<requiredInput>true|false)"), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\brequired = (?<required>true|false)"), null);
			_withMatcher(line, attributeDef, Pattern.compile("\\b(?<deprecated>@Deprecated)"), () -> attributeDef.deprecated = true);
			_withMatcher(line, attributeDef, Pattern.compile("\\s+public(default)? (?<type>\\w+|\\S+) (?<name>\\w+)\\(\\)"), () -> {
				if (attributeDef.requiredInput) {
					attributeDef.required = true;
				}

				if (attributeDef.defaultValue != null) {
					if (attributeDef.isBoolean()) {
						attributeDef.defaultValue = attributeDef.defaultValue == "true";
					}
					if (attributeDef.isNumber()) {
						attributeDef.defaultValue = _stringToNumber(String.valueOf(attributeDef.defaultValue));
					}
				}

				if (ArrayUtil.isNotEmpty(attributeDef.optionValues) && attributeDef.isNumber()) {
					Object[] optionValues = {};

					for (Object optionValue : attributeDef.optionValues) {
						ArrayUtil.append(optionValues, _stringToNumber(String.valueOf(optionValue)));
					}

					attributeDef.optionValues = optionValues;
				}

				if (ArrayUtil.isNotEmpty(attributeDef.optionValues) && attributeDef.isNumber()) {
					Object[] optionValues = {};

					for (Object optionValue : attributeDef.optionValues) {
						ArrayUtil.append(optionValues, _stringToNumber(String.valueOf(optionValue)));
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

	private static void _withMatcher(
		String s, Object o, Pattern pattern, Runnable runnable) {
		Matcher matcher = pattern.matcher(s);

		if (!matcher.find()) {
			return;
		}

		Class<?> clazz = o.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			Class<?> typeClass = field.getType();

			String value;

			try {
				value = matcher.group(field.getName());
			}
			catch (Exception ignored) {
				continue;
			}

			if (ArrayUtil.contains(new Class<?>[]{Object[].class, String[].class}, typeClass)) {
				_setFieldValue(field, o, value.replaceAll("[ \"]", "").split(","));
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

	private static void _setFieldValue(Field f, Object o, Object value) {
		try {
			f.set(o, value);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static final Map<String, String> schemaDataTypes = HashMapBuilder.put("LocalizedValuesMap", "object")
		.put("String", "string")
		.put("String[]", "array")
		.put("boolean", "boolean")
		.put("float", "number")
		.put("int", "number")
		.put("long", "number").build();

	static class ObjectDef {
		List<AttributeDef> attributeDefs = new ArrayList<>();
		String pid;
		String category;
		String description;
		String title;
		String interfaceName;
	}

	private static class AttributeDef {
		Object defaultValue;
		String description;
		Number max;
		Number min;
		String title;
		String[] optionLabels;
		Object[] optionValues;
		boolean requiredInput;
		boolean required = true;
		boolean deprecated;
		String type;
		String name;

		boolean isArray() {
			return Objects.equals(type, "array");
		}
		boolean isBoolean() {
			return Objects.equals(type, "boolean");
		}
		boolean isNumber() {
			return Objects.equals(type, "number");
		}
		boolean isObject() {
			return Objects.equals(type, "object");
		}
		boolean isString() {
			return Objects.equals(type, "string");
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\s*public .* ([^\\s]+)\\(\\);");

}