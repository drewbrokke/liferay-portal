/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Process } from '../models/Process';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ProcessService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns Process
     * @throws ApiError
     */
    public postProcess({
        requestBody,
    }: {
        requestBody?: Process,
    }): CancelablePromise<Process> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/processes',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public deleteProcess({
        processId,
    }: {
        processId: number,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/processes/{processId}',
            path: {
                'processId': processId,
            },
        });
    }
    /**
     * @returns Process
     * @throws ApiError
     */
    public getProcess({
        processId,
    }: {
        processId: number,
    }): CancelablePromise<Process> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/processes/{processId}',
            path: {
                'processId': processId,
            },
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public putProcess({
        processId,
        requestBody,
    }: {
        processId: number,
        requestBody?: Process,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/processes/{processId}',
            path: {
                'processId': processId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns string
     * @throws ApiError
     */
    public getProcessTitle({
        processId,
    }: {
        processId: number,
    }): CancelablePromise<string> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/processes/{processId}/title',
            path: {
                'processId': processId,
            },
        });
    }
}
