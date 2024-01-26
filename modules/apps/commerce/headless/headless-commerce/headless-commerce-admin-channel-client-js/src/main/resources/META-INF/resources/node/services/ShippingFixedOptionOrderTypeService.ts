/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ShippingFixedOptionOrderType } from '../models/ShippingFixedOptionOrderType';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ShippingFixedOptionOrderTypeService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes a Shipping Fixed Option Order Type by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteShippingFixedOptionOrderTypes({
        shippingFixedOptionOrderTypeId,
    }: {
        shippingFixedOptionOrderTypeId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/shipping-fixed-option-order-types/{shippingFixedOptionOrderTypeId}',
            path: {
                'shippingFixedOptionOrderTypeId': shippingFixedOptionOrderTypeId,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets a list of Shipping Fixed Option Order Types.
     * @returns ShippingFixedOptionOrderType Successful operation
     * @throws ApiError
     */
    public getShippingFixedOptionsShippingFixedOptionOrderTypes({
        id,
        filter,
        page,
        pageSize,
        search,
        sort,
    }: {
        id: number,
        filter?: string,
        page?: number,
        pageSize?: number,
        search?: string,
        sort?: string,
    }): CancelablePromise<Array<ShippingFixedOptionOrderType>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/shipping-fixed-options/{id}/shipping-fixed-option-order-types',
            path: {
                'id': id,
            },
            query: {
                'filter': filter,
                'page': page,
                'pageSize': pageSize,
                'search': search,
                'sort': sort,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Creates or updates a Shipping Fixed Option Order Type.
     * @returns ShippingFixedOptionOrderType Created
     * @returns any Async
     * @throws ApiError
     */
    public postShippingFixedOptionsShippingFixedOptionOrderTypes({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: ShippingFixedOptionOrderType,
    }): CancelablePromise<ShippingFixedOptionOrderType | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/shipping-fixed-options/{id}/shipping-fixed-option-order-types',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Invalid input`,
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
}
