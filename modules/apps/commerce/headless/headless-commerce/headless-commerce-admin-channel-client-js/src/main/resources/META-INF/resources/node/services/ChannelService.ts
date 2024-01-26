/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Channel } from '../models/Channel';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ChannelService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * Gets channel associated to the account address object.
     * @returns Channel Successful operation
     * @throws ApiError
     */
    public getAccountAddressChannelChannel({
        accountAddressChannelId,
    }: {
        accountAddressChannelId: number,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/account-address-channels/{accountAddressChannelId}/channel',
            path: {
                'accountAddressChannelId': accountAddressChannelId,
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
     * Retrieves channels.
     * @returns Channel Successful operation
     * @throws ApiError
     */
    public getChannels({
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
    }): CancelablePromise<Array<Channel>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/channels',
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
     * Creates a Channel.
     * @returns Channel Created
     * @throws ApiError
     */
    public postChannels({
        requestBody,
    }: {
        requestBody: Channel,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/channels',
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
     * Deletes a Channel by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteChannelsByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/channels/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Retrive information of the given Channel.
     * @returns Channel
     * @throws ApiError
     */
    public getChannelsByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/channels/by-externalReferenceCode/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Updates a Channel.
     * @returns Channel Updated
     * @throws ApiError
     */
    public patchChannelsByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: Channel,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/channels/by-externalReferenceCode/{externalReferenceCode}',
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
     * Updates a Channel.
     * @returns Channel Updated
     * @throws ApiError
     */
    public putChannelsByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody: Channel,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/channels/by-externalReferenceCode/{externalReferenceCode}',
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
     * Deletes a Channel by ID.
     * @returns void
     * @throws ApiError
     */
    public deleteChannels({
        channelId,
    }: {
        channelId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/channels/{channelId}',
            path: {
                'channelId': channelId,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
            },
        });
    }
    /**
     * Retrive information of the given Channel.
     * @returns Channel
     * @throws ApiError
     */
    public getChannels1({
        channelId,
    }: {
        channelId: number,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/channels/{channelId}',
            path: {
                'channelId': channelId,
            },
            errors: {
                401: `Authentication information is missing or invalid`,
                404: `The specified resource was not found`,
                500: `Unexpected error`,
            },
        });
    }
    /**
     * Updates a Channel.
     * @returns Channel Updated
     * @throws ApiError
     */
    public patchChannels({
        channelId,
        requestBody,
    }: {
        channelId: number,
        requestBody: Channel,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/channels/{channelId}',
            path: {
                'channelId': channelId,
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
     * Updates a Channel.
     * @returns Channel Updated
     * @throws ApiError
     */
    public putChannels({
        channelId,
        requestBody,
    }: {
        channelId: number,
        requestBody: Channel,
    }): CancelablePromise<Channel> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/channels/{channelId}',
            path: {
                'channelId': channelId,
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
