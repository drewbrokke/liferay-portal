# Ant Source Formatter Guide

The Liferay Portal Source Formatter is a powerful tool used to ensure code consistency and adherence to Liferay's engineering standards. While many modules use Gradle, the core portal and many legacy components still rely on the Ant-based source formatter targets defined in `portal-impl/build.xml`.

---

## Reference Path

### Core Targets

All formatting targets eventually delegate to the `format-source-files` target in `portal-impl/build.xml`.

| Target | Description |
| :--- | :--- |
| `ant format-source` | **The default task.** Runs on added/modified files in the current branch compared to the working branch (usually `master`). Also validates commit messages and checks for vulnerabilities. |
| `ant format-source-all` | Runs the formatter on **all** files in the project. Use with caution as it can be time-consuming. |
| `ant format-source-current-branch` | Runs on files modified in the current branch. |
| `ant format-source-local-changes` | Runs on files with local (unstaged) changes. |
| `ant format-source-latest-author` | Runs on files modified by the latest author in the git log. |
| `ant format-source-debug` | Runs the formatter on all files with verbose debug information. |

### Specialized Targets

| Target | Purpose |
| :--- | :--- |
| `ant format-source-bnd` | Formats `.bnd`, `.gradle`, and `.json` files. |
| `ant format-source-deprecated-api` | Specifically runs the `DeprecatedAPICheck`. |
| `ant format-source-missing-override` | Specifically runs the `JavaMissingOverrideCheck`. |

### Key Properties

These properties can be passed via command line (e.g., `-Dproperty.name=value`) or set in `build.${user.name}.properties`.

| Property | Default | Description |
| :--- | :--- | :--- |
| `source.auto.fix` | `true` | If `true`, the formatter will attempt to fix violations automatically. |
| `git.working.branch.name` | `master` | The baseline branch used to calculate diffs for `format-source` and `format-source-current-branch`. |
| `source.files` | (none) | Comma-separated list of specific files to format. |
| `source.file.extensions` | (none) | Comma-separated list of extensions (e.g., `java,jsp,xml`) to limit the scan. |
| `source.check.names` | (none) | Comma-separated list of specific check names to run. |
| `skip.check.names` | (none) | Com_ma-separated list of checks to skip. |
| `max.line.length` | `80` | Maximum allowed line length. |
| `processor.thread.count` | `5` | Number of threads to use for parallel processing. |
| `source.fail.on.has.warning` | `true` | If `true`, the Ant task will fail if any violations (warnings) are found. |
| `check.vulnerabilities` | `false` | Whether to perform security vulnerability scans. |

---

## Guided Learning Path

### 1. How the Formatter Works
The `format-source-files` target performs the following steps:
1. **Branch Detection**: It identifies the current Git branch and determines the diff against the `git.working.branch.name`.
2. **File Selection**: Based on the target called (e.g., `-all` vs. default), it gathers a list of candidate files.
3. **Execution**: It invokes the `com.liferay.source.formatter.SourceFormatter` Java application.
4. **Multi-Step Processing**: It runs various "Processors" for different file types (Java, JSP, XML, Gradle, etc.).
5. **Node/Yarn Integration**: If web-related files (JS, SCSS, TS) are modified, it automatically triggers Gradle tasks like `:portalYarnFormat` to handle modern frontend formatting.

### 2. Common Usage Examples

#### Formatting your current work
Before committing, it is standard practice to run:
```bash
ant format-source
```
This is efficient as it only checks files you have changed in your current branch.

#### Forcing a check on specific files
If you want to run the formatter on just a few files:
```bash
ant format-source-all -Dsource.files=portal-impl/src/com/liferay/portal/util/StringUtil.java,portal-kernel/src/com/liferay/portal/kernel/util/Validator.java
```

#### Running a single check project-wide
To find all missing `@Override` annotations in the entire project:
```bash
ant format-source-missing-override
```

#### Disabling auto-fix for a "Dry Run"
To see violations without the formatter changing your code:
```bash
ant format-source -Dsource.auto.fix=false
```

### 3. Debugging Issues
If the formatter is behaving unexpectedly, use the debug target to see which checks are being executed and which files are being skipped:
```bash
ant format-source-debug
```
You can also check `source-formatter.properties` in the project root for the full list of configurable checks and their individual settings.

### 4. Configuration
The behavior of individual checks is governed by:
- `source-formatter.properties`: Global configuration for all checks.
- `source-formatter-suppressions.xml`: Used to skip specific checks for specific files or code blocks.
- `source-formatter.properties` (within modules): Local overrides for specific modules.