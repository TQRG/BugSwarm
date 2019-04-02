/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for ManagedPipelineMode.
 */
public enum ManagedPipelineMode {
    /** Enum value Integrated. */
    INTEGRATED("Integrated"),

    /** Enum value Classic. */
    CLASSIC("Classic");

    /** The actual serialized value for a ManagedPipelineMode instance. */
    private String value;

    ManagedPipelineMode(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a ManagedPipelineMode instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a ManagedPipelineMode instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed ManagedPipelineMode object, or null if unable to parse.
     */
    @JsonCreator
    public static ManagedPipelineMode fromValue(String value) {
        ManagedPipelineMode[] items = ManagedPipelineMode.values();
        for (ManagedPipelineMode item : items) {
            if (item.toValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toValue();
    }
}
