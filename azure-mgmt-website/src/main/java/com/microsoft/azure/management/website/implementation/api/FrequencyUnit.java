/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for FrequencyUnit.
 */
public enum FrequencyUnit {
    /** Enum value Day. */
    DAY("Day"),

    /** Enum value Hour. */
    HOUR("Hour");

    /** The actual serialized value for a FrequencyUnit instance. */
    private String value;

    FrequencyUnit(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a FrequencyUnit instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a FrequencyUnit instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed FrequencyUnit object, or null if unable to parse.
     */
    @JsonCreator
    public static FrequencyUnit fromValue(String value) {
        FrequencyUnit[] items = FrequencyUnit.values();
        for (FrequencyUnit item : items) {
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
