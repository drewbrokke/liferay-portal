/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.task;

import java.util.List;

/**
 * @author Seiphon Wang
 */
public class PnpmInstallTask extends ExecutePackageManagerTask {

	@Override
	public void executeNode() throws Exception {
		executePnpmInstall(false);
	}

	protected void executePnpmInstall(boolean reset) throws Exception {

		// do something

		super.executeNode();
	}

	@Override
	protected List<String> getCompleteArgs() {
		List<String> completeArgs = super.getCompleteArgs();

		completeArgs.add("install");

		return completeArgs;
	}

}