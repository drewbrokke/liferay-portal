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

<%
String redirect = ParamUtil.getString(request, "redirect");
long phoneId = ParamUtil.getLong(request, "phoneId", 0L);

Phone phone = null;

if (phoneId > 0L) {
	phone = PhoneServiceUtil.getPhone(phoneId);
}
%>

<aui:form action="<%= redirect %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="phoneNumberFm">
	<aui:fieldset-group markupView="lexicon">
		<div class="alert alert-info">
			<liferay-ui:message key="phone-number-and-type-are-required-fields.-extension-must-be-numeric" />
		</div>

		<div class="sheet">
			<aui:fieldset id="phoneNumbers">
				<c:if test="<%= phone != null %>">
					<aui:model-context bean="<%= phone %>" model="<%= Phone.class %>" />
				</c:if>

				<div class="form-group-autofit lfr-form-row">
					<aui:input name="phoneId" type="hidden" value="<%= phoneId %>" />

					<div class="form-group-item">
						<aui:input checked="<%= (phone != null)? phone.isPrimary() : false %>" id="phonePrimary" inlineField="<%= true %>" label="make-primary" name="phonePrimary" type="checkbox" />
					</div>

					<div class="form-group-item">
						<aui:input fieldParam="phoneNumber" id="phoneNumber" inlineField="<%= true %>" name="number" />
					</div>

					<div class="form-group-item">
						<aui:input fieldParam="phoneExtension" id="phoneExtension" inlineField="<%= true %>" name="extension" />
					</div>

					<div class="form-group-item">
						<aui:select inlineField="<%= true %>" label="type" listType="<%= Organization.class.getName() + ListTypeConstants.PHONE %>" name="phoneTypeId" />
					</div>
				</div>
			</aui:fieldset>
		</div>
	</aui:fieldset-group>
</aui:form>