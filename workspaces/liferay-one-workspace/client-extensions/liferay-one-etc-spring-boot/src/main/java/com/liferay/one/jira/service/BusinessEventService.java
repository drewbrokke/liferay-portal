/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.jira.service;

import com.liferay.one.jira.client.JiraAccountReference;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Application logic for business events: orchestrates schema resolution
 * ({@link AssetSchemaService}), mapping ({@link BusinessEventConverter} /
 * {@link BusinessEventVersionConverter}), reference resolution
 * ({@link JiraAccountReference}), and transport ({@link JiraAssetsClient}). It
 * holds no HTTP, no JSON wire mechanics, and no attribute ids.
 *
 * @author Jenny Chen
 */
@Component
public class BusinessEventService {

	public void createBusinessEvent(BusinessEvent businessEvent)
		throws Exception {

		String accountObjectKey = _jiraAccountReference.getObjectKey(
			businessEvent.getAccountExternalReferenceCode());

		_jiraAssetsClient.createObject(
			_objectTypeId(),
			_businessEventConverter.toAssetObject(
				accountObjectKey, businessEvent, _attributeIds()));
	}

	public void deleteBusinessEvent(String id) throws Exception {
		_jiraAssetsClient.deleteObject(id);
	}

	public BusinessEvent getBusinessEvent(String id) throws Exception {
		return _businessEventConverter.toBusinessEvent(
			StringPool.BLANK, _jiraAssetsClient.getObject(id), _attributeIds());
	}

	public List<BusinessEvent> getBusinessEvents(
			String accountExternalReferenceCode)
		throws Exception {

		String aql = AQLUtil.builder(
			AQLUtil.getBaseAQL(
				BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
				BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT)
		).andEquals(
			accountExternalReferenceCode,
			BusinessEventConstants.ATTRIBUTE_NAME_ACCOUNT, "External Key"
		).build();

		Map<String, String> attributeIds = _attributeIds();

		List<BusinessEvent> businessEvents = new ArrayList<>();

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

		Map<String, String> attributeIds = _versionAttributeIds();

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
				null, businessEvent, _attributeIds()));

		return getBusinessEvent(id);
	}

	private Map<String, String> _attributeIds() {
		return _assetSchemaService.getAttributeIds(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT);
	}

	private String _objectTypeId() {
		return _assetSchemaService.getObjectTypeId(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT);
	}

	private Map<String, String> _versionAttributeIds() {
		return _assetSchemaService.getAttributeIds(
			BusinessEventConstants.OBJECT_SCHEMA_BUSINESS_EVENTS,
			BusinessEventConstants.OBJECT_TYPE_BUSINESS_EVENT_VERSION);
	}

	@Autowired
	private AssetSchemaService _assetSchemaService;

	@Autowired
	private BusinessEventConverter _businessEventConverter;

	@Autowired
	private BusinessEventVersionConverter _businessEventVersionConverter;

	@Autowired
	private JiraAccountReference _jiraAccountReference;

	@Autowired
	private JiraAssetsClient _jiraAssetsClient;

}