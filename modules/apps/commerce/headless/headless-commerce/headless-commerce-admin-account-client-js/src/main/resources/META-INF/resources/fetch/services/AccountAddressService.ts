/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AccountAddress } from '../models/AccountAddress';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class AccountAddressService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes an account address by external reference code.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountAddressByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/accountAddresses/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets the addresses by external reference Code
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public getAccountAddressByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<AccountAddress> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accountAddresses/by-externalReferenceCode/{externalReferenceCode}',
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
     * Updates an account address by external reference code.
     * @returns any Created
     * @throws ApiError
     */
    public patchAccountAddressByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountAddress,
    }): CancelablePromise<any> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/accountAddresses/by-externalReferenceCode/{externalReferenceCode}',
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
     * Deletes an Account Address by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountAddress({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/accountAddresses/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets the addresses by external reference Code
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public getAccountAddresses({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountAddress> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accountAddresses/{id}',
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
     * Updates an account address by external reference code.
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public patchAccountAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountAddress,
    }): CancelablePromise<AccountAddress> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/accountAddresses/{id}',
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
    /**
     * Updates an account address by external reference code.
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public putAccountAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountAddress,
    }): CancelablePromise<AccountAddress> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/accountAddresses/{id}',
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
    /**
     * Gets a list of Addresses related to an Account.
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public getAccountByExternalReferenceCodeAccountAddressesPage({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountAddress>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/accountAddresses',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            query: {
                'page': page,
                'pageSize': pageSize,
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
     * Creates an Address.
     * @returns AccountAddress Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountByExternalReferenceCodeAccountAddress({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountAddress,
    }): CancelablePromise<AccountAddress | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/accountAddresses',
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
     * Gets a list of Addresses related to an Account.
     * @returns AccountAddress Successful operation
     * @throws ApiError
     */
    public getAccountIdAccountAddressesPage({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountAddress>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/accountAddresses',
            path: {
                'id': id,
            },
            query: {
                'page': page,
                'pageSize': pageSize,
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
     * Creates an Address.
     * @returns AccountAddress Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountIdAccountAddress({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountAddress,
    }): CancelablePromise<AccountAddress | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/accountAddresses',
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
