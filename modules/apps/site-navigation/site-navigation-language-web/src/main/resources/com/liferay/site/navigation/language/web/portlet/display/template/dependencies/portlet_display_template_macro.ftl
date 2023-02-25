<#function get_url entry>
	<#if entry.isSelected()>
		<#return ''>
	</#if>

	<#return 'javascript:${namespace}changeLanguage("${entry.getLanguageId()}");'>
</#function>

<#macro language_form>
	<#if entries?has_content>
		<@liferay_aui["form"]
			action=formAction
			method="post"
			name='${namespace + formName}'
			useNamespace=false
		>
			<#nested>
		</@>

		<@liferay_aui["script"]>
			function ${namespace}changeLanguage(languageId) {
				const form = document.${namespace + formName};

				if (languageId) {
					form.${name}.value = languageId;
				}

				submitForm(form);
			}
		</@>
	</#if>
</#macro>

<#macro language_form_with_input>
	<@language_form>
		<@liferay_aui["input"]
			name='${name}'
			type="hidden"
			value='${languageId}'
		/>

		<#nested>
	</@>
</#macro>