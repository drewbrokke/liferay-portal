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

package com.liferay.configuration.admin.portlet.action;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Provides a common interface for all the render commands for the view of
 * factory configuration instances in the System Settings portlet.
 *
 * Render commands should be defined as OSGi components and registered under the
 * type <code>FactoryInstancesListRenderCommand</code>:
 * <pre>
 * {@literal @}Component
 * (
 * 	  property={"configurationPid=com.foo.FooConfiguration"},
 * 	  service = FactoryInstancesListRenderCommand.class
 * )
 * public class MyCustomRenderCommand
 *     implements FactoryInstancesListRenderCommand {
 *
 * }
 * </pre>
 *
 * @author Pei-Jung Lan
 */
public interface FactoryInstancesListRenderCommand {

	public void render(
		RenderRequest renderRequest, RenderResponse renderResponse);

}