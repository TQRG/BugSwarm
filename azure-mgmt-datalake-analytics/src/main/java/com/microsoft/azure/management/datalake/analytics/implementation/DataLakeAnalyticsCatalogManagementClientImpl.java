/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.datalake.analytics.implementation;

import com.microsoft.azure.AzureClient;
import com.microsoft.azure.AzureServiceClient;
import com.microsoft.azure.management.datalake.analytics.Catalogs;
import com.microsoft.azure.management.datalake.analytics.DataLakeAnalyticsCatalogManagementClient;
import com.microsoft.azure.serializer.AzureJacksonMapperAdapter;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import com.microsoft.rest.RestClient;

/**
 * Initializes a new instance of the DataLakeAnalyticsCatalogManagementClientImpl class.
 */
public final class DataLakeAnalyticsCatalogManagementClientImpl extends AzureServiceClient implements DataLakeAnalyticsCatalogManagementClient {
    /** the {@link AzureClient} used for long running operations. */
    private AzureClient azureClient;

    /**
     * Gets the {@link AzureClient} used for long running operations.
     * @return the azure client;
     */
    public AzureClient getAzureClient() {
        return this.azureClient;
    }

    /** Client Api Version. */
    private String apiVersion;

    /**
     * Gets Client Api Version.
     *
     * @return the apiVersion value.
     */
    public String apiVersion() {
        return this.apiVersion;
    }

    /** Gets the DNS suffix used as the base for all Azure Data Lake Analytics Catalog service requests. */
    private String adlaCatalogDnsSuffix;

    /**
     * Gets Gets the DNS suffix used as the base for all Azure Data Lake Analytics Catalog service requests.
     *
     * @return the adlaCatalogDnsSuffix value.
     */
    public String adlaCatalogDnsSuffix() {
        return this.adlaCatalogDnsSuffix;
    }

    /**
     * Sets Gets the DNS suffix used as the base for all Azure Data Lake Analytics Catalog service requests.
     *
     * @param adlaCatalogDnsSuffix the adlaCatalogDnsSuffix value.
     * @return the service client itself
     */
    public DataLakeAnalyticsCatalogManagementClientImpl withAdlaCatalogDnsSuffix(String adlaCatalogDnsSuffix) {
        this.adlaCatalogDnsSuffix = adlaCatalogDnsSuffix;
        return this;
    }

    /** Gets or sets the preferred language for the response. */
    private String acceptLanguage;

    /**
     * Gets Gets or sets the preferred language for the response.
     *
     * @return the acceptLanguage value.
     */
    public String acceptLanguage() {
        return this.acceptLanguage;
    }

    /**
     * Sets Gets or sets the preferred language for the response.
     *
     * @param acceptLanguage the acceptLanguage value.
     * @return the service client itself
     */
    public DataLakeAnalyticsCatalogManagementClientImpl withAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
        return this;
    }

    /** Gets or sets the retry timeout in seconds for Long Running Operations. Default value is 30. */
    private int longRunningOperationRetryTimeout;

    /**
     * Gets Gets or sets the retry timeout in seconds for Long Running Operations. Default value is 30.
     *
     * @return the longRunningOperationRetryTimeout value.
     */
    public int longRunningOperationRetryTimeout() {
        return this.longRunningOperationRetryTimeout;
    }

    /**
     * Sets Gets or sets the retry timeout in seconds for Long Running Operations. Default value is 30.
     *
     * @param longRunningOperationRetryTimeout the longRunningOperationRetryTimeout value.
     * @return the service client itself
     */
    public DataLakeAnalyticsCatalogManagementClientImpl withLongRunningOperationRetryTimeout(int longRunningOperationRetryTimeout) {
        this.longRunningOperationRetryTimeout = longRunningOperationRetryTimeout;
        return this;
    }

    /** When set to true a unique x-ms-client-request-id value is generated and included in each request. Default is true. */
    private boolean generateClientRequestId;

    /**
     * Gets When set to true a unique x-ms-client-request-id value is generated and included in each request. Default is true.
     *
     * @return the generateClientRequestId value.
     */
    public boolean generateClientRequestId() {
        return this.generateClientRequestId;
    }

    /**
     * Sets When set to true a unique x-ms-client-request-id value is generated and included in each request. Default is true.
     *
     * @param generateClientRequestId the generateClientRequestId value.
     * @return the service client itself
     */
    public DataLakeAnalyticsCatalogManagementClientImpl withGenerateClientRequestId(boolean generateClientRequestId) {
        this.generateClientRequestId = generateClientRequestId;
        return this;
    }

    /**
     * The Catalogs object to access its operations.
     */
    private Catalogs catalogs;

    /**
     * Gets the Catalogs object to access its operations.
     * @return the Catalogs object.
     */
    public Catalogs catalogs() {
        return this.catalogs;
    }

    /**
     * Initializes an instance of DataLakeAnalyticsCatalogManagementClient client.
     *
     * @param credentials the management credentials for Azure
     */
    public DataLakeAnalyticsCatalogManagementClientImpl(ServiceClientCredentials credentials) {
        this("https://{accountName}.{adlaCatalogDnsSuffix}", credentials);
    }

    /**
     * Initializes an instance of DataLakeAnalyticsCatalogManagementClient client.
     *
     * @param baseUrl the base URL of the host
     * @param credentials the management credentials for Azure
     */
    private DataLakeAnalyticsCatalogManagementClientImpl(String baseUrl, ServiceClientCredentials credentials) {
        this(new RestClient.Builder(baseUrl)
                .withMapperAdapter(new AzureJacksonMapperAdapter())
                .withCredentials(credentials)
                .build());
    }

    /**
     * Initializes an instance of DataLakeAnalyticsCatalogManagementClient client.
     *
     * @param restClient the REST client to connect to Azure.
     */
    public DataLakeAnalyticsCatalogManagementClientImpl(RestClient restClient) {
        super(restClient);
        initialize();
    }

    protected void initialize() {
        this.apiVersion = "2015-10-01-preview";
        this.adlaCatalogDnsSuffix = "azuredatalakeanalytics.net";
        this.acceptLanguage = "en-US";
        this.longRunningOperationRetryTimeout = 30;
        this.generateClientRequestId = true;
        this.catalogs = new CatalogsImpl(restClient().retrofit(), this);
        this.azureClient = new AzureClient(this);
    }

    /**
     * Gets the User-Agent header for the client.
     *
     * @return the user agent string.
     */
    @Override
    public String userAgent() {
        return String.format("Azure-SDK-For-Java/%s (%s)",
                getClass().getPackage().getImplementationVersion(),
                "DataLakeAnalyticsCatalogManagementClient, 2015-10-01-preview");
    }
}
