/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AccountChannelEntry } from '../models/AccountChannelEntry';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class AccountChannelEntryService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes account channel billing address.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelBillingAddresses({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-billing-addresses/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel billing address.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelBillingAddresses({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-billing-addresses/{id}',
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
     * Updates an account channel billing address.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelBillingAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-billing-addresses/{id}',
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
     * Deletes account channel currency.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelCurrencies({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-currencies/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel currency.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelCurrencies({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-currencies/{id}',
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
     * Updates an account channel currency.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelCurrencies({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-currencies/{id}',
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
     * Deletes account channel delivery term.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelDeliveryTerms({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-delivery-terms/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel delivery term.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelDeliveryTerms({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-delivery-terms/{id}',
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
     * Updates an account channel delivery term.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelDeliveryTerms({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-delivery-terms/{id}',
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
     * Deletes account channel discount.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelDiscounts({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-discounts/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel discount.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelDiscounts({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-discounts/{id}',
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
     * Updates an account channel discount.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelDiscounts({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-discounts/{id}',
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
     * Deletes account channel payment method.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelPaymentMethods({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-payment-methods/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel payment method.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelPaymentMethods({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-payment-methods/{id}',
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
     * Updates an account channel payment method.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelPaymentMethods({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-payment-methods/{id}',
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
     * Deletes account channel payment term.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelPaymentTerms({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-payment-terms/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel payment term.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelPaymentTerms({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-payment-terms/{id}',
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
     * Updates an account channel payment term.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelPaymentTerms({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-payment-terms/{id}',
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
     * Deletes account channel price list.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelPriceList({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-price-list/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel price list.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelPriceList({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-price-list/{id}',
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
     * Updates an account channel price list.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelPriceList({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-price-list/{id}',
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
     * Deletes account channel shipping address.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelShippingAddresses({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-shipping-addresses/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel shipping address.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelShippingAddresses({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-shipping-addresses/{id}',
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
     * Updates an account channel shipping address.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelShippingAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-shipping-addresses/{id}',
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
     * Deletes account channel user.
     * @returns void
     * @throws ApiError
     */
    public deleteAccountChannelUsers({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/account-channel-users/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an account channel user.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountChannelUsers({
        id,
    }: {
        id: number,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-channel-users/{id}',
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
     * Updates an account channel account user.
     * @returns AccountChannelEntry Updated
     * @throws ApiError
     */
    public patchAccountChannelUsers({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/account-channel-users/{id}',
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
     * Gets a list of channel billing addresses.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelBillingAddresses({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-billing-addresses',
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
     * Creates an account channel billing address.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelBillingAddresses({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-billing-addresses',
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
     * Gets a list of channel currencies.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelCurrencies({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-currencies',
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
     * Creates an account channel currency.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelCurrencies({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-currencies',
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
     * Gets a list of channel delivery terms.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelDeliveryTerms({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-delivery-terms',
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
     * Creates an account channel delivery term.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelDeliveryTerms({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-delivery-terms',
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
     * Gets a list of channel discounts.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelDiscounts({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-discounts',
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
     * Creates an account channel discount.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelDiscounts({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-discounts',
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
     * Gets a list of channel payment methods.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelPaymentMethods({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-payment-methods',
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
     * Creates an account channel payment method.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelPaymentMethods({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-payment-methods',
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
     * Gets a list of channel payment terms.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelPaymentTerms({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-payment-terms',
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
     * Creates an account channel payment term.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelPaymentTerms({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-payment-terms',
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
     * Gets a list of channel price lists.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelPriceLists({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-price-lists',
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
     * Creates an account channel price list.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelPriceLists({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-price-lists',
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
     * Gets a list of channel shipping addresses.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelShippingAddresses({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-shipping-addresses',
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
     * Creates an account channel shipping address.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelShippingAddresses({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-shipping-addresses',
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
     * Gets a list of channel users.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsByExternalReferenceCodeAccountChannelUsers({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-users',
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
     * Creates an account channel user.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsByExternalReferenceCodeAccountChannelUsers({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/by-externalReferenceCode/{externalReferenceCode}/account-channel-users',
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
     * Gets a list of channel billing addresses.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelBillingAddresses({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-billing-addresses',
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
     * Creates an account channel billing address.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelBillingAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-billing-addresses',
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
     * Gets a list of channel currencies.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelCurrencies({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-currencies',
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
     * Creates an account channel currency.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelCurrencies({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-currencies',
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
     * Gets a list of channel delivery terms.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelDeliveryTerms({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-delivery-terms',
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
     * Creates an account channel delivery term.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelDeliveryTerms({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-delivery-terms',
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
     * Gets a list of channel discounts.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelDiscounts({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-discounts',
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
     * Creates an account channel discount.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelDiscounts({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-discounts',
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
     * Gets a list of channel payment methods.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelPaymentMethods({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-payment-methods',
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
     * Creates an account channel payment method.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelPaymentMethods({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-payment-methods',
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
     * Gets a list of channel payment terms.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelPaymentTerms({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-payment-terms',
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
     * Creates an account channel payment term.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelPaymentTerms({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-payment-terms',
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
     * Gets a list of channel price lists.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelPriceLists({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-price-lists',
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
     * Creates an account channel price list.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelPriceLists({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-price-lists',
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
     * Gets a list of channel shipping addresses.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelShippingAddresses({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-shipping-addresses',
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
     * Creates an account channel shipping address.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelShippingAddresses({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-shipping-addresses',
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
     * Gets a list of channel users.
     * @returns AccountChannelEntry Successful operation
     * @throws ApiError
     */
    public getAccountsAccountChannelUsers({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<AccountChannelEntry>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/accounts/{id}/account-channel-users',
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
     * Creates an account channel user.
     * @returns AccountChannelEntry Created
     * @returns any Async
     * @throws ApiError
     */
    public postAccountsAccountChannelUsers({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AccountChannelEntry,
    }): CancelablePromise<AccountChannelEntry | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/accounts/{id}/account-channel-users',
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
