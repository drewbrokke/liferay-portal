/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ShippingMethod } from '../models/ShippingMethod';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ShippingMethodService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Retrieves channel shipping methods.
     * @returns ShippingMethod Successful operation
     * @throws ApiError
     */
    public getChannelsShippingMethods({
        channelId,
        page,
        pageSize,
    }: {
        channelId: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<ShippingMethod>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/channels/{channelId}/shipping-methods',
            path: {
                'channelId': channelId,
            },
            query: {
                'page': page,
                'pageSize': pageSize,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
}
