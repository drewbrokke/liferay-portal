/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ContactConfiguration } from '../models/ContactConfiguration';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class ContactConfigurationService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns ContactConfiguration
     * @throws ApiError
     */
    public getContactConfiguration(): CancelablePromise<ContactConfiguration> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/contacts/configuration',
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public putContactConfiguration({
        requestBody,
    }: {
        requestBody?: ContactConfiguration,
    }): CancelablePromise<void> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/contacts/configuration',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
