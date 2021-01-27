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

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreenAlertProvider;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util.InceptionModelUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.io.PrintWriter;
import java.io.Serializable;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration",
	property = "configuration.pid=com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration",
	service = ConfigurationScreenAlertProvider.class
)
public class TensorFlowImageAssetAutoTagProviderConfigurationScreenAlertProvider
	implements ConfigurationScreenAlertProvider {

	@Override
	public Alert getAlert(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		try {
			if (!InceptionModelUtil.isDownloaded()) {
				return new Alert() {

					@Override
					public String getType() {
						if (InceptionModelUtil.isDownloadFailed()) {
							return "danger	";
						}

						return "info";
					}

					@Override
					public void render(
							HttpServletRequest httpServletRequest,
							HttpServletResponse httpServletResponse)
						throws Exception {

						PrintWriter writer = httpServletResponse.getWriter();

						writer.write(
							ResourceBundleUtil.getString(
								ResourceBundleUtil.getBundle(
									httpServletRequest.getLocale(), getClass()),
								_getAlertMessageKey(
									_isTensorFlowImageAssetAutoTagProviderEnabled(
										scope, scopePK))));
					}

				};
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return null;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
			ConfigurableUtil.createConfigurable(
				TensorFlowImageAssetAutoTagProviderCompanyConfiguration.class,
				properties);
	}

	private String _getAlertMessageKey(
		boolean tensorFlowImageAssetAutoTagProviderEnabled) {

		if (InceptionModelUtil.isDownloadFailed()) {
			return "the-tensorflow-model-could-not-be-downloaded.-please-" +
				"contact-your-administrator";
		}

		if (tensorFlowImageAssetAutoTagProviderEnabled) {
			return "the-tensorflow-model-is-being-downloaded-in-the-" +
				"background.-no-tags-will-be-created-until-the-model-is-" +
					"fully-downloaded";
		}

		return "the-tensorflow-model-will-be-downloaded-in-the-background.-" +
			"no-tags-will-be-created-until-the-model-is-fully-downloaded";
	}

	private boolean _isTensorFlowImageAssetAutoTagProviderEnabled(
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK)
		throws PortalException {

		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			return _tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
				enabled();
		}

		if (ExtendedObjectClassDefinition.Scope.COMPANY.equals(scope)) {
			TensorFlowImageAssetAutoTagProviderCompanyConfiguration
				tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
					_configurationProvider.getCompanyConfiguration(
						TensorFlowImageAssetAutoTagProviderCompanyConfiguration.
							class,
						(long)scopePK);

			if (tensorFlowImageAssetAutoTagProviderCompanyConfiguration !=
					null) {

				return tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
					enabled();
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TensorFlowImageAssetAutoTagProviderConfigurationScreenAlertProvider.
			class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile TensorFlowImageAssetAutoTagProviderCompanyConfiguration
		_tensorFlowImageAssetAutoTagProviderCompanyConfiguration;

}