package com.rahmatullo.comfortmarket.service.enums;


public enum Action4Product {
    EXPORTED("%s product exported. count: %s"),
    IMPORTED("%s product imported. count: %s"),
    TRANSFERRED("%s product transferred to %s. count: %s"),
    DELETED("%s product deleted"),
    UPDATED("%s product updated"),
    CREATED("%s product created");
    private final String description;

    Action4Product(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
