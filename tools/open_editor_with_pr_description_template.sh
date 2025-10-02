#!/bin/bash

# Define the input file and output file names
input_template=~/.pr_description_template.md
adapted_template=$(mktemp /tmp/pr_description_template.XXXXXXXXXXX.md)

# Declare arrays to store sections to omit and omitted sections
declare -a sections_to_omit
declare -a omitted_sections

declare -a gh_args

# Function to generate a simplified, consistent short name from a section title
get_short_name() {
    local title="$1"
    # Convert to lowercase and replace spaces with hyphens
    local sanitized_title=$(echo "$title" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')
    # Remove punctuation and special characters
    sanitized_title=$(echo "$sanitized_title" | sed 's/[^a-z0-9-]//g' | sed 's/--/-/g')
    
    # Custom abbreviations for common phrases
    case "$sanitized_title" in
        "why-doesnt-this-include-any-test") echo "test" ;;
        "has-a11y-been-checked") echo "a11y" ;;
        "do-you-include-custom-css") echo "css" ;;
        "have-you-introduced-breaking-changes") echo "breaking" ;;
        "are-you-affecting-other-teams-functionalities") echo "teams" ;;
        "how-does-this-affect-performance") echo "performance" ;;
        "have-you-followed-well-established-secure-coding-patterns") echo "secure" ;;
        *) echo "$sanitized_title" ;; # Default to the full sanitized name
    esac
}

# Function to display help and usage
usage() {
    echo "Usage: $0 [--no-section-1 --no-section-2 ...]"
    echo "This script create a tailored template for your PR description"
    echo ""
    echo "Available sections to omit (use --no-XXXX):"
    grep -E '^## ' "$input_template" | sed 's/## //' | while read -r line; do
        short_name=$(get_short_name "$line")
        echo "  --no-$short_name"
    done
    echo ""
    exit 1
}

# Check if the input file is provided and exists
if [ -z "$input_template" ] || [ ! -f "$input_template" ]; then
    echo "Error: File '$input_template' not found or not specified."
    usage
fi

# Parse command line arguments to identify sections to omit
for arg in "$@"; do
    if [[ "$arg" == "--no-"* ]]; then
        sections_to_omit+=("${arg#--no-}")
    elif [ "$arg" == "-h" ] || [ "$arg" == "--help" ]; then
        usage
    else
        gh_args+="${arg}"
    fi
done

# If no sections are specified for omission, just copy the file
if [ ${#sections_to_omit[@]} -eq 0 ]; then
    echo "No sections specified for omission. Creating a copy of the original template."
    cp "$input_template" "$adapted_template"
#     exit 0
fi

# Process the markdown file
omitting=false
> "$adapted_template" # Clear the output file before writing
while IFS= read -r line; do
    # Check if the line is a level 2 heading
    if [[ "$line" =~ ^"## " ]]; then
        # Extract the section title and get its short name
        title_raw=$(echo "$line" | sed 's/^## //')
        title_short_name=$(get_short_name "$title_raw")

        # Check if this section should be omitted
        should_omit=false
        for omit_name in "${sections_to_omit[@]}"; do
            if [ "$title_short_name" == "$omit_name" ]; then
                should_omit=true
                omitted_sections+=("$title_raw")
                break
            fi
        done

        if [ "$should_omit" == true ]; then
            omitting=true
            continue
        else
            omitting=false
            echo "$line" >> "$adapted_template"
        fi
    elif [ "$omitting" == false ]; then
        # If not a header and we are not currently omitting, write the line
        echo "$line" >> "$adapted_template"
    fi
done < "$input_template"

# Add the "Omitted Sections" summary at the end
# if [ ${#omitted_sections[@]} -gt 0 ]; then
#     echo "" >> "$adapted_template"
#     echo "---" >> "$adapted_template"
#     echo "" >> "$adapted_template"
#     echo "## Omitted Sections" >> "$adapted_template"
#     for section_title in "${omitted_sections[@]}"; do
#         echo "  - $section_title" >> "$adapted_template"
#     done
# fi

echo "Successfully processed '$input_template'. Modified template saved to '$adapted_template'."

remote=$(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null | cut -d/ -f1)
git push --set-upstream ${remote:-origin} $(git branch --show-current)
prefilled_template=$(mktemp /tmp/g.XXXXXXXXXXX.md)
if [[ -f $adapted_template ]]
then
    lps=$(git show --pretty='format:%s' --no-patch | cut -d' ' -f1)
    if [[ ! -z $lps ]]
    then
        url="https://liferay.atlassian.net/browse/"$lps
        sed -e "s/{lps}/$lps/g" -e "s,{url},$url," <$adapted_template >$prefilled_template
    fi
fi

vi ${prefilled_template}

#echo ${GH_EDITOR:-${VISUAL:-${EDITOR:-ed}}} ${prefilled_template} >/dev/null

# Function to extract and clean 2nd-level sections
extract_sections() {
    local file=$1
    # Use grep to find lines starting with '## ', then sed to remove the '## ' prefix and trim whitespace
    grep "^## " "$file" | sed 's/^## //g' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'
}

# Extract sections from both files and sort them
template_sections=$(extract_sections "$input_template" | sort)
filled_sections=$(extract_sections "${prefilled_template}" | sort)

# Use `comm` to find lines unique to the first file (template_sections)
omitted_sections=$(comm -23 <(echo "$template_sections") <(echo "$filled_sections"))

# Append the missing sections to the filled file if any were found
if [ -n "$omitted_sections" ]; then    
    # Append the sections to the filled file with a 2nd-level heading and a placeholder for content
    echo -e "\n\n" >> "${prefilled_template}"
    echo -e "## Omitted Sections" >> "${prefilled_template}"
    
    echo "$omitted_sections" | while read -r line; do
        echo "  - $line" >> "${prefilled_template}"
    done
fi

echo gh $gh_args --title "$(head -1 $prefilled_template)" --body "$(tail -n +3 $prefilled_template)"
mv -f $prefilled_template /tmp/g.lastpr

