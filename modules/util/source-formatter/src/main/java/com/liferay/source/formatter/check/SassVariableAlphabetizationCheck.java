package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks for unalphabetized Sass variable declarations within SCSS files.
 * It considers blocks of variables separated by blank lines.
 */
public class SassVariableAlphabetizationCheck extends BaseUpgradeCheck {

    // Pattern to match a Sass variable declaration.
    // Group 1: entire line including comments
    // Group 2: variable name (e.g., "$link-color")
    private static final Pattern _SASS_VARIABLE_PATTERN = Pattern.compile("^(\s*\$[^:]+?)(:.*)?$", Pattern.MULTILINE);

    @Override
    protected String format(
            String fileName, String absolutePath, String content)
        throws Exception {

        StringBuilder formattedContent = new StringBuilder();
        String[] lines = content.split("?
");
        List<VariableEntry> currentVariableBlock = new ArrayList<>();
        int currentLineNumber = 0;

        for (String line : lines) {
            currentLineNumber++;
            Matcher matcher = _SASS_VARIABLE_PATTERN.matcher(line);

            if (matcher.find()) {
                // This line is a Sass variable declaration
                currentVariableBlock.add(new VariableEntry(line, matcher.group(1).trim(), currentLineNumber));
            } else {
                // Not a variable declaration. Process the accumulated variable block if any.
                // A blank line or non-variable line signals the end of a block.
                if (!currentVariableBlock.isEmpty()) {
                    formattedContent.append(processVariableBlock(fileName, currentVariableBlock));
                    currentVariableBlock.clear();
                }
                // Add the current non-variable line to the output
                formattedContent.append(line).append(StringPool.NEW_LINE);
            }
        }

        // Process any remaining variable block at the end of the file
        if (!currentVariableBlock.isEmpty()) {
            formattedContent.append(processVariableBlock(fileName, currentVariableBlock));
        }

        return formattedContent.toString();
    }

    @Override
    protected String[] getValidExtensions() {
        return new String[] {"scss"};
    }

    private String processVariableBlock(String fileName, List<VariableEntry> variableBlock) {
        List<VariableEntry> sortedBlock = new ArrayList<>(variableBlock);
        Collections.sort(sortedBlock, Comparator.comparing(entry -> entry.variableName));

        boolean needsFixing = false;
        for (int i = 0; i < variableBlock.size(); i++) {
            if (!variableBlock.get(i).variableName.equals(sortedBlock.get(i).variableName)) {
                needsFixing = true;
                break;
            }
        }

        if (needsFixing) {
            StringBundler message = new StringBundler();
            message.append("Sass variables are not alphabetized in block starting at line ");
            message.append(variableBlock.get(0).lineNumber);
            message.append(".
Expected order:
");
            for (VariableEntry entry : sortedBlock) {
                message.append(entry.lineContent).append(StringPool.NEW_LINE);
            }
            addMessage(fileName, message.toString());

            // Reconstruct the block with sorted variables
            StringBundler fixedBlock = new StringBundler();
            for (VariableEntry entry : sortedBlock) {
                fixedBlock.append(entry.lineContent).append(StringPool.NEW_LINE);
            }
            return fixedBlock.toString();
        }

        // If no fixing needed, return original block content
        StringBundler originalBlock = new StringBundler();
        for (VariableEntry entry : variableBlock) {
            originalBlock.append(entry.lineContent).append(StringPool.NEW_LINE);
        }
        return originalBlock.toString();
    }

    private static class VariableEntry {
        String lineContent;
        String variableName;
        int lineNumber;

        VariableEntry(String lineContent, String variableName, int lineNumber) {
            this.lineContent = lineContent;
            this.variableName = variableName;
            this.lineNumber = lineNumber;
        }
    }
}
