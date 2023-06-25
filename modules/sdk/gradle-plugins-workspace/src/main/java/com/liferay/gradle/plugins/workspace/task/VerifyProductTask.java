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
	public String getProcut() {
		return _product;
	}

	@Input
	@Optional
	public WorkspaceExtension.ProductInfo getProcutInfo() {
		return _productInfo;
	}

	public void setProduct(String product) {
		_product = product;
	}

	public void setProductInfo(WorkspaceExtension.ProductInfo productInfo) {
		_productInfo = productInfo;
	}

	@TaskAction
	public void verifyProduct() throws Exception {
		if (Objects.isNull(_productInfo)) {
			throw new GradleException(
				"Can not get produtInfo for prdocut " + _product);
		}

		if (Validator.isNull(_productInfo.getAppServerTomcatVersion())) {
			throw new GradleException(
				"Can not get correct tomcat version for prdocut " +
					_product);
		}

		if (Validator.isNull(_productInfo.getBundleChecksumMD5())) {
			throw new GradleException(
				"Can not get correct bundleChecksumMD5 for prdocut " +
					_product);
		}

		if (Validator.isNull(_productInfo.getBundleUrl())) {
			throw new GradleException(
				"Can not get correct bundle url for prdocut " + _product);
		}

		if (Validator.isNull(_productInfo.getLiferayDockerImage())) {
			throw new GradleException(
				"Can not get correct liferay docker image for prdocut " +
					_product);
		}

		if (Validator.isNull(_productInfo.getLiferayProductVersion())) {
			throw new GradleException(
				"Can not get correct product version for prdocut " + _product);
		}

		if (Validator.isNull(_productInfo.getReleaseDate())) {
			throw new GradleException(
				"Can not get correct release date for prdocut " + _product);
		}

		if (Validator.isNull(_productInfo.getTargetPlatformVersion())) {
			throw new GradleException(
				"Can not get correct tareget platform version for prdocut " +
					_product);
		}
	}

	private String _product;
	private WorkspaceExtension.ProductInfo _productInfo;

}