/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.compute.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for ComponentNames.
 */
public enum ComponentNames {
    /** Enum value Microsoft-Windows-Shell-Setup. */
    MICROSOFT_WINDOWS_SHELL_SETUP("Microsoft-Windows-Shell-Setup");

    /** The actual serialized value for a ComponentNames instance. */
    private String value;

    ComponentNames(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a ComponentNames instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a ComponentNames instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed ComponentNames object, or null if unable to parse.
     */
    @JsonCreator
    public static ComponentNames fromValue(String value) {
        ComponentNames[] items = ComponentNames.values();
        for (ComponentNames item : items) {
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
