package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks for Sass map keys whose values are also Sass maps and ensures they are
 * separated by new lines for readability.
 */
public class SassMapNewlineCheck extends BaseUpgradeCheck {

    // Pattern to find Sass map declarations: $variable: (...);
    // Group 1: The entire map content including the variable name and the outer parentheses
    private static final Pattern _SASS_MAP_DECLARATION_PATTERN = Pattern.compile(
        "(\$[a-zA-Z0-9_-]+\s*:\s*\([\s\S]*?\));", Pattern.MULTILINE);

    // Pattern to find keys within a map that have a nested map as their value: key: (
    private static final Pattern _NESTED_MAP_KEY_START_PATTERN = Pattern.compile(
        "^(\s*[a-zA-Z0-9_-]+\s*:\s*\()", Pattern.MULTILINE);


    @Override
    protected String format(
            String fileName, String absolutePath, String content)
        throws Exception {

        Matcher mapMatcher = _SASS_MAP_DECLARATION_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();

        while (mapMatcher.find()) {
            String fullMapDeclaration = mapMatcher.group(1);
            int contentStart = fullMapDeclaration.indexOf('(');
            int contentEnd = fullMapDeclaration.lastIndexOf(')');

            if (contentStart == -1 || contentEnd == -1 || contentEnd <= contentStart) {
                // Malformed map, skip or report
                mapMatcher.appendReplacement(result, Matcher.quoteReplacement(fullMapDeclaration));
                continue;
            }

            String mapInnerContent = fullMapDeclaration.substring(contentStart + 1, contentEnd);
            String formattedMapInnerContent = formatMapContent(
                mapInnerContent, fileName, mapMatcher.start() + contentStart + 2); // Calculate true line number

            // Reconstruct the full map declaration with formatted content
            String fixedMapDeclaration = fullMapDeclaration.substring(0, contentStart + 1) +
                                         formattedMapInnerContent +
                                         fullMapDeclaration.substring(contentEnd);

            mapMatcher.appendReplacement(result, Matcher.quoteReplacement(fixedMapDeclaration));
        }
        mapMatcher.appendTail(result);

        return result.toString();
    }

    @Override
    protected String[] getValidExtensions() {
        return new String[] {"scss"};
    }

    private String formatMapContent(String mapContent, String fileName, int startLineNumber) {
        String[] lines = mapContent.split("?
");
        StringBundler formattedContent = new StringBundler();
        String previousLineTrimmed = "";

        for (int i = 0; i < lines.length; i++) {
            String currentLine = lines[i];
            String currentLineTrimmed = currentLine.trim();

            if (currentLineTrimmed.isEmpty()) {
                // Preserve existing blank lines
                formattedContent.append(currentLine).append(StringPool.NEW_LINE);
                previousLineTrimmed = currentLineTrimmed;
                continue;
            }

            Matcher nestedMapKeyMatcher = _NESTED_MAP_KEY_START_PATTERN.matcher(currentLine);

            // Check for nested map key (e.g., "  hover: (")
            if (nestedMapKeyMatcher.find()) {
                // If it's not the very first non-empty line in the map,
                // and the previous significant line was not a blank line,
                // and the previous significant line did not end with a comma (indicating continuation of current entry)
                if (i > 0 && !previousLineTrimmed.isEmpty() && !previousLineTrimmed.endsWith(",")) {
                    // Add a blank line for separation
                    formattedContent.append(StringPool.NEW_LINE);
                    addMessage(fileName, "Missing newline before nested Sass map key starting at line " + (startLineNumber + i) + ": " + currentLine);
                }
            }
            formattedContent.append(currentLine).append(StringPool.NEW_LINE);
            previousLineTrimmed = currentLineTrimmed;
        }
        return formattedContent.toString();
    }
}
