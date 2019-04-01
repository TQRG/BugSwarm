package com.microsoft.azure.management.storage;

public enum KeyType {
    PRIMARY("Primary"),

    SECONDARY("Secondary");

    private String value;

    KeyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
