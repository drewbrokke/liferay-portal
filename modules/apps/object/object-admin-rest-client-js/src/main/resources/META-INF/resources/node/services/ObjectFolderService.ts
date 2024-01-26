/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ObjectFolder } from '../models/ObjectFolder';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ObjectFolderService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public getObjectFoldersPage({
        page,
        pageSize,
        search,
        acceptLanguage,
    }: {
        page?: number,
        pageSize?: number,
        search?: string,
        acceptLanguage?: string,
    }): CancelablePromise<Array<ObjectFolder>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/object-folders',
            headers: {
                'Accept-Language': acceptLanguage,
            },
            query: {
                'page': page,
                'pageSize': pageSize,
                'search': search,
            },
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public postObjectFolder({
        requestBody,
    }: {
        requestBody?: ObjectFolder,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/object-folders',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public getObjectFolderByExternalReferenceCode({
        externalReferenceCode,
    }: {
        externalReferenceCode: string,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/object-folders/by-external-reference-code/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public putObjectFolderByExternalReferenceCode({
        externalReferenceCode,
        requestBody,
    }: {
        externalReferenceCode: string,
        requestBody?: ObjectFolder,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/object-folders/by-external-reference-code/{externalReferenceCode}',
            path: {
                'externalReferenceCode': externalReferenceCode,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public deleteObjectFolder({
        objectFolderId,
    }: {
        objectFolderId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/object-folders/{objectFolderId}',
            path: {
                'objectFolderId': objectFolderId,
            },
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public getObjectFolder({
        objectFolderId,
    }: {
        objectFolderId: number,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/object-folders/{objectFolderId}',
            path: {
                'objectFolderId': objectFolderId,
            },
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public patchObjectFolder({
        objectFolderId,
        requestBody,
    }: {
        objectFolderId: number,
        requestBody?: ObjectFolder,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/object-folders/{objectFolderId}',
            path: {
                'objectFolderId': objectFolderId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ObjectFolder
     * @throws ApiError
     */
    public putObjectFolder({
        objectFolderId,
        requestBody,
    }: {
        objectFolderId: number,
        requestBody?: ObjectFolder,
    }): CancelablePromise<ObjectFolder> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/object-folders/{objectFolderId}',
            path: {
                'objectFolderId': objectFolderId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
