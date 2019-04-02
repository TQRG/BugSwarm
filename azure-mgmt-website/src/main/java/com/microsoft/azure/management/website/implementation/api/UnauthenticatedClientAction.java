/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.website.implementation.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for UnauthenticatedClientAction.
 */
public enum UnauthenticatedClientAction {
    /** Enum value RedirectToLoginPage. */
    REDIRECT_TO_LOGIN_PAGE("RedirectToLoginPage"),

    /** Enum value AllowAnonymous. */
    ALLOW_ANONYMOUS("AllowAnonymous");

    /** The actual serialized value for a UnauthenticatedClientAction instance. */
    private String value;

    UnauthenticatedClientAction(String value) {
        this.value = value;
    }

    /**
     * Gets the serialized value for a UnauthenticatedClientAction instance.
     *
     * @return the serialized value.
     */
    @JsonValue
    public String toValue() {
        return this.value;
    }

    /**
     * Parses a serialized value to a UnauthenticatedClientAction instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed UnauthenticatedClientAction object, or null if unable to parse.
     */
    @JsonCreator
    public static UnauthenticatedClientAction fromValue(String value) {
        UnauthenticatedClientAction[] items = UnauthenticatedClientAction.values();
        for (UnauthenticatedClientAction item : items) {
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
