package com.liferay.frontend.token.definition.register;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;

public interface CETFrontendTokenDefinitionRegistry {

	public FrontendTokenDefinition getTokenDefinition(
		long companyId, String externalReferenceCode);

}