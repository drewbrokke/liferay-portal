<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="no-entities-remain-of-this-type"
	id="UADEntities"
>
	<liferay-ui:search-container-results
		results="${entityTypeComposite.getUADEntities()}"
	/>

	<liferay-ui:search-container-row
		className="java.lang.Object"
		escapedModel="${true}"
		keyProperty="name"
		modelVar="uadEntity"
	>
		<liferay-ui:search-container-column-text
			href="${manageUserAssociatedDataEntitiesURL}"
			name="entity-id"
			property="UADEntityId"
		/>

		<liferay-ui:search-container-column-text
			href="${manageUserAssociatedDataEntitiesURL}"
			name="edit-url"
			value="${entityTypeComposite.getEditURL(uadEntity)}"
		/>

		<liferay-ui:search-container-column-text
			href="${manageUserAssociatedDataEntitiesURL}"
			name="nonanonymizable-fields"
			value="${entityTypeComposite.getEntityNonAnonymizableFields(uadEntity)}"
		/>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/entity_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>