/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for HostNameType.
 */
public enum HostNameType {
    /** Enum value Verified. */
    VERIFIED("Verified"),

    /** Enum value Managed. */
    MANAGED("Managed");

    /** The actual serialized value for a HostNameType instance. */
    private String value;

    HostNameType(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a HostNameType instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a HostNameType instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed HostNameType object, or null if unable to parse.
     */
    @JsonCreator
    public static HostNameType fromValue(String value) {
        HostNameType[] items = HostNameType.values();
        for (HostNameType item : items) {
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
