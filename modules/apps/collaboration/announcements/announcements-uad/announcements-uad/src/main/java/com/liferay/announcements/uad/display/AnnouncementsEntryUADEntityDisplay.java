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

package com.liferay.announcements.uad.display;

import com.liferay.announcements.uad.constants.AnnouncementsUADConstants;
import com.liferay.user.associated.data.anonymizer.UADEntityAnonymizer;
import com.liferay.user.associated.data.display.BaseUADEntityDisplay;
import com.liferay.user.associated.data.display.UADEntityDisplay;
import com.liferay.user.associated.data.entity.UADEntity;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author William Newbury
 */
@Component(
	immediate = true,
	property = "model.class.name=" + AnnouncementsUADConstants.ANNOUNCEMENTS_ENTRY,
	service = UADEntityDisplay.class
)
public class AnnouncementsEntryUADEntityDisplay extends BaseUADEntityDisplay {

	@Override
	public String getEditURL(UADEntity uadEntity) {
		return "test Edit URL";
	}

	@Override
	public String getEntityTypeDescription() {
		return "test Announcements, see?";
	}

	@Override
	public String getEntityTypeName() {
		return " test Announcements Entry";
	}

	@Override
	public List<String> getEntityTypeNonAnonymizableFieldsList() {
		return _uadEntityAnonymizer.getEntityNonAnonymizableFields();
	}

	@Reference(
		target = "(model.class.name=" + AnnouncementsUADConstants.ANNOUNCEMENTS_ENTRY + ")"
	)
	private UADEntityAnonymizer _uadEntityAnonymizer;

}