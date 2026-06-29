/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.client.JiraAssetsClient;
import com.liferay.one.jira.constants.BusinessEventConstants;
import com.liferay.one.jira.converter.BusinessEventConverter;
import com.liferay.one.jira.converter.BusinessEventVersionConverter;
import com.liferay.one.jira.model.AssetObject;
import com.liferay.one.jira.model.AssetObjectFieldOption;
import com.liferay.one.jira.model.BusinessEvent;
import com.liferay.one.jira.model.BusinessEventVersion;
import com.liferay.one.jira.util.AQLUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Application logic for business events: orchestrates schema resolution
 * ({@link AssetSchemaService}), mapping ({@link BusinessEventConverter} /
 * {@link BusinessEventVersionConverter}), and transport
 * ({@link JiraAssetsClient}). It holds no HTTP, no JSON wire mechanics, and no
 * attribute ids.
 *
 * @author Jenny Chen
 */
@Component
public class BusinessEventService {

	public void createBusinessEvent(BusinessEvent businessEvent)
		throws Exception {

		String accountObjectKey = _getAccountObjectKey(
			businessEvent.getAccountExternalReferenceCode());

		_jiraAssetsClient.createObject(
			_objectTypeId(),
			_businessEventConverter.toAssetObject(
				accountObjectKey, businessEvent,
				_getBusinessEventAttributeIds()));
	}

	public void deleteBusinessEvent(String id) throws Exception {
		_jiraAssetsClient.deleteObject(id);
	}

	public BusinessEvent getBusinessEvent(String id) throws Exception {
		return _businessEventConverter.toBusinessEvent(
			StringPool.BLANK, _jiraAssetsClient.getObject(id),
			_getBusinessEventAttributeIds());
	}

	public List<BusinessEvent> getBusinessEvents(
			String accountExternalReferenceCode)
		throws Exception {

		List<BusinessEvent> businessEvents = new ArrayList<>();

		String aql = AQLUtil.builder(
			AQLUtil.getBaseAQL(
				BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
				BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT)
		).andEquals(
			accountExternalReferenceCode,
			BusinessEventConstants.ATTRIBUTE_NAME_ACCOUNT, "External Key"
		).build();

		Map<String, String> attributeIds = _getBusinessEventAttributeIds();

		JSONArray assetObjectsJSONArray = _jiraAssetsClient.searchObjects(aql);

		for (int i = 0; i < assetObjectsJSONArray.length(); i++) {
			businessEvents.add(
				_businessEventConverter.toBusinessEvent(
					accountExternalReferenceCode,
					assetObjectsJSONArray.getJSONObject(i), attributeIds));
		}

		return businessEvents;
	}

	public List<BusinessEventVersion> getBusinessEventVersions(
			String businessEventId)
		throws Exception {

		List<BusinessEventVersion> businessEventVersions = new ArrayList<>();

		if (!Validator.isNumber(businessEventId)) {
			return businessEventVersions;
		}

		String aql = AQLUtil.builder(
			AQLUtil.getBaseAQL(
				BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
				BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT_VERSION)
		).andEqualsObject(
			businessEventId, BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT
		).orderByDescending(
			"Updated"
		).build();

		Map<String, String> attributeIds =
			_getBusinessEventVersionAttributeIds();

		JSONArray assetObjectsJSONArray = _jiraAssetsClient.searchObjects(aql);

		for (int i = 0; i < assetObjectsJSONArray.length(); i++) {
			businessEventVersions.add(
				_businessEventVersionConverter.toBusinessEventVersion(
					assetObjectsJSONArray.getJSONObject(i), attributeIds));
		}

		return businessEventVersions;
	}

	@Cacheable("assetObjectFieldOptions")
	public List<AssetObjectFieldOption> getFieldOptions(String fieldName)
		throws Exception {

		List<AssetObjectFieldOption> assetObjectFieldOptions =
			new ArrayList<>();

		JSONArray objectTypeAttributesJSONArray =
			_jiraAssetsClient.getObjectTypeAttributes(_objectTypeId());

		for (int i = 0; i < objectTypeAttributesJSONArray.length(); i++) {
			JSONObject objectTypeAttributeJSONObject =
				objectTypeAttributesJSONArray.getJSONObject(i);

			if (!fieldName.equals(
					objectTypeAttributeJSONObject.optString("name"))) {

				continue;
			}

			String options = objectTypeAttributeJSONObject.optString("options");

			if (Validator.isNotNull(options)) {
				for (String option : options.split(",")) {
					assetObjectFieldOptions.add(
						new AssetObjectFieldOption(option, option));
				}
			}

			break;
		}

		return assetObjectFieldOptions;
	}

	@Cacheable("assetObjects")
	public List<AssetObject> getProductVersions() throws Exception {
		String aql = AQLUtil.getBaseAQL(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_PRODUCT_VERSION);

		List<AssetObject> assetObjects = new ArrayList<>();

		JSONArray assetObjectsJSONArray = _jiraAssetsClient.searchObjects(aql);

		for (int i = 0; i < assetObjectsJSONArray.length(); i++) {
			assetObjects.add(
				new AssetObject(assetObjectsJSONArray.getJSONObject(i)));
		}

		return assetObjects;
	}

	@CacheEvict(
		allEntries = true, value = {"assetObjectFieldOptions", "assetObjects"}
	)
	@Scheduled(cron = "0 0 0 * * *")
	public void scheduledAssetObjectsCacheEviction() throws Exception {
	}

	public BusinessEvent updateBusinessEvent(
			BusinessEvent businessEvent, String id)
		throws Exception {

		_jiraAssetsClient.updateObject(
			id,
			_businessEventConverter.toAssetObject(
				null, businessEvent, _getBusinessEventAttributeIds()));

		return getBusinessEvent(id);
	}

	private String _getAccountObjectKey(String accountExternalReferenceCode)
		throws Exception {

		String aql = AQLUtil.builder(
			AQLUtil.getBaseAQL(_accountSchemaName, "Account")
		).andEquals(
			accountExternalReferenceCode, "External Key"
		).build();

		JSONArray accountsJSONArray = _jiraAssetsClient.searchObjects(aql);

		if (accountsJSONArray.isEmpty()) {
			return null;
		}

		JSONObject accountJSONObject = accountsJSONArray.getJSONObject(0);

		return accountJSONObject.optString("objectKey");
	}

	private Map<String, String> _getBusinessEventAttributeIds() {
		return _assetSchemaService.getAttributeIds(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT);
	}

	private Map<String, String> _getBusinessEventVersionAttributeIds() {
		return _assetSchemaService.getAttributeIds(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT_VERSION);
	}

	private String _objectTypeId() {
		return _assetSchemaService.getObjectTypeId(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT);
	}

	@Value("${liferay.one.jira.account.schema:Koroneiki}")
	private String _accountSchemaName;

	@Autowired
	private AssetSchemaService _assetSchemaService;

	@Autowired
	private BusinessEventConverter _businessEventConverter;

	@Autowired
	private BusinessEventVersionConverter _businessEventVersionConverter;

	@Autowired
	private JiraAssetsClient _jiraAssetsClient;

}