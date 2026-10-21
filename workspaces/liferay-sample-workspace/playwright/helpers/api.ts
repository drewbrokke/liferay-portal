/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {APIRequestContext, APIResponse, request} from '@playwright/test';

import {apiEmailAddress, apiPassword, portalURL} from './environment';

export interface Credentials {
	emailAddress: string;
	password: string;
}

/**
 * Calls the headless APIs with basic authentication, so test setup never
 * depends on the browser session. By default it authenticates as the API user
 * that global setup creates, not as the user the browser signs in as, because
 * a basic authentication request signs that user's browser session out on some
 * Liferay versions.
 */
export class LiferayAPI {
	static async create(
		credentials: Credentials = {
			emailAddress: apiEmailAddress,
			password: apiPassword,
		}
	): Promise<LiferayAPI> {
		return new LiferayAPI(
			await request.newContext({
				baseURL: portalURL,
				httpCredentials: {
					password: credentials.password,
					send: 'always',
					username: credentials.emailAddress,
				},
			})
		);
	}

	async delete(path: string): Promise<void> {
		await this._check(
			'DELETE',
			path,
			await this.requestContext.delete(path)
		);
	}

	async dispose(): Promise<void> {
		await this.requestContext.dispose();
	}

	async get<T = any>(path: string): Promise<T> {
		return this._json('GET', path, await this.requestContext.get(path));
	}

	async patch<T = any>(path: string, data: unknown): Promise<T> {
		return this._json(
			'PATCH',
			path,
			await this.requestContext.patch(path, {data})
		);
	}

	async post<T = any>(path: string, data: unknown): Promise<T> {
		return this._json(
			'POST',
			path,
			await this.requestContext.post(path, {data})
		);
	}

	async put<T = any>(path: string, data: unknown): Promise<T> {
		return this._json(
			'PUT',
			path,
			await this.requestContext.put(path, {data})
		);
	}

	readonly requestContext: APIRequestContext;

	private constructor(requestContext: APIRequestContext) {
		this.requestContext = requestContext;
	}

	private async _check(method: string, path: string, response: APIResponse) {
		if (!response.ok()) {
			throw new Error(
				`${method} ${path} returned ${response.status()}: ${await response.text()}`
			);
		}
	}

	private async _json(method: string, path: string, response: APIResponse) {
		await this._check(method, path, response);

		const text = await response.text();

		return text ? JSON.parse(text) : undefined;
	}
}
