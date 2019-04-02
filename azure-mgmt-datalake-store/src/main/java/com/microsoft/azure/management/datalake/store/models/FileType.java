/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.datalake.store.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for FileType.
 */
public enum FileType {
    /** Enum value FILE. */
    FILE("FILE"),

    /** Enum value DIRECTORY. */
    DIRECTORY("DIRECTORY");

    /** The actual serialized value for a FileType instance. */
    private String value;

    FileType(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a FileType instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a FileType instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed FileType object, or null if unable to parse.
     */
    @JsonCreator
    public static FileType fromValue(String value) {
        FileType[] items = FileType.values();
        for (FileType item : items) {
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
