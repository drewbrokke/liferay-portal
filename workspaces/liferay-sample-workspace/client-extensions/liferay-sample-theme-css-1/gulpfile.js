/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable @liferay/no-extraneous-dependencies */

'use strict';

const gulp = require('gulp');
const liferayThemeTasks = require('liferay-theme-tasks');

const disabledTasks = [
	// 'build:web-inf',
	// 'build:liferay-look-and-feel',
	'build:war'
];

liferayThemeTasks.registerTasks({
	gulp,
	hookFn: function(gulp) {
		for (const disabledTask of disabledTasks) {
			gulp.task(disabledTask, function(done) {
				console.log(`Skipping task: ${disabledTask}`);

				done();
			});
		}

	},
	pathBuild: "./build/buildTheme"
});
