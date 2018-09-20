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

<portlet:actionURL name="/users_admin/update_organization_contact_information" var="editPhoneURL" />

<aui:form action="<%= editPhoneURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="edit_phone_number_fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset-group markupView="lexicon">
		<div class="sheet">
			<div class="sheet-section">
				<div class="alert alert-info">
					<liferay-ui:message key="phone-number-and-type-are-required-fields.-extension-must-be-numeric" />
				</div>

				<liferay-ui:error key='<%= NoSuchListTypeException.class.getName() + "phones.className" + ListTypeConstants.PHONE %>' message="please-select-a-type" />
				<liferay-ui:error exception="<%= PhoneNumberException.class %>" message="please-enter-a-valid-phone-number" />
				<liferay-ui:error exception="<%= PhoneNumberExtensionException.class %>" message="please-enter-a-valid-phone-number-extension" />

				<aui:fieldset id="phoneNumbers">
					<div class="form-group-autofit lfr-form-row">
						<aui:input name="phoneId" type="hidden" value="<%= phoneId %>" />

						<div class="form-group-item">
							<aui:input fieldParam="phoneNumber" id="phoneNumber" inlineField="<%= true %>" name="number" value='<%= (phoneId > 0L)? phone.getNumber() : "" %>' />
						</div>

						<div class="form-group-item">
							<aui:input fieldParam="phoneExtension" id="phoneExtension" inlineField="<%= true %>" name="extension" value='<%= (phoneId > 0L)? phone.getExtension() : "" %>' />
						</div>

						<div class="form-group-item">
							<aui:select inlineField="<%= true %>" label="type" listType="<%= Phone.class.getName() + ListTypeConstants.PHONE %>" name="phoneTypeId" />
						</div>

						<div class="form-group-item form-group-item-label-spacer">
							<aui:input checked="<%= (phoneId > 0L)? phone.isPrimary() : false %>" id="phonePrimary" inlineField="<%= true %>" label="primary" name="phonePrimary" type="checkbox" />
						</div>
					</div>
				</aui:fieldset>
			</div>

			<div class="sheet-footer">
				<aui:button primary="<%= true %>" type="submit" />
			</div>
		</div>
	</aui:fieldset-group>
</aui:form>