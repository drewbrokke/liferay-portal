---

derived-from:
  author: "Brian Chan"
  paths:
    - ".claude/**/*.sh"
    - "cloud/**/*.sh"
  since: c33aead2df052
paths:
  - ".claude/**/*.sh"
  - "cloud/**/*.sh"

---

# Shell Script Style

When creating or editing any shell script under `.claude` or `cloud`, follow these conventions.

## Continuation Lines

- Indent continuation arguments with one tab deeper than the command itself.
- Inside a function body (already one tab in), a multi-line command sits at one tab and its continuation arguments at two tabs.
- Inside an inline JSON value, indent each nested level by one additional tab so the indentation tracks the JSON nesting depth.

Before:

```bash
function _configure_s3_bucket {
	aws s3api put-bucket-encryption \
	--bucket "${bucket_name}" \
	--region "${region}" \
	--server-side-encryption-configuration "{
			\"Rules\": [{
					\"ApplyServerSideEncryptionByDefault\": {
							\"KMSMasterKeyID\": \"${kms_key_id}\"
					}
			}]
		}"
}
```

After:

```bash
function _configure_s3_bucket {
	aws s3api put-bucket-encryption \
		--bucket "${bucket_name}" \
		--region "${region}" \
		--server-side-encryption-configuration "{
			\"Rules\": [{
				\"ApplyServerSideEncryptionByDefault\": {
					\"KMSMasterKeyID\": \"${kms_key_id}\"
				}
			}]
		}"
}
```

## Function Order

- Define `main` first.
- Place helper functions below `main`.
- Invoke `main` on the last line of the file.

## Function Parameters

- Sort positional parameter unpacking alphabetically by variable name.
- Update every call site to pass arguments in the same alphabetical order.

Before:

```bash
function _update_chart_dependency_version {
	local chart_name="${1}"
	local new_version="${2}"
	local current_chart_yaml="${3}"
}

_update_chart_dependency_version "${helm_chart_name}" "${new_version}" "${helm_chart_yaml}"
```

After:

```bash
function _update_chart_dependency_version {
	local chart_name="${1}"
	local current_chart_yaml="${2}"
	local new_version="${3}"
}

_update_chart_dependency_version "${helm_chart_name}" "${helm_chart_yaml}" "${new_version}"
```

## `if` and `until` Blocks

- Place `then` on its own line, never on the same line as `if`.
- Place `do` on its own line, never on the same line as `until` or `while`.

Before:

```bash
if git -C "${cwd}" show-ref --quiet --verify "refs/heads/${name}"; then
	git -C "${cwd}" worktree add "${target_path}" "${name}" >&2
fi
```

After:

```bash
if git -C "${cwd}" show-ref --quiet --verify "refs/heads/${name}"
then
	git -C "${cwd}" worktree add "${target_path}" "${name}" >&2
fi
```

## Local Declarations

- Declare each `local` variable immediately before its first use, not in a block at the top of the function.
- Use one `local` per variable.
- When the value is computed by a separate assignment, place a blank line between the `local` line and the assignment.
- Separate distinct declaration blocks with a blank line.
- Do not break the flow: keep the declaration adjacent to the code that uses the variable, not interleaved with unrelated commands.

Before:

```bash
function main {
	local cwd
	local input
	local name

	input="$(cat)"

	name="$(jq --exit-status --raw-output '.name' <<< "${input}")"
	cwd="$(jq --exit-status --raw-output '.cwd' <<< "${input}")"
}
```

After:

```bash
function main {
	local input

	input="$(cat)"

	local cwd

	cwd="$(jq --exit-status --raw-output '.cwd' <<< "${input}")"

	local name

	name="$(jq --exit-status --raw-output '.name' <<< "${input}")"
}
```

## Log Messages

- Write complete sentences with sentence-final punctuation.
- For state-change confirmations, use the passive past tense form "was X successfully" rather than "X successfully".
- Phrase failure messages as "Unable to X" rather than "X failed".

Before:

```bash
_log "Bucket ${bucket_name} created successfully."
echo "Terraform apply failed. Attempting to recover the kubectl context." >&2
```

After:

```bash
_log "Bucket ${bucket_name} was created successfully."
echo "Unable to apply Terraform. Attempting to recover the kubectl context." >&2
```

## Top-Level Globals

- Stack related top-level global assignments together with no blank line between them.

Before:

```bash
_GCP_DEPLOYMENT_NAME=""

_GCP_PROJECT_ID=""
```

After:

```bash
_GCP_DEPLOYMENT_NAME=""
_GCP_PROJECT_ID=""
```