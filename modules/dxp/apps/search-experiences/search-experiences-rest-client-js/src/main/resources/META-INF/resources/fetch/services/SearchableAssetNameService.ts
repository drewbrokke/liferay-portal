/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { SearchableAssetName } from '../models/SearchableAssetName';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class SearchableAssetNameService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns SearchableAssetName
     * @throws ApiError
     */
    public getSearchableAssetNamesPage(): CancelablePromise<Array<SearchableAssetName>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/searchable-asset-names',
        });
    }
}
