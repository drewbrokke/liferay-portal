/* eslint-disable no-console */
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as child_process from 'node:child_process';
import * as fs from 'node:fs';
import * as fsPromises from 'node:fs/promises';
import * as path from 'node:path';
import * as util from 'node:util';

export const execFilePromise = util.promisify(child_process.execFile);

const rootDir = path.resolve('..');

const bundleDir = path.join(rootDir, 'bundles');
const gradlew = path.join(rootDir, 'gradlew');
const logsDir = path.join(bundleDir, 'logs');

const logFiles = fs
	.readdirSync(logsDir)
	.filter((f) => f.endsWith('.log'))
	.map((f) => {
		return {
			date: Number(f.split('.')[1].split('-').join('')),
			name: f,
		};
	});

logFiles.sort((a, b) => b.date - a.date);

const logFile = path.join(logsDir, logFiles[0].name);

console.log(`LOG FILE: ${logFile}`)

async function tailForOutput(filename, targetString): Promise<void> {
	let {size: lastReadPosition} = await fsPromises.stat(filename);

	return new Promise((resolve) => {
		console.log(`Waiting for expected log output "${targetString}"`);
		const watcher = fs.watch(
			filename,
			{persistent: true},
			async (_eventType, fileName) => {
				if (!fileName) {
					return;
				}

				const {size: fileSize} = await fsPromises.stat(filename);

				if (fileSize <= lastReadPosition) {
					return; // No new content
				}

				const readStream = fs.createReadStream(filename, {
					start: lastReadPosition,
				});

				readStream.on('data', (chunk) => {
					if (chunk.includes(targetString)) {
						console.log(
							`Found expected log output "${targetString}"`
						);
						readStream.close();
						watcher.close();

						return resolve();
					}

					lastReadPosition += chunk.length; // Update last read position
				});
			}
		);
	});
}

export async function deployCX(
	directory: string,
	expectedLogEntry?: string
): Promise<void> {
	directory = path.resolve(rootDir, directory);

	let tailer: any;

	if (expectedLogEntry) {
		tailer = tailForOutput(logFile, expectedLogEntry);
	}

	console.log(`Deploying project from ${directory}`);
	await execFilePromise(gradlew, ['deploy'], {
		cwd: directory,
	});

	if (tailer) {
		await tailer;
	}
}

export async function cleanCX(): Promise<{
	stderr: string;
	stdout: string;
}> {
	return execFilePromise('./gradlew', ['cleanDeploy'], {
		cwd: rootDir,
	});
}
