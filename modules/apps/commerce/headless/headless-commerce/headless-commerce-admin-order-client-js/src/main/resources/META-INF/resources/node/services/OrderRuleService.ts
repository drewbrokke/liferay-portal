/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { OrderRule } from '../models/OrderRule';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class OrderRuleService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Gets a list of Order Rules.
     * @returns OrderRule Successful operation
     * @throws ApiError
     */
    public getOrderRulesPage({
        filter,
        page,
        pageSize,
        search,
        sort,
    }: {
        filter?: string,
        page?: number,
        pageSize?: number,
        search?: string,
        sort?: string,
    }): CancelablePromise<Array<OrderRule>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/order-rules',
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
     * Creates or updates a Order Rule.
     * @returns OrderRule Created
     * @returns any Async
     * @throws ApiError
     */
    public postOrderRule({
        requestBody,
    }: {
        requestBody: OrderRule,
    }): CancelablePromise<OrderRule | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/order-rules',
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
    /**
     * Deletes a Order Rule by external reference code.
     * @returns void
     * @throws ApiError
     */
    public deleteOrderRuleByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/order-rules/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets a Order Rule by external reference code.
     * @returns OrderRule Successful operation
     * @throws ApiError
     */
    public getOrderRuleByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<OrderRule> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/order-rules/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                400: `Invalid input`,
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Updates a Order Rule by external reference code.
     * @returns OrderRule Succesfully update the order Rule
     * @throws ApiError
     */
    public patchOrderRuleByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: OrderRule,
    }): CancelablePromise<OrderRule> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/order-rules/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
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
    /**
     * Deletes an OrderRule by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteOrderRules({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/order-rules/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an OrderRule by ID.
     * @returns OrderRule Successful operation
     * @throws ApiError
     */
    public getOrderRules({
        id,
    }: {
        id: number,
    }): CancelablePromise<OrderRule> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/order-rules/{id}',
            path: {
                'id': id,
            },
            errors: {
                400: `Invalid input`,
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Updates an OrderRule by ID.
     * @returns OrderRule Succesfully update the Order Rule
     * @throws ApiError
     */
    public patchOrderRules({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: OrderRule,
    }): CancelablePromise<OrderRule> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/order-rules/{id}',
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
