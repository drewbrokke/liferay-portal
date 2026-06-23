/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.model.listener;

import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.type.internal.manager.CETManagerImpl;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = ModelListener.class)
public class ClientExtensionEntryModelListener
	extends BaseModelListener<ClientExtensionEntry> {

	@Override
	public void onAfterRemove(ClientExtensionEntry clientExtensionEntry) {
		_portalCache.remove(clientExtensionEntry.getClientExtensionEntryId());
	}

	private final PortalCache<Long, Object> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, CETManagerImpl.class.getName());

}