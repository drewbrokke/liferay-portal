/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { OrderNote } from '../models/OrderNote';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class OrderNoteService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Deletes an Order Note by external reference code.
     * @returns void
     * @throws ApiError
     */
    public deleteOrderNoteByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/orderNotes/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an Order Note by external reference code.
     * @returns OrderNote Successful operation
     * @throws ApiError
     */
    public getOrderNoteByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<OrderNote> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/orderNotes/by-externalReferenceCode/{externalReferenceCode}',
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
     * Updates an Order Note by external reference code.
     * @returns any Async
     * @throws ApiError
     */
    public patchOrderNoteByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: OrderNote,
    }): CancelablePromise<any> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/orderNotes/by-externalReferenceCode/{externalReferenceCode}',
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
     * Deletes an Order Note by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteOrderNote({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/orderNotes/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Gets an Order Note by ID.
     * @returns OrderNote Successful operation
     * @throws ApiError
     */
    public getOrderNote({
        id,
    }: {
        id: number,
    }): CancelablePromise<OrderNote> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/orderNotes/{id}',
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
     * Updates an Order Note by ID.
     * @returns any Async
     * @throws ApiError
     */
    public patchOrderNote({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: OrderNote,
    }): CancelablePromise<any> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/orderNotes/{id}',
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
     * Gets a list of Order Notes related to an Order.
     * @returns OrderNote Successful operation
     * @throws ApiError
     */
    public getOrderByExternalReferenceCodeOrderNotesPage({
        externalReferenceCode,
        page,
        pageSize,
    }: {
        externalReferenceCode: string,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<OrderNote>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/orders/by-externalReferenceCode/{externalReferenceCode}/orderNotes',
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
     * Creates or updates an Order Note.
     * @returns OrderNote Created
     * @returns any Async
     * @throws ApiError
     */
    public postOrderByExternalReferenceCodeOrderNote({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: OrderNote,
    }): CancelablePromise<OrderNote | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/orders/by-externalReferenceCode/{externalReferenceCode}/orderNotes',
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
     * Gets a list of Order Notes related to an Order.
     * @returns OrderNote Successful operation
     * @throws ApiError
     */
    public getOrderIdOrderNotesPage({
        id,
        page,
        pageSize,
    }: {
        id: number,
        page?: number,
        pageSize?: number,
    }): CancelablePromise<Array<OrderNote>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/orders/{id}/orderNotes',
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
     * Creates or updates an Order Note.
     * @returns OrderNote Created
     * @returns any Async
     * @throws ApiError
     */
    public postOrderIdOrderNote({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: OrderNote,
    }): CancelablePromise<OrderNote | any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/orders/{id}/orderNotes',
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
