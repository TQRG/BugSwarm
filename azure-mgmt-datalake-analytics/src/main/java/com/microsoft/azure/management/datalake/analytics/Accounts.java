/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.datalake.analytics;

import com.microsoft.azure.CloudException;
import com.microsoft.azure.ListOperationCallback;
import com.microsoft.azure.management.datalake.analytics.models.AddDataLakeStoreParameters;
import com.microsoft.azure.management.datalake.analytics.models.AddStorageAccountParameters;
import com.microsoft.azure.management.datalake.analytics.models.BlobContainer;
import com.microsoft.azure.management.datalake.analytics.models.DataLakeAnalyticsAccount;
import com.microsoft.azure.management.datalake.analytics.models.DataLakeStoreAccountInfo;
import com.microsoft.azure.management.datalake.analytics.models.PageImpl;
import com.microsoft.azure.management.datalake.analytics.models.SasTokenInfo;
import com.microsoft.azure.management.datalake.analytics.models.StorageAccountInfo;
import com.microsoft.azure.PagedList;
import com.microsoft.rest.ServiceCall;
import com.microsoft.rest.ServiceCallback;
import com.microsoft.rest.ServiceResponse;
import java.io.IOException;

/**
 * An instance of this class provides access to all the operations defined
 * in Accounts.
 */
public interface Accounts {
    /**
     * Gets the specified Azure Storage account linked to the given Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to retrieve Azure storage account details.
     * @param storageAccountName The name of the Azure Storage account for which to retrieve the details.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the StorageAccountInfo object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<StorageAccountInfo> getStorageAccount(String resourceGroupName, String accountName, String storageAccountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the specified Azure Storage account linked to the given Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to retrieve Azure storage account details.
     * @param storageAccountName The name of the Azure Storage account for which to retrieve the details.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall getStorageAccountAsync(String resourceGroupName, String accountName, String storageAccountName, final ServiceCallback<StorageAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to remove an Azure Storage account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to remove the Azure Storage account.
     * @param storageAccountName The name of the Azure Storage account to remove
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> deleteStorageAccount(String resourceGroupName, String accountName, String storageAccountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to remove an Azure Storage account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to remove the Azure Storage account.
     * @param storageAccountName The name of the Azure Storage account to remove
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall deleteStorageAccountAsync(String resourceGroupName, String accountName, String storageAccountName, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account to replace Azure Storage blob account details, such as the access key and/or suffix.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to modify storage accounts in
     * @param storageAccountName The Azure Storage account to modify
     * @param parameters The parameters containing the access key and suffix to update the storage account with.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> updateStorageAccount(String resourceGroupName, String accountName, String storageAccountName, AddStorageAccountParameters parameters) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account to replace Azure Storage blob account details, such as the access key and/or suffix.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to modify storage accounts in
     * @param storageAccountName The Azure Storage account to modify
     * @param parameters The parameters containing the access key and suffix to update the storage account with.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall updateStorageAccountAsync(String resourceGroupName, String accountName, String storageAccountName, AddStorageAccountParameters parameters, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to add an Azure Storage account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to which to add the Azure Storage account.
     * @param storageAccountName The name of the Azure Storage account to add
     * @param parameters The parameters containing the access key and optional suffix for the Azure Storage Account.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> addStorageAccount(String resourceGroupName, String accountName, String storageAccountName, AddStorageAccountParameters parameters) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to add an Azure Storage account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to which to add the Azure Storage account.
     * @param storageAccountName The name of the Azure Storage account to add
     * @param parameters The parameters containing the access key and optional suffix for the Azure Storage Account.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall addStorageAccountAsync(String resourceGroupName, String accountName, String storageAccountName, AddStorageAccountParameters parameters, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the specified Azure Storage container associated with the given Data Lake Analytics and Azure Storage accounts.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to retrieve blob container.
     * @param storageAccountName The name of the Azure storage account from which to retrieve the blob container.
     * @param containerName The name of the Azure storage container to retrieve
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the BlobContainer object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<BlobContainer> getStorageContainer(String resourceGroupName, String accountName, String storageAccountName, String containerName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the specified Azure Storage container associated with the given Data Lake Analytics and Azure Storage accounts.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to retrieve blob container.
     * @param storageAccountName The name of the Azure storage account from which to retrieve the blob container.
     * @param containerName The name of the Azure storage container to retrieve
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall getStorageContainerAsync(String resourceGroupName, String accountName, String storageAccountName, String containerName, final ServiceCallback<BlobContainer> serviceCallback) throws IllegalArgumentException;

    /**
     * Lists the Azure Storage containers, if any, associated with the specified Data Lake Analytics and Azure Storage account combination. The response includes a link to the next page of results, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage blob containers.
     * @param storageAccountName The name of the Azure storage account from which to list blob containers.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;BlobContainer&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<BlobContainer>> listStorageContainers(final String resourceGroupName, final String accountName, final String storageAccountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Lists the Azure Storage containers, if any, associated with the specified Data Lake Analytics and Azure Storage account combination. The response includes a link to the next page of results, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage blob containers.
     * @param storageAccountName The name of the Azure storage account from which to list blob containers.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listStorageContainersAsync(final String resourceGroupName, final String accountName, final String storageAccountName, final ListOperationCallback<BlobContainer> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the SAS token associated with the specified Data Lake Analytics and Azure Storage account and container combination.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which an Azure Storage account's SAS token is being requested.
     * @param storageAccountName The name of the Azure storage account for which the SAS token is being requested.
     * @param containerName The name of the Azure storage container for which the SAS token is being requested.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;SasTokenInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<SasTokenInfo>> listSasTokens(final String resourceGroupName, final String accountName, final String storageAccountName, final String containerName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the SAS token associated with the specified Data Lake Analytics and Azure Storage account and container combination.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which an Azure Storage account's SAS token is being requested.
     * @param storageAccountName The name of the Azure storage account for which the SAS token is being requested.
     * @param containerName The name of the Azure storage container for which the SAS token is being requested.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listSasTokensAsync(final String resourceGroupName, final String accountName, final String storageAccountName, final String containerName, final ListOperationCallback<SasTokenInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the specified Data Lake Store account details in the specified Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to retrieve the Data Lake Store account details.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to retrieve
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the DataLakeStoreAccountInfo object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeStoreAccountInfo> getDataLakeStoreAccount(String resourceGroupName, String accountName, String dataLakeStoreAccountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the specified Data Lake Store account details in the specified Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to retrieve the Data Lake Store account details.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to retrieve
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall getDataLakeStoreAccountAsync(String resourceGroupName, String accountName, String dataLakeStoreAccountName, final ServiceCallback<DataLakeStoreAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account specified to remove the specified Data Lake Store account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to remove the Data Lake Store account.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to remove
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> deleteDataLakeStoreAccount(String resourceGroupName, String accountName, String dataLakeStoreAccountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account specified to remove the specified Data Lake Store account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account from which to remove the Data Lake Store account.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to remove
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall deleteDataLakeStoreAccountAsync(String resourceGroupName, String accountName, String dataLakeStoreAccountName, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to include the additional Data Lake Store account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to which to add the Data Lake Store account.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to add.
     * @param parameters The details of the Data Lake Store account.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> addDataLakeStoreAccount(String resourceGroupName, String accountName, String dataLakeStoreAccountName, AddDataLakeStoreParameters parameters) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the specified Data Lake Analytics account to include the additional Data Lake Store account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to which to add the Data Lake Store account.
     * @param dataLakeStoreAccountName The name of the Data Lake Store account to add.
     * @param parameters The details of the Data Lake Store account.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall addDataLakeStoreAccountAsync(String resourceGroupName, String accountName, String dataLakeStoreAccountName, AddDataLakeStoreParameters parameters, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage accounts.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;StorageAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<StorageAccountInfo>> listStorageAccounts(final String resourceGroupName, final String accountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage accounts.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listStorageAccountsAsync(final String resourceGroupName, final String accountName, final ListOperationCallback<StorageAccountInfo> serviceCallback) throws IllegalArgumentException;
    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage accounts.
     * @param filter The OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;StorageAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<StorageAccountInfo>> listStorageAccounts(final String resourceGroupName, final String accountName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Azure Storage accounts.
     * @param filter The OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listStorageAccountsAsync(final String resourceGroupName, final String accountName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format, final ListOperationCallback<StorageAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Data Lake Store accounts.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeStoreAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeStoreAccountInfo>> listDataLakeStoreAccounts(final String resourceGroupName, final String accountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Data Lake Store accounts.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listDataLakeStoreAccountsAsync(final String resourceGroupName, final String accountName, final ListOperationCallback<DataLakeStoreAccountInfo> serviceCallback) throws IllegalArgumentException;
    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Data Lake Store accounts.
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeStoreAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeStoreAccountInfo>> listDataLakeStoreAccounts(final String resourceGroupName, final String accountName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account for which to list Data Lake Store accounts.
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listDataLakeStoreAccountsAsync(final String resourceGroupName, final String accountName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format, final ListOperationCallback<DataLakeStoreAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeAnalyticsAccount>> listByResourceGroup(final String resourceGroupName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listByResourceGroupAsync(final String resourceGroupName, final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;
    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeAnalyticsAccount>> listByResourceGroup(final String resourceGroupName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listByResourceGroupAsync(final String resourceGroupName, final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format, final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeAnalyticsAccount>> list() throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listAsync(final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;
    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PagedList<DataLakeAnalyticsAccount>> list(final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @param filter OData filter. Optional.
     * @param top The number of items to return. Optional.
     * @param skip The number of items to skip over before returning elements. Optional.
     * @param expand OData expansion. Expand related resources in line with the retrieved resources, e.g. Categories/$expand=Products would expand Product data in line with each Category entry. Optional.
     * @param select OData Select statement. Limits the properties on each entry to just those requested, e.g. Categories?$select=CategoryName,Description. Optional.
     * @param orderby OrderBy clause. One or more comma-separated expressions with an optional "asc" (the default) or "desc" depending on the order you'd like the values sorted, e.g. Categories?$orderby=CategoryName desc. Optional.
     * @param count The Boolean value of true or false to request a count of the matching resources included with the resources in the response, e.g. Categories?$count=true. Optional.
     * @param search A free form search. A free-text search expression to match for whether a particular entry should be included in the feed, e.g. Categories?$search=blue OR green. Optional.
     * @param format The desired return format. Return the response in particular formatxii without access to request headers for standard content-type negotiation (e.g Orders?$format=json). Optional.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listAsync(final String filter, final Integer top, final Integer skip, final String expand, final String select, final String orderby, final Boolean count, final String search, final String format, final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets details of the specified Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to retrieve.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the DataLakeAnalyticsAccount object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeAnalyticsAccount> get(String resourceGroupName, String accountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets details of the specified Data Lake Analytics account.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to retrieve.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall getAsync(String resourceGroupName, String accountName, final ServiceCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Begins the delete delete process for the Data Lake Analytics account object specified by the account name.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to delete
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @throws InterruptedException exception thrown when long running operation is interrupted
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> delete(String resourceGroupName, String accountName) throws CloudException, IOException, IllegalArgumentException, InterruptedException;

    /**
     * Begins the delete delete process for the Data Lake Analytics account object specified by the account name.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to delete
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall deleteAsync(String resourceGroupName, String accountName, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Begins the delete delete process for the Data Lake Analytics account object specified by the account name.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to delete
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the {@link ServiceResponse} object if successful.
     */
    ServiceResponse<Void> beginDelete(String resourceGroupName, String accountName) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Begins the delete delete process for the Data Lake Analytics account object specified by the account name.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param accountName The name of the Data Lake Analytics account to delete
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall beginDeleteAsync(String resourceGroupName, String accountName, final ServiceCallback<Void> serviceCallback) throws IllegalArgumentException;

