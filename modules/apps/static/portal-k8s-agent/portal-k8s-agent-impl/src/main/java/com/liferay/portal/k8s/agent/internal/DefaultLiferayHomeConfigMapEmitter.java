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

package com.liferay.portal.k8s.agent.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gregory Amerson
 */
@Component(
	service = {
		PortalInetSocketAddressEventListener.class,
		PortalK8sConfigMapModifier.class
	}
)
public class DefaultLiferayHomeConfigMapEmitter
	implements PortalInetSocketAddressEventListener,
			   PortalK8sConfigMapModifier {

	@Override
	public Result modifyConfigMap(
		Consumer<PortalK8sConfigMapModifier.ConfigMapModel>
			configMapModelConsumer,
		String configMapName) {

		Objects.requireNonNull(
			configMapModelConsumer, "Config map model consumer is null");

		_validateConfigMapName(configMapName);

		Map<String, String> annotations = new TreeMap<>();
		Map<String, String> binaryData = new TreeMap<>();
		Map<String, String> data = new TreeMap<>();
		Map<String, String> labels = new TreeMap<>();

		configMapModelConsumer.accept(
			new ConfigMapModel() {

				@Override
				public Map<String, String> annotations() {
					return annotations;
				}

				@Override
				public Map<String, String> binaryData() {
					return binaryData;
				}

				@Override
				public Map<String, String> data() {
					return data;
				}

				@Override
				public Map<String, String> labels() {
					return labels;
				}

			});

		if (binaryData.isEmpty() && data.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Config map does not exist and no data was supplied ",
						"for ", configMapName, " resulting in no change"));
			}

			return Result.UNCHANGED;
		}

		_validateLabels(configMapName, labels);
		_updateForPortalLocalPort(labels, _getPortalLocalPort());

		try {
			_writeCXMetadata(data, labels);
		}
		catch (Exception exception) {
			_log.error("Unable to write CX metadata", exception);
		}

		return Result.CREATED;
	}

	@Override
	public void portalLocalInetSocketAddressConfigured(
		InetSocketAddress localInetSocketAddress, boolean secure) {

		_updateDxpMetadata(secure);
	}

	@Override
	public void portalServerInetSocketAddressConfigured(
		InetSocketAddress serverInetSocketAddress, boolean secure) {

		_updateDxpMetadata(secure);
	}

	private Path _getCXMetadataPath() {
		String liferayHome = System.getProperty("liferay.home");

		if (!FileUtil.exists(liferayHome)) {
			return null;
		}

		Path cxMetadataPath = Paths.get(liferayHome, "cx-metadata");

		try {
			cxMetadataPath = Files.createDirectories(cxMetadataPath);
		}
		catch (IOException ioException) {
			_log.error("Could not create CX Metadata path", ioException);
		}

		return cxMetadataPath;
	}

	private int _getPortalLocalPort() {
		return _portal.getPortalLocalPort(
			Objects.equals(_getWebServerProtocol(), "https"));
	}

	private String _getWebServerProtocol() {
		String webServerProtocol = PropsValues.WEB_SERVER_PROTOCOL;

		if (Validator.isNull(webServerProtocol)) {
			return Http.HTTP;
		}

		return webServerProtocol;
	}

	private void _updateDxpMetadata(boolean secure) {
		try {
			Files.walkFileTree(
				_getCXMetadataPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (Objects.equals(
								String.valueOf(path.getFileName()),
								"dxp-metadata")) {

							File dir = path.toFile();

							Map<String, String> labels = new HashMap<>();

							for (File file : dir.listFiles()) {
								labels.put(
									file.getName(),
									new String(
										Files.readAllBytes(file.toPath())));
							}

							_updateForPortalLocalPort(
								labels, _portal.getPortalLocalPort(secure));

							labels.forEach(
								(key, value) -> {
									Path file = path.resolve(key);

									try {
										Files.write(file, value.getBytes());
									}
									catch (IOException ioException) {
										_log.error(
											"Unable to write file " +
												file.toString(),
											ioException);
									}
								});

							return FileVisitResult.SKIP_SUBTREE;
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			_log.error("Unable to update DXP Metadata", ioException);
		}
	}

	private void _updateForPortalLocalPort(
		Map<String, String> labels, int portalLocalPort) {

		if (portalLocalPort <= 0) {
			return;
		}

		String lxcDXPMainDomain = labels.get("com.liferay.lxc.dxp.mainDomain");

		if ((lxcDXPMainDomain != null) &&
			(lxcDXPMainDomain.indexOf(':') == -1)) {

			labels.put(
				"com.liferay.lxc.dxp.mainDomain",
				lxcDXPMainDomain + ":" + portalLocalPort);
		}

		List<String> lxcDXPDomains = StringUtil.split(
			labels.get("com.liferay.lxc.dxp.domains"), CharPool.NEW_LINE);

		if (!lxcDXPDomains.isEmpty()) {
			List<String> updatedLxcDXPDomains = new ArrayList<>();

			for (String lxcDXPDomain : lxcDXPDomains) {
				if ((lxcDXPDomain != null) &&
					(lxcDXPDomain.indexOf(":") == -1)) {

					updatedLxcDXPDomains.add(
						lxcDXPDomain + ":" + portalLocalPort);
				}
				else {
					updatedLxcDXPDomains.add(lxcDXPDomain);
				}
			}

			labels.put(
				"com.liferay.lxc.dxp.domains",
				StringUtil.merge(updatedLxcDXPDomains, StringPool.NEW_LINE));
		}
	}

	private void _validateConfigMapName(String configMapName) {
		Objects.requireNonNull(configMapName, "Config map name is null");

		if (!configMapName.endsWith("-lxc-dxp-metadata") &&
			!configMapName.endsWith("-lxc-ext-init-metadata")) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map name ", configMapName,
					" does not follow a recognized pattern"));
		}
	}

	private void _validateLabels(
		String configMapName, Map<String, String> labels) {

		_validateConfigMapName(configMapName);

		String metadataType = labels.get("lxc.liferay.com/metadataType");

		if ((metadataType == null) ||
			(!Objects.equals(metadataType, "dxp") &&
			 !Objects.equals(metadataType, "ext-init"))) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map labels must contain the key ",
					"\"lxc.liferay.com/metadataType\" with a value of \"dxp\" ",
					"or \"ext-init\""));
		}

		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if (virtualInstanceId == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map labels must contain the key ",
					"\"dxp.lxc.liferay.com/virtualInstanceId\" whose value is ",
					"the web ID of the virtual instance from which the ",
					"configuration originated"));
		}

		// <virtualInstanceId>-lxc-dxp-metadata

		if (configMapName.endsWith("-lxc-dxp-metadata") &&
			!Objects.equals(
				virtualInstanceId.concat("-lxc-dxp-metadata"), configMapName)) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"A config map name with the suffix \"-lxc-dxp-metadata\" ",
					"must begin with the value of the label ",
					"\"dxp.lxc.liferay.com/virtualInstanceId\" followed by ",
					"\"-lxc-dxp-metadata\""));
		}

		// <projectId>-<virtualInstanceId>-lxc-ext-init-metadata

		else if (configMapName.endsWith("-lxc-ext-init-metadata")) {
			String projectId = labels.get("ext.lxc.liferay.com/projectId");

			if (projectId == null) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"A config map with the suffix ",
						"\"-lxc-ext-init-metadata\" must have a label with ",
						"the key \"ext.lxc.liferay.com/projectId\" whose ",
						"value is the name of the local project"));
			}

			if (!Objects.equals(
					configMapName,
					StringBundler.concat(
						projectId, "-", virtualInstanceId,
						"-lxc-ext-init-metadata"))) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"A config map name with suffix ",
						"\"-lxc-ext-init-metadata\" must begin with the value ",
						"of the label \"ext.lxc.liferay.com/projectId\" ",
						"followed by a \"-\" and then the value of the label ",
						"\"dxp.lxc.liferay.com/virtualInstanceId\" followed ",
						"by \"-lxc-ext-init-metadata\""));
			}
		}
	}

	private void _writeCXData(Path dataPath, Map<String, String> data) {
		data.forEach(
			(key, value) -> {
				Path keyPath = dataPath.resolve(key);

				try {
					Files.write(
						keyPath, value.getBytes(), StandardOpenOption.CREATE);
				}
				catch (IOException ioException) {
					_log.error("Unable to write CX data", ioException);
				}
			});
	}

	private void _writeCXMetadata(
			Map<String, String> data, Map<String, String> labels)
		throws Exception {

		Path cxMetadataPath = _getCXMetadataPath();

		if (cxMetadataPath == null) {
			return;
		}

		String metadataType = labels.get("lxc.liferay.com/metadataType");

		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if ((metadataType == null) || (virtualInstanceId == null)) {
			return;
		}

		if (Objects.equals(
				virtualInstanceId, PropsValues.COMPANY_DEFAULT_WEB_ID)) {

			virtualInstanceId = "default";
		}

		Path virtualInstanceIdPath = cxMetadataPath.resolve(virtualInstanceId);

		Files.createDirectories(virtualInstanceIdPath);

		Path dxpMetadataPath = virtualInstanceIdPath.resolve("dxp-metadata");

		if (Objects.equals(metadataType, "dxp")) {
			Files.createDirectories(dxpMetadataPath);

			_writeCXData(dxpMetadataPath, data);
		}
		else if (Objects.equals(metadataType, "ext-init")) {
			String projectName = labels.get("ext.lxc.liferay.com/projectName");

			Path projectPath = virtualInstanceIdPath.resolve(projectName);

			Files.createDirectories(projectPath);

			Path extTnitMetadataPath = projectPath.resolve("ext-init-metadata");

			Files.createDirectories(extTnitMetadataPath);

			_writeCXData(extTnitMetadataPath, data);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultLiferayHomeConfigMapEmitter.class);

	@Reference
	private Portal _portal;

}