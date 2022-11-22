/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.feature.flag.web.internal;

import com.liferay.portal.kernel.util.Portal;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import javax.portlet.ReadOnlyException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/com-liferay-feature-flags-web",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=com.liferay.feature.flag.web.Application",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=false", "liferay.oauth2=false"
	},
	service = Application.class
)
public class FeatureFlagsApplication extends Application {

	@Path("/set-enabled")
	@POST
	public Response confirm(
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse,
		@FormParam("enabled") boolean enabled,
		@FormParam("key") String key) {

		FeatureFlagsPreferencesUtil.setEnabled(_portal.getCompanyId(httpServletRequest), key, enabled);

		return Response.ok(
		).build();
	}

	@Reference
	private Portal _portal;

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}


}