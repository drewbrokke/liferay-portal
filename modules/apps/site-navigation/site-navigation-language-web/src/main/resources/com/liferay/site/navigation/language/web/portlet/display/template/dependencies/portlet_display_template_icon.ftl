<#include "${templatesPath}/macro-ftl">

<@language_form_with_input>
	<#list entries as entry>
		<#if entry.isSelected()>
			<#assign cssClass = "current-language" />
		</#if>

		<#if !entry.isDisabled()>
			<@liferay_aui["icon"]
				ariaLabel=entry.getLongDisplayName()
				cssClass=cssClass
				image=entry.getW3cLanguageId()?lower_case
				markupView="lexicon"
				message=entry.getLongDisplayName()
				url=get_url(entry)
			/>
		</#if>
	</#list>
</@>