/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.datalake.analytics.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Azure Storage blob container information.
 */
public class BlobContainer {
    /**
     * the name of the blob container.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String name;

    /**
     * the unique identifier of the blob container.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;

    /**
     * the type of the blob container.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    /**
     * the properties of the blob container.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BlobContainerProperties properties;

    /**
     * Get the name value.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Get the id value.
     *
     * @return the id value
     */
    public String id() {
        return this.id;
    }

    /**
     * Get the type value.
     *
     * @return the type value
     */
    public String type() {
        return this.type;
    }

    /**
     * Get the properties value.
     *
     * @return the properties value
     */
    public BlobContainerProperties properties() {
        return this.properties;
    }

}
