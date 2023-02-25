<#include "${templatesPath}/macro-ftl">

<@language_form_with_input>
	<style>
		.language-entry-short-text {
			padding: 0 0.5em;
		}
	</style>

	<#list entries as entry>
		<#if !entry.isDisabled()>
			<@liferay_aui["a"]
				cssClass="language-entry-short-text"
				href=get_url(entry)
				label=entry.getShortDisplayName()
				lang=entry.getW3cLanguageId()
			/>
		</#if>
	</#list>
</@>