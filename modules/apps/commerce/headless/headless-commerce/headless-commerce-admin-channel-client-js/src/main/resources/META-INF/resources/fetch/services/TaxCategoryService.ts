/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { TaxCategory } from '../models/TaxCategory';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class TaxCategoryService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Gets a list of Tax Category.
     * @returns TaxCategory Successful operation
     * @throws ApiError
     */
    public getTaxCategories({
        page,
        pageSize,
        search,
    }: {
        page?: number,
        pageSize?: number,
        search?: string,
    }): CancelablePromise<Array<TaxCategory>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/tax-categories',
            query: {
                'page': page,
                'pageSize': pageSize,
                'search': search,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Gets a Tax Category by ID.
     * @returns TaxCategory Successful operation
     * @throws ApiError
     */
    public getTaxCategories1({
        id,
    }: {
        id: number,
    }): CancelablePromise<TaxCategory> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/tax-categories/{id}',
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
}
