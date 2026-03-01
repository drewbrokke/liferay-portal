/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Leo
 */
public class SCSSVariablesOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith(".scss")) {
			return content;
		}

		return _sortVariables(content);
	}

	private String _sortVariables(String content) {
		Matcher matcher = _variablesPattern.matcher(content);

		while (matcher.find()) {
			String variables = StringUtil.trimTrailing(matcher.group());

			String noTabVariables = StringUtil.removeChar(
				variables, CharPool.TAB);

			List<String> variableList = ListUtil.fromArray(
				StringUtil.splitLines(noTabVariables));

			if (variableList.size() < 2) {
				continue;
			}

			Collections.sort(variableList, new VariableComparator());

			String tabs = matcher.group(2);

			StringBundler sb = new StringBundler(variableList.size() * 3);

			for (String variable : variableList) {
				sb.append(tabs);
				sb.append(variable);
				sb.append("\n");
			}

			String newVariables = sb.toString();

			newVariables = newVariables.substring(
				0, newVariables.length() - 1);

			content = StringUtil.replaceFirst(
				content, variables, newVariables, matcher.start() - 1);
		}

		return content;
	}

	private static final Pattern _variablesPattern = Pattern.compile(
		"(^(\\t*)\\$[\\w-]+\\s*:.*;\n)+", Pattern.MULTILINE);

	private class VariableComparator extends NaturalOrderStringComparator {

		@Override
		public int compare(String s1, String s2) {
			int pos1 = s1.indexOf(CharPool.COLON);
			int pos2 = s2.indexOf(CharPool.COLON);

			return super.compare(
				s1.substring(0, pos1), s2.substring(0, pos2));
		}

	}

}
