/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for CloneAbilityResult.
 */
public enum CloneAbilityResult {
    /** Enum value Cloneable. */
    CLONEABLE("Cloneable"),

    /** Enum value PartiallyCloneable. */
    PARTIALLY_CLONEABLE("PartiallyCloneable"),

    /** Enum value NotCloneable. */
    NOT_CLONEABLE("NotCloneable");

    /** The actual serialized value for a CloneAbilityResult instance. */
    private String value;

    CloneAbilityResult(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a CloneAbilityResult instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a CloneAbilityResult instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed CloneAbilityResult object, or null if unable to parse.
     */
    @JsonCreator
    public static CloneAbilityResult fromValue(String value) {
        CloneAbilityResult[] items = CloneAbilityResult.values();
        for (CloneAbilityResult item : items) {
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