    /**
     * Creates the specified Data Lake Analytics account. This supplies the user with computation services for Data Lake Analytics workloads.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.the account will be associated with.
     * @param name The name of the Data Lake Analytics account to create.
     * @param parameters Parameters supplied to the create Data Lake Analytics account operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @throws InterruptedException exception thrown when long running operation is interrupted
     * @return the DataLakeAnalyticsAccount object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeAnalyticsAccount> create(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters) throws CloudException, IOException, IllegalArgumentException, InterruptedException;

    /**
     * Creates the specified Data Lake Analytics account. This supplies the user with computation services for Data Lake Analytics workloads.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.the account will be associated with.
     * @param name The name of the Data Lake Analytics account to create.
     * @param parameters Parameters supplied to the create Data Lake Analytics account operation.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall createAsync(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters, final ServiceCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Creates the specified Data Lake Analytics account. This supplies the user with computation services for Data Lake Analytics workloads.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.the account will be associated with.
     * @param name The name of the Data Lake Analytics account to create.
     * @param parameters Parameters supplied to the create Data Lake Analytics account operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the DataLakeAnalyticsAccount object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeAnalyticsAccount> beginCreate(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Creates the specified Data Lake Analytics account. This supplies the user with computation services for Data Lake Analytics workloads.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.the account will be associated with.
     * @param name The name of the Data Lake Analytics account to create.
     * @param parameters Parameters supplied to the create Data Lake Analytics account operation.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall beginCreateAsync(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters, final ServiceCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account object specified by the accountName with the contents of the account object.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param name The name of the Data Lake Analytics account to update.
     * @param parameters Parameters supplied to the update Data Lake Analytics account operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @throws InterruptedException exception thrown when long running operation is interrupted
     * @return the DataLakeAnalyticsAccount object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeAnalyticsAccount> update(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters) throws CloudException, IOException, IllegalArgumentException, InterruptedException;

    /**
     * Updates the Data Lake Analytics account object specified by the accountName with the contents of the account object.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param name The name of the Data Lake Analytics account to update.
     * @param parameters Parameters supplied to the update Data Lake Analytics account operation.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall updateAsync(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters, final ServiceCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account object specified by the accountName with the contents of the account object.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param name The name of the Data Lake Analytics account to update.
     * @param parameters Parameters supplied to the update Data Lake Analytics account operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the DataLakeAnalyticsAccount object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<DataLakeAnalyticsAccount> beginUpdate(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Updates the Data Lake Analytics account object specified by the accountName with the contents of the account object.
     *
     * @param resourceGroupName The name of the Azure resource group that contains the Data Lake Analytics account.
     * @param name The name of the Data Lake Analytics account to update.
     * @param parameters Parameters supplied to the update Data Lake Analytics account operation.
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall beginUpdateAsync(String resourceGroupName, String name, DataLakeAnalyticsAccount parameters, final ServiceCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Lists the Azure Storage containers, if any, associated with the specified Data Lake Analytics and Azure Storage account combination. The response includes a link to the next page of results, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;BlobContainer&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<BlobContainer>> listStorageContainersNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Lists the Azure Storage containers, if any, associated with the specified Data Lake Analytics and Azure Storage account combination. The response includes a link to the next page of results, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listStorageContainersNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<BlobContainer> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the SAS token associated with the specified Data Lake Analytics and Azure Storage account and container combination.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;SasTokenInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<SasTokenInfo>> listSasTokensNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the SAS token associated with the specified Data Lake Analytics and Azure Storage account and container combination.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listSasTokensNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<SasTokenInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;StorageAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<StorageAccountInfo>> listStorageAccountsNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Azure Storage accounts, if any, linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listStorageAccountsNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<StorageAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeStoreAccountInfo&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<DataLakeStoreAccountInfo>> listDataLakeStoreAccountsNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Store accounts linked to the specified Data Lake Analytics account. The response includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listDataLakeStoreAccountsNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<DataLakeStoreAccountInfo> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<DataLakeAnalyticsAccount>> listByResourceGroupNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within a specific resource group. This includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listByResourceGroupNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @throws CloudException exception thrown from REST call
     * @throws IOException exception thrown from serialization/deserialization
     * @throws IllegalArgumentException exception thrown from invalid parameters
     * @return the List&lt;DataLakeAnalyticsAccount&gt; object wrapped in {@link ServiceResponse} if successful.
     */
    ServiceResponse<PageImpl<DataLakeAnalyticsAccount>> listNext(final String nextPageLink) throws CloudException, IOException, IllegalArgumentException;

    /**
     * Gets the first page of Data Lake Analytics accounts, if any, within the current subscription. This includes a link to the next page, if any.
     *
     * @param nextPageLink The NextLink from the previous successful call to List operation.
     * @param serviceCall the ServiceCall object tracking the Retrofit calls
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if callback is null
     * @return the {@link ServiceCall} object
     */
    ServiceCall listNextAsync(final String nextPageLink, final ServiceCall serviceCall, final ListOperationCallback<DataLakeAnalyticsAccount> serviceCallback) throws IllegalArgumentException;

}
