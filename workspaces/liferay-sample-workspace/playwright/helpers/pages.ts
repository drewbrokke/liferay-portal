/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LiferayAPI} from './api';
import {ClientExtension, getWidgetName} from './clientExtensions';

export interface Site {
	friendlyUrlPath: string;
	id: number;
}

export interface PageSettings {
	favIcon?: ClientExtension;
	globalCSSClientExtensions?: ClientExtension[];
	globalJSClientExtensions?: ClientExtension[];
	themeCSSClientExtension?: ClientExtension;
	themeSpritemapClientExtension?: ClientExtension;
}

/**
 * Creates a published content page with the given client extensions placed
 * as widgets and applied through the page settings, and returns its URL.
 */
export async function addContentPage(
	api: LiferayAPI,
	site: Site,
	{
		companyId,
		settings = {},
		widgets = [],
	}: {
		companyId?: string;
		settings?: PageSettings;
		widgets?: ClientExtension[];
	}
): Promise<string> {
	const sitePage = await api.post(
		`/o/headless-delivery/v1.0/sites/${site.id}/site-pages`,
		{
			pageDefinition: {
				pageElement: {
					pageElements: widgets.map((widget, index) => ({
						definition: {
							widgetInstance: {
								widgetName: getWidgetName(widget, companyId!),
							},
						},
						id: `widget-${index}`,
						type: 'Widget',
					})),
					type: 'Root',
				},
				settings: toSettings(settings),
			},
			title: `Page ${Date.now()}`,
		}
	);

	return `/web${site.friendlyUrlPath}${sitePage.friendlyUrlPath}`;
}

function toClientExtensionReference(clientExtension: ClientExtension) {
	return {externalReferenceCode: clientExtension.externalReferenceCode};
}

function toSettings(settings: PageSettings) {
	const pageSettings: Record<string, unknown> = {};

	if (settings.favIcon) {
		pageSettings.favIcon = toClientExtensionReference(settings.favIcon);
	}

	if (settings.globalCSSClientExtensions) {
		pageSettings.globalCSSClientExtensions =
			settings.globalCSSClientExtensions.map(toClientExtensionReference);
	}

	if (settings.globalJSClientExtensions) {

		// A page level global JS script only renders at the location stored
		// on the page, which the UI always sets and the API leaves blank

		pageSettings.globalJSClientExtensions =
			settings.globalJSClientExtensions.map((clientExtension) => ({
				...toClientExtensionReference(clientExtension),
				clientExtensionConfig: {scriptLocation: 'bottom'},
			}));
	}

	if (settings.themeCSSClientExtension) {
		pageSettings.themeCSSClientExtension = toClientExtensionReference(
			settings.themeCSSClientExtension
		);
	}

	if (settings.themeSpritemapClientExtension) {
		pageSettings.themeSpritemapClientExtension = toClientExtensionReference(
			settings.themeSpritemapClientExtension
		);
	}

	return pageSettings;
}
