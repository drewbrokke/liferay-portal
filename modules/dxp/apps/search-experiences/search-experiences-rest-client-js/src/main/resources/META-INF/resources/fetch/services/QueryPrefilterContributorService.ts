/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { QueryPrefilterContributor } from '../models/QueryPrefilterContributor';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class QueryPrefilterContributorService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns QueryPrefilterContributor
     * @throws ApiError
     */
    public getQueryPrefilterContributorsPage(): CancelablePromise<Array<QueryPrefilterContributor>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/query-prefilter-contributors',
        });
    }
}
