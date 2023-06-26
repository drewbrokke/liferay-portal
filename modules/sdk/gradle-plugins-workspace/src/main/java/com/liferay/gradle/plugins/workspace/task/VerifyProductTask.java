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

package com.liferay.gradle.plugins.workspace.task;

import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.util.Validator;

import java.util.Objects;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Simon Jiang
 */
public class VerifyProductTask extends DefaultTask {

	@Input
	@Optional
	public WorkspaceExtension getExtension() {
		return _extension;
	}

	@Input
	@Optional
	public String getProcut() {
		return _product;
	}

	public void setExtension(WorkspaceExtension extension) {
		_extension = extension;
	}

	public void setProduct(String product) {
		_product = product;
	}

	@TaskAction
	public void verifyProduct() throws Exception {
		WorkspaceExtension.ProductInfo productInfo =
			_extension.getProductInfo();

		if (Objects.isNull(productInfo)) {
			throw new GradleException(
				"Can not get produtInfo for product '" + _product + "'");
		}

		String defaultAppServerTomcatVersion =
			productInfo.getAppServerTomcatVersion();

		if (Objects.equals(
				_extension.getAppServerTomcatVersion(),
				defaultAppServerTomcatVersion) &&
			Validator.isNull(defaultAppServerTomcatVersion)) {

			throw new GradleException(
				"Can not get correct tomcat version for product '" + _product +
					"'");
		}

		String defaultBundleChecksumMD5 = productInfo.getBundleChecksumMD5();

		if (Objects.equals(
				_extension.getBundleChecksumMD5(), defaultBundleChecksumMD5) &&
			Validator.isNull(defaultBundleChecksumMD5)) {

			throw new GradleException(
				"Can not get correct bundleChecksumMD5 for product '" +
					_product + "'");
		}

		String defaultBundleUrl = productInfo.getBundleUrl();

		if (Objects.equals(_extension.getBundleUrl(), defaultBundleUrl) &&
			Validator.isNull(defaultBundleUrl)) {

			throw new GradleException(
				"Can not get correct bundle url for product '" + _product +
					"'");
		}

		String defaultLiferayDockerImage = productInfo.getLiferayDockerImage();

		if (Objects.equals(
				_extension.getDockerImageLiferay(),
				defaultLiferayDockerImage) &&
			Validator.isNull(defaultLiferayDockerImage)) {

			throw new GradleException(
				"Can not get correct liferay docker image for product '" +
					_product + "'");
		}

		String defaultTargetPlatformVersion =
			productInfo.getTargetPlatformVersion();

		if (Objects.equals(
				_extension.getTargetPlatformVersion(),
				defaultTargetPlatformVersion) &&
			Validator.isNull(defaultTargetPlatformVersion)) {

			throw new GradleException(
				"Can not get correct tareget platform version for product '" +
					_product + "'");
		}
	}

	private WorkspaceExtension _extension;
	private String _product;

}