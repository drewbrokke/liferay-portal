/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Leo
 */
public class SCSSMapCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith(".scss")) {
			return content;
		}

		return _formatMaps(content);
	}

	private String _addBlankLinesBeforeNestedMaps(String mapBody) {
		String[] lines = mapBody.split("\n", -1);

		StringBundler sb = new StringBundler(lines.length * 2);

		int depth = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			String trimmedLine = line.trim();

			int lineDepth = depth;

			for (int j = 0; j < trimmedLine.length(); j++) {
				char c = trimmedLine.charAt(j);

				if (c == CharPool.OPEN_PARENTHESIS) {
					depth++;
				}
				else if (c == CharPool.CLOSE_PARENTHESIS) {
					depth--;
				}
			}

			if ((i > 0) && (lineDepth == 0) &&
				_containsNestedMap(trimmedLine)) {

				String previousLine = lines[i - 1].trim();

				if (!previousLine.isEmpty() &&
					!previousLine.equals("(")) {

					sb.append("\n");
				}
			}

			sb.append(line);

			if (i < (lines.length - 1)) {
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	private boolean _containsNestedMap(String line) {
		if (line.isEmpty()) {
			return false;
		}

		int colonIndex = line.indexOf(CharPool.COLON);

		if (colonIndex == -1) {
			return false;
		}

		String value = line.substring(colonIndex + 1).trim();

		return value.startsWith("(");
	}

	private int _findMatchingParen(String content, int openIndex) {
		int depth = 1;

		for (int i = openIndex + 1; i < content.length(); i++) {
			char c = content.charAt(i);

			if (c == CharPool.OPEN_PARENTHESIS) {
				depth++;
			}
			else if (c == CharPool.CLOSE_PARENTHESIS) {
				depth--;

				if (depth == 0) {
					return i;
				}
			}
		}

		return -1;
	}

	private String _formatMapBody(String mapBody) {
		mapBody = _addBlankLinesBeforeNestedMaps(mapBody);

		mapBody = _sortMapKeyBlocks(mapBody);

		return mapBody;
	}

	private String _formatMaps(String content) {
		Matcher matcher = _mapPattern.matcher(content);

		while (matcher.find()) {
			int openParenIndex = content.indexOf(
				CharPool.OPEN_PARENTHESIS, matcher.start());

			if (openParenIndex == -1) {
				continue;
			}

			int closeParenIndex = _findMatchingParen(content, openParenIndex);

			if (closeParenIndex == -1) {
				continue;
			}

			String mapBody = content.substring(
				openParenIndex + 1, closeParenIndex);

			String newMapBody = _formatMapBody(mapBody);

			if (!mapBody.equals(newMapBody)) {
				content =
					content.substring(0, openParenIndex + 1) + newMapBody +
					content.substring(closeParenIndex);

				matcher = _mapPattern.matcher(content);
			}
		}

		return content;
	}

	private boolean _isSimpleEntry(String line) {
		String trimmedLine = line.trim();

		if (trimmedLine.isEmpty()) {
			return false;
		}

		int colonIndex = trimmedLine.indexOf(CharPool.COLON);

		if (colonIndex == -1) {
			return false;
		}

		String value = trimmedLine.substring(colonIndex + 1).trim();

		return !value.startsWith("(");
	}

	private String _sortMapKeyBlocks(String mapBody) {
		String[] lines = mapBody.split("\n", -1);

		List<String> result = new ArrayList<>();

		List<String> block = new ArrayList<>();

		int depth = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			String trimmedLine = line.trim();

			int lineDepth = depth;

			for (int j = 0; j < trimmedLine.length(); j++) {
				char c = trimmedLine.charAt(j);

				if (c == CharPool.OPEN_PARENTHESIS) {
					depth++;
				}
				else if (c == CharPool.CLOSE_PARENTHESIS) {
					depth--;
				}
			}

			if ((lineDepth == 0) && _isSimpleEntry(line)) {
				block.add(line);
			}
			else {
				if (block.size() > 1) {
					Collections.sort(block, new MapKeyComparator());
				}

				result.addAll(block);

				block = new ArrayList<>();

				result.add(line);
			}
		}

		if (block.size() > 1) {
			Collections.sort(block, new MapKeyComparator());
		}

		result.addAll(block);

		StringBundler sb = new StringBundler(result.size() * 2);

		for (int i = 0; i < result.size(); i++) {
			sb.append(result.get(i));

			if (i < (result.size() - 1)) {
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	private static final Pattern _mapPattern = Pattern.compile(
		"^(\\$[\\w-]+\\s*:|map-deep-merge\\s*\\(\\s*)\\s*\\(",
		Pattern.MULTILINE);

	private class MapKeyComparator extends NaturalOrderStringComparator {

		@Override
		public int compare(String s1, String s2) {
			String key1 = s1.trim();
			String key2 = s2.trim();

			int pos1 = key1.indexOf(CharPool.COLON);
			int pos2 = key2.indexOf(CharPool.COLON);

			if ((pos1 == -1) || (pos2 == -1)) {
				return super.compare(key1, key2);
			}

			return super.compare(
				key1.substring(0, pos1), key2.substring(0, pos2));
		}

	}

}
