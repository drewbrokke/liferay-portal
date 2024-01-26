/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PaymentMethodGroupRelOrderType } from '../models/PaymentMethodGroupRelOrderType';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class PaymentMethodGroupRelOrderTypeService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes a Payment Method Group Rel Order Type by ID.
     * @returns void
     * @throws ApiError
     */
    public deletePaymentMethodGroupRelOrderTypes({
        paymentMethodGroupRelOrderTypeId,
    }: {
        paymentMethodGroupRelOrderTypeId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/payment-method-group-rel-order-types/{paymentMethodGroupRelOrderTypeId}',
            path: {
                'paymentMethodGroupRelOrderTypeId': paymentMethodGroupRelOrderTypeId,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets a list of Payment Method Group Rel Order Types.
     * @returns PaymentMethodGroupRelOrderType Successful operation
     * @throws ApiError
     */
    public getPaymentMethodGroupRelsPaymentMethodGroupRelOrderTypes({
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
    }): CancelablePromise<Array<PaymentMethodGroupRelOrderType>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/payment-method-group-rels/{id}/payment-method-group-rel-order-types',
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
     * Creates or updates a Payment Method Group Rel Order Type.
     * @returns PaymentMethodGroupRelOrderType Created
     * @returns any Async
     * @throws ApiError
     */
    public postPaymentMethodGroupRelsPaymentMethodGroupRelOrderTypes({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: PaymentMethodGroupRelOrderType,
    }): CancelablePromise<PaymentMethodGroupRelOrderType | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/payment-method-group-rels/{id}/payment-method-group-rel-order-types',
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
