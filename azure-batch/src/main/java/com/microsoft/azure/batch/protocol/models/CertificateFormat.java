/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.batch.protocol.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for CertificateFormat.
 */
public enum CertificateFormat {
    /** Enum value pfx. */
    PFX("pfx"),

    /** Enum value cer. */
    CER("cer"),

    /** Enum value unmapped. */
    UNMAPPED("unmapped");

    /** The actual serialized value for a CertificateFormat instance. */
    private String value;

    CertificateFormat(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a CertificateFormat instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a CertificateFormat instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed CertificateFormat object, or null if unable to parse.
     */
    @JsonCreator
    public static CertificateFormat fromValue(String value) {
        CertificateFormat[] items = CertificateFormat.values();
        for (CertificateFormat item : items) {
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
