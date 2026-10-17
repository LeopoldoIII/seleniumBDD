package com.bdd.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe Scenario Context for sharing data across Steps and Page Objects
 * within the lifecycle of a single Cucumber scenario.
 * Directly mirrors Python Behave's `context` variable in a thread-safe Java environment.
 */
public class ScenarioContext {

    private static final ThreadLocal<Map<String, Object>> contextData = ThreadLocal.withInitial(HashMap::new);

    private ScenarioContext() {
        // Prevent instantiation
    }

    public static void set(String key, Object value) {
        contextData.get().put(key, value);
    }

    public static Object get(String key) {
        return contextData.get().get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        Object val = contextData.get().get(key);
        if (val == null) return null;
        return (T) val;
    }

    public static boolean contains(String key) {
        return contextData.get().containsKey(key);
    }

    public static void remove(String key) {
        contextData.get().remove(key);
    }

    /**
     * Clears all context data for the current thread.
     * Invoked in Hooks @After to prevent memory leaks across scenarios.
     */
    public static void clear() {
        contextData.get().clear();
        contextData.remove();
    }
}
