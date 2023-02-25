<#include "${templatesPath}/macro-ftl">

<@language_form_with_input>
	<style>
		.language-entry-long-text {
			display: inline-block;
			padding: 0 0.5em;
		}
	</style>

	<#list entries as entry>
		<#if !entry.isDisabled()>
			<@liferay_aui["a"]
				cssClass="language-entry-long-text"
				href=get_url(entry)
				label=entry.getLongDisplayName()
				lang=entry.getW3cLanguageId()
				localizeLabel=false
			/>
		</#if>
	</#list>
</@>