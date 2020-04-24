package com.avionos.aem.akamai.purgesurgeon.enums;

/**
 * Akamai purge type.
 */
public enum PurgeType {
    URL("url"),
    CP_CODE("cpcode"),
    CACHE_TAG("tag");

    private final String type;

    PurgeType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
