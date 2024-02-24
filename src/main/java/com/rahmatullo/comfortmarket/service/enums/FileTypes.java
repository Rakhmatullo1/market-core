package com.rahmatullo.comfortmarket.service.enums;

public enum FileTypes {
    JPEG("image/jpeg", ".jpeg"),
    PNG("image/png", ".png");
    private final String contentType;
    private final String suffix;

    FileTypes(String contentType, String suffix) {
        this.contentType = contentType;
        this.suffix = suffix;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSuffix() {
        return suffix;
    }
}
