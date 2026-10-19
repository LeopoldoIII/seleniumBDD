package com.bdd.core.driver;

public enum ExecutionTarget {
    LOCAL,
    GRID,
    SAUCELABS;

    public static ExecutionTarget fromString(String target) {
        if (target == null) return LOCAL;
        try {
            return ExecutionTarget.valueOf(target.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return LOCAL;
        }
    }
}
