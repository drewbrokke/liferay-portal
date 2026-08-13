/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.one.constants.ClassNameConstants;
import com.liferay.one.exception.LicenseKeyActiveException;
import com.liferay.one.exception.NoSuchLicenseKeyException;
import com.liferay.one.license.LicenseKeyExporter;
import com.liferay.one.license.LicenseKeyGenerator;
import com.liferay.one.license.LicenseKeyValidator;
import com.liferay.one.model.LicenseKey;
import com.liferay.one.model.SubscriptionEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.ee.license.shared.LicenseConstants;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 * @author Allen Ziegenfus
 */
@Component
public class LicenseKeyService extends OneBaseService {

	public LicenseKey addLicenseKey(
			String accountName, boolean active, String additionalInfo,
			boolean complimentary, String description, String domains,
			Date expirationDate, String hostName, String ipAddresses,
			String licenseName, String licenseType, int licenseVersion,
			String macAddresses, int maxClusterNodes, long maxConcurrentUsers,
			int maxHttpSessions, int maxServers, long maxUsers, String name,
			String orderId, String owner, String productExternalId,
			String productName, String productVersion, String serverId,
			String sizing, Date startDate)
		throws Exception {

		_licenseKeyValidator.validateMetadata(
			description, licenseType, maxClusterNodes, name, owner,
			productVersion);

		_licenseKeyValidator.validateDates(
			expirationDate, hostName, ipAddresses, licenseType, macAddresses,
			startDate);

		String key = _licenseKeyGenerator.generateKey(
			accountName, licenseName, licenseType, licenseVersion, productName,
			productExternalId, productVersion, owner, maxClusterNodes,
			maxServers, maxHttpSessions, maxConcurrentUsers, maxUsers, sizing,
			description, domains, hostName, ipAddresses, macAddresses, serverId,
			startDate, expirationDate);

		JSONObject jsonObject = new JSONObject(
		).put(
			"accountName", accountName
		).put(
			"active", active
		).put(
			"additionalInfo", additionalInfo
		).put(
			"complimentary", complimentary
		).put(
			"customExpirationDate", _toISO8601(expirationDate)
		).put(
			"description", description
		).put(
			"domains", domains
		).put(
			"hostName", hostName
		).put(
			"ipAddresses", ipAddresses
		).put(
			"key", key
		).put(
			"licenseName", licenseName
		).put(
			"licenseType", licenseType
		).put(
			"licenseVersion", licenseVersion
		).put(
			"macAddresses", macAddresses
		).put(
			"maxClusterNodes", maxClusterNodes
		).put(
			"maxConcurrentUsers", maxConcurrentUsers
		).put(
			"maxHttpSessions", maxHttpSessions
		).put(
			"maxServers", maxServers
		).put(
			"maxUsers", maxUsers
		).put(
			"name", name
		).put(
			"orderId", orderId
		).put(
			"owner", owner
		).put(
			"productExternalId", productExternalId
		).put(
			"productName", productName
		).put(
			"productVersion", productVersion
		).put(
			"serverId", serverId
		).put(
			"sizing", sizing
		).put(
			"startDate", _toISO8601(startDate)
		);

		String response = post(
			getAuthorization(), jsonObject.toString(),
			UriComponentsBuilder.fromPath(
				_PATH
			).build(
			).toUri());

		return new LicenseKey(new JSONObject(response));
	}

	public LicenseKey addLicenseKeyTypeFree(
			long accountEntryId, String domains, String orderId, String owner)
		throws Exception {

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		calendar.set(Calendar.MILLISECOND, 0);

		Date startDate = calendar.getTime();

		calendar.add(Calendar.MONTH, _FREE_TIER_DURATION_MONTHS);

		Date expirationDate = calendar.getTime();

		String productVersion = getFreeTierProductVersion();

		String key = _licenseKeyGenerator.generateKey(
			StringPool.BLANK, _FREE_TIER_LICENSE_NAME,
			LicenseConstants.TYPE_FREE, _FREE_TIER_LICENSE_VERSION,
			_FREE_TIER_PRODUCT_NAME, LicenseConstants.PRODUCT_ID_PORTAL,
			productVersion, owner, _FREE_TIER_MAX_CLUSTER_NODES, 0, 0, 0L, 0L,
			StringPool.BLANK, StringPool.BLANK, domains, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, startDate,
			expirationDate);

		JSONObject jsonObject = new JSONObject(
		).put(
			"active", true
		).put(
			"complimentary", false
		).put(
			"customExpirationDate", _toISO8601(expirationDate)
		).put(
			"domains", domains
		).put(
			"key", key
		).put(
			"licenseName", _FREE_TIER_LICENSE_NAME
		).put(
			"licenseType", LicenseConstants.TYPE_FREE
		).put(
			"licenseVersion", _FREE_TIER_LICENSE_VERSION
		).put(
			"maxClusterNodes", _FREE_TIER_MAX_CLUSTER_NODES
		).put(
			"name", _FREE_TIER_LICENSE_NAME
		).put(
			"orderId", orderId
		).put(
			"owner", owner
		).put(
			"productExternalId", LicenseConstants.PRODUCT_ID_PORTAL
		).put(
			"productName", _FREE_TIER_PRODUCT_NAME
		).put(
			"productVersion", productVersion
		).put(
			"r_accountEntryToLicenseKey_accountEntryId", accountEntryId
		).put(
			"r_commerceProductToLicenseKey_CProductERC", _FREE_TIER_PRODUCT_ERC
		).put(
			"startDate", _toISO8601(startDate)
		);

		String response = post(
			getAuthorization(), jsonObject.toString(),
			UriComponentsBuilder.fromPath(
				_PATH
			).build(
			).toUri());

		return new LicenseKey(new JSONObject(response));
	}

	public LicenseKey extendLicenseKey(
			Date expirationDate, long licenseKeyId, Date startDate)
		throws Exception {

		LicenseKey newLicenseKey = _copyLicenseKey(
			expirationDate, getLicenseKey(licenseKeyId), startDate);

		List<SubscriptionEntry> subscriptionEntries =
			_subscriptionEntryService.getSubscriptionEntries(
				and(
					eq("className", ClassNameConstants.LICENSE_KEY),
					eq("classPK", licenseKeyId)));

		for (SubscriptionEntry subscriptionEntry : subscriptionEntries) {
			_subscriptionEntryService.addSubscriptionEntry(
				null, ClassNameConstants.LICENSE_KEY,
				newLicenseKey.getLicenseKeyId(),
				subscriptionEntry.getCustomUserId());
		}

		return newLicenseKey;
	}

	public List<LicenseKey> getAssetReceiptLicenseLicenseKeys(
			boolean active, boolean complimentary, String orderId)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("active", active), eq("complimentary", complimentary),
				eq("orderId", orderId)));
	}

	public int getAssetReceiptLicenseLicenseKeysCount(
			boolean active, boolean complimentary, String orderId)
		throws Exception {

		return _getCount(
			and(
				eq("active", active), eq("complimentary", complimentary),
				eq("orderId", orderId)));
	}

	public LicenseKey getLicenseKey(Jwt jwt, long licenseKeyId)
		throws Exception {

		try {
			String response = get(
				getAuthorization(jwt),
				UriComponentsBuilder.fromPath(
					_PATH + "/{licenseKeyId}"
				).buildAndExpand(
					licenseKeyId
				).toUri());

			return new LicenseKey(new JSONObject(response));
		}
		catch (WebClientResponseException webClientResponseException) {
			int statusCode = webClientResponseException.getStatusCode(
			).value();

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				throw new NoSuchLicenseKeyException(
					"No license key exists with ID " + licenseKeyId);
			}

			throw webClientResponseException;
		}
	}

	public LicenseKey getLicenseKey(long licenseKeyId) throws Exception {
		String response = get(
			getAuthorization(),
			UriComponentsBuilder.fromPath(
				_PATH + "/{id}"
			).buildAndExpand(
				licenseKeyId
			).toUri());

		return new LicenseKey(new JSONObject(response));
	}

	public LicenseKey getLicenseKeyByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		List<LicenseKey> licenseKeys = getLicenseKeys(
			eq("externalReferenceCode", externalReferenceCode));

		if (licenseKeys.isEmpty()) {
			throw new NoSuchLicenseKeyException(
				"{externalReferenceCode=" + externalReferenceCode + "}");
		}

		return licenseKeys.get(0);
	}

	public String getLicenseKeyDownloadFileName(LicenseKey licenseKey) {
		return _licenseKeyExporter.getFileName(
			licenseKey.getProductName(), licenseKey.getProductVersion(),
			licenseKey.getName());
	}

	public String getLicenseKeyDownloadXML(LicenseKey licenseKey)
		throws Exception {

		return _licenseKeyExporter.toXML(
			licenseKey.getKey(), licenseKey.getAccountName(),
			licenseKey.getLicenseName(), licenseKey.getLicenseType(),
			licenseKey.getLicenseVersion(), licenseKey.getProductName(),
			licenseKey.getProductExternalId(), licenseKey.getProductVersion(),
			licenseKey.getOwner(), licenseKey.getMaxClusterNodes(),
			licenseKey.getMaxServers(), licenseKey.getMaxHttpSessions(),
			licenseKey.getMaxConcurrentUsers(), licenseKey.getMaxUsers(),
			licenseKey.getSizing(), licenseKey.getDescription(),
			licenseKey.getDomains(), licenseKey.getHostName(),
			licenseKey.getIpAddresses(), licenseKey.getMacAddresses(),
			licenseKey.getServerId(),
			Date.from(licenseKey.getStartDateInstant()),
			Date.from(licenseKey.getCustomExpirationDateInstant()));
	}

	public List<LicenseKey> getLicenseKeys(
			boolean active, boolean complimentary, long entitlementId)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("active", active), eq("complimentary", complimentary),
				eq("entitlementId", entitlementId)));
	}

	public List<LicenseKey> getLicenseKeys(
			boolean active, String orderId, String productExternalId,
			String serverId)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("active", active), eq("orderId", orderId),
				eq("productExternalId", productExternalId),
				eq("serverId", serverId)));
	}

	public List<LicenseKey> getLicenseKeys(String filterString)
		throws Exception {

		return getAllItems(_PATH, filterString, LicenseKey::new);
	}

	public List<LicenseKey> getLicenseKeys(
			String productExternalId, String serverId)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("productExternalId", productExternalId),
				eq("serverId", serverId)));
	}

	public List<LicenseKey> getLicenseKeys(
			String domains, String licenseType, String owner)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("domains", domains), eq("licenseType", licenseType),
				eq("owner", owner)));
	}

	public List<LicenseKey> getLicenseKeysByName(
			boolean active, String productName, String serverId)
		throws Exception {

		return getLicenseKeys(
			and(
				eq("active", active), eq("productName", productName),
				eq("serverId", serverId)));
	}

	public boolean hasValidLicenseKeyTypeFree(String domains, String owner)
		throws Exception {

		Instant renewalThreshold = Instant.now(
		).plus(
			_FREE_TIER_RENEWAL_THRESHOLD_DAYS, ChronoUnit.DAYS
		);

		for (LicenseKey licenseKey :
				getLicenseKeys(domains, LicenseConstants.TYPE_FREE, owner)) {

			Instant customExpirationDateInstant =
				licenseKey.getCustomExpirationDateInstant();

			if ((customExpirationDateInstant != null) &&
				customExpirationDateInstant.isAfter(renewalThreshold)) {

				return true;
			}
		}

		return false;
	}

	public LicenseKey replaceLicenseKey(
			Date expirationDate, long licenseKeyId, Date startDate)
		throws Exception {

		LicenseKey licenseKey = getLicenseKey(licenseKeyId);

		if (Validator.isNotNull(licenseKey.getOrderId()) &&
			!licenseKey.isActive()) {

			throw new LicenseKeyActiveException();
		}

		LicenseKey newLicenseKey = _copyLicenseKey(
			expirationDate, licenseKey, startDate);

		updateLicenseKey(false, licenseKey.isComplimentary(), licenseKeyId);

		return newLicenseKey;
	}

	public List<LicenseKey> search(
			Boolean active, String description, String hostName,
			String ipAddress, String licenseType, String macAddress,
			String owner, String productExternalId, String productName,
			String serverId)
		throws Exception {

		return getLicenseKeys(
			_buildSearchFilter(
				active, description, hostName, ipAddress, licenseType,
				macAddress, owner, productExternalId, productName, serverId));
	}

	public int searchCount(
			Boolean active, String description, String hostName,
			String ipAddress, String licenseType, String macAddress,
			String owner, String productExternalId, String productName,
			String serverId)
		throws Exception {

		return _getCount(
			_buildSearchFilter(
				active, description, hostName, ipAddress, licenseType,
				macAddress, owner, productExternalId, productName, serverId));
	}

	public LicenseKey updateLicenseKey(
			boolean active, boolean complimentary, long licenseKeyId)
		throws Exception {

		JSONObject jsonObject = new JSONObject(
		).put(
			"active", active
		).put(
			"complimentary", complimentary
		);

		String response = patch(
			getAuthorization(), jsonObject.toString(),
			UriComponentsBuilder.fromPath(
				_PATH + "/{id}"
			).buildAndExpand(
				licenseKeyId
			).toUri());

		return new LicenseKey(new JSONObject(response));
	}

	public LicenseKey updateLicenseKeyActive(boolean active, long licenseKeyId)
		throws Exception {

		JSONObject jsonObject = new JSONObject(
		).put(
			"active", active
		);

		String response = patch(
			getAuthorization(), jsonObject.toString(),
			UriComponentsBuilder.fromPath(
				_PATH + "/{id}"
			).buildAndExpand(
				licenseKeyId
			).toUri());

		return new LicenseKey(new JSONObject(response));
	}

	protected String getFreeTierProductVersion() {
		String productGroupVersion = null;

		try {
			productGroupVersion = getLatestSupportedProductGroupVersion();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to determine the latest product version",
					exception);
			}
		}

		if (Validator.isNull(productGroupVersion)) {
			return _FREE_TIER_PRODUCT_VERSION;
		}

		return productGroupVersion;
	}

	protected String getLatestSupportedProductGroupVersion() throws Exception {
		return _productVersionService.getLatestProductGroupVersion(
			_FREE_TIER_PRODUCT_GROUP);
	}

	private String _buildSearchFilter(
		Boolean active, String description, String hostName, String ipAddress,
		String licenseType, String macAddress, String owner,
		String productExternalId, String productName, String serverId) {

		List<String> conditions = new ArrayList<>();

		if (active != null) {
			conditions.add(eq("active", active));
		}

		if (Validator.isNotNull(description)) {
			conditions.add(eq("description", description));
		}

		if (Validator.isNotNull(hostName)) {
			conditions.add(eq("hostName", hostName));
		}

		if (Validator.isNotNull(ipAddress)) {
			conditions.add(eq("ipAddresses", ipAddress));
		}

		if (Validator.isNotNull(licenseType)) {
			conditions.add(eq("licenseType", licenseType));
		}

		if (Validator.isNotNull(macAddress)) {
			conditions.add(eq("macAddresses", macAddress));
		}

		if (Validator.isNotNull(owner)) {
			conditions.add(eq("owner", owner));
		}

		if (Validator.isNotNull(productExternalId)) {
			conditions.add(eq("productExternalId", productExternalId));
		}

		if (Validator.isNotNull(productName)) {
			conditions.add(eq("productName", productName));
		}

		if (Validator.isNotNull(serverId)) {
			conditions.add(eq("serverId", serverId));
		}

		return and(conditions.toArray(new String[0]));
	}

	private LicenseKey _copyLicenseKey(
			Date expirationDate, LicenseKey licenseKey, Date startDate)
		throws Exception {

		return addLicenseKey(
			licenseKey.getAccountName(), true, licenseKey.getAdditionalInfo(),
			licenseKey.isComplimentary(), licenseKey.getDescription(),
			licenseKey.getDomains(), expirationDate, licenseKey.getHostName(),
			licenseKey.getIpAddresses(), licenseKey.getLicenseName(),
			licenseKey.getLicenseType(), licenseKey.getLicenseVersion(),
			licenseKey.getMacAddresses(), licenseKey.getMaxClusterNodes(),
			licenseKey.getMaxConcurrentUsers(), licenseKey.getMaxHttpSessions(),
			licenseKey.getMaxServers(), licenseKey.getMaxUsers(),
			licenseKey.getName(), licenseKey.getOrderId(),
			licenseKey.getOwner(), licenseKey.getProductExternalId(),
			licenseKey.getProductName(), licenseKey.getProductVersion(),
			licenseKey.getServerId(), licenseKey.getSizing(), startDate);
	}

	private int _getCount(String filterString) throws Exception {
		UriComponentsBuilder uriComponentsBuilder =
			UriComponentsBuilder.fromPath(
				_PATH
			).queryParam(
				"pageSize", 1
			);

		if (filterString != null) {
			uriComponentsBuilder.queryParam("filter", filterString);
		}

		String response = get(
			getAuthorization(),
			uriComponentsBuilder.build(
			).toUri());

		JSONObject jsonObject = new JSONObject(response);

		return jsonObject.optInt("totalCount");
	}

	private String _toISO8601(Date date) {
		if (date == null) {
			return null;
		}

		DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		return dateFormat.format(date);
	}

	private static final int _FREE_TIER_DURATION_MONTHS = 12;

	private static final String _FREE_TIER_LICENSE_NAME =
		"Liferay DXP - Free Tier";

	private static final int _FREE_TIER_LICENSE_VERSION = 3;

	private static final int _FREE_TIER_MAX_CLUSTER_NODES = 1;

	private static final String _FREE_TIER_PRODUCT_ERC = "PRDCT-DXP";

	private static final String _FREE_TIER_PRODUCT_GROUP = "dxp";

	private static final String _FREE_TIER_PRODUCT_NAME =
		"Liferay DXP - Free Tier";

	private static final String _FREE_TIER_PRODUCT_VERSION = "7.4";

	private static final int _FREE_TIER_RENEWAL_THRESHOLD_DAYS = 90;

	private static final String _PATH = "/o/c/licensekeys";

	private static final Log _log = LogFactory.getLog(LicenseKeyService.class);

	@Autowired
	private LicenseKeyExporter _licenseKeyExporter;

	@Autowired
	private LicenseKeyGenerator _licenseKeyGenerator;

	@Autowired
	private LicenseKeyValidator _licenseKeyValidator;

	@Autowired
	@Lazy
	private ProductVersionService _productVersionService;

	@Autowired
	@Lazy
	private SubscriptionEntryService _subscriptionEntryService;

}