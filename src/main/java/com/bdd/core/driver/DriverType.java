package com.bdd.core.driver;

public enum DriverType {
    CHROME,
    FIREFOX,
    EDGE,
    ANDROID,
    IOS;

    public static DriverType fromString(String name) {
        if (name == null) return CHROME;
        try {
            return DriverType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CHROME;
        }
    }
}
