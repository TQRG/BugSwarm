/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for InternalLoadBalancingMode.
 */
public enum InternalLoadBalancingMode {
    /** Enum value None. */
    NONE("None"),

    /** Enum value Web. */
    WEB("Web"),

    /** Enum value Publishing. */
    PUBLISHING("Publishing");

    /** The actual serialized value for a InternalLoadBalancingMode instance. */
    private String value;

    InternalLoadBalancingMode(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a InternalLoadBalancingMode instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a InternalLoadBalancingMode instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed InternalLoadBalancingMode object, or null if unable to parse.
     */
    @JsonCreator
    public static InternalLoadBalancingMode fromValue(String value) {
        InternalLoadBalancingMode[] items = InternalLoadBalancingMode.values();
        for (InternalLoadBalancingMode item : items) {
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
