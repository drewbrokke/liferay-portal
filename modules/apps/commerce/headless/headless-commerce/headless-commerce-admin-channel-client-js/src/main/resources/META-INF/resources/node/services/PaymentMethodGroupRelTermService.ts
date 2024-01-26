/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PaymentMethodGroupRelTerm } from '../models/PaymentMethodGroupRelTerm';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class PaymentMethodGroupRelTermService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes a Payment Method Group Rel Term by ID.
     * @returns void
     * @throws ApiError
     */
    public deletePaymentMethodGroupRelTerms({
        paymentMethodGroupRelTermId,
    }: {
        paymentMethodGroupRelTermId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/payment-method-group-rel-terms/{paymentMethodGroupRelTermId}',
            path: {
                'paymentMethodGroupRelTermId': paymentMethodGroupRelTermId,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets a list of Payment Method Group Rel Terms.
     * @returns PaymentMethodGroupRelTerm Successful operation
     * @throws ApiError
     */
    public getPaymentMethodGroupRelsPaymentMethodGroupRelTerms({
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
    }): CancelablePromise<Array<PaymentMethodGroupRelTerm>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/payment-method-group-rels/{id}/payment-method-group-rel-terms',
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
     * Creates or updates a Payment Method Group Rel Term.
     * @returns PaymentMethodGroupRelTerm Created
     * @returns any Async
     * @throws ApiError
     */
    public postPaymentMethodGroupRelsPaymentMethodGroupRelTerms({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: PaymentMethodGroupRelTerm,
    }): CancelablePromise<PaymentMethodGroupRelTerm | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/payment-method-group-rels/{id}/payment-method-group-rel-terms',
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
