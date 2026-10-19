package com.bdd.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Enterprise Test Data & Date Utility.
 * Provides conflict-free dynamic test data (timestamped emails, unique IDs, relative dates)
 * for parallel executions and shared environments.
 */
public class TestDataUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Generates a unique email with timestamp to avoid collisions in parallel runs.
     * Example: test_user_1697664800@example.com
     */
    public static String generateEmail(String prefix, String domain) {
        String cleanPrefix = (prefix != null && !prefix.trim().isEmpty()) ? prefix.trim() : "testuser";
        String cleanDomain = (domain != null && !domain.trim().isEmpty()) ? domain.trim() : "example.com";
        if (cleanDomain.startsWith("@")) {
            cleanDomain = cleanDomain.substring(1);
        }
        return cleanPrefix + "_" + System.currentTimeMillis() + "@" + cleanDomain;
    }

    public static String generateEmail(String prefix) {
        return generateEmail(prefix, "example.com");
    }

    /**
     * Generates a unique business ID with a prefix and random alphanumeric string.
     * Example: PLAN-784f9b2c
     */
    public static String generateId(String prefix) {
        String cleanPrefix = (prefix != null && !prefix.trim().isEmpty()) ? prefix.trim() : "ID";
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 8);
        return cleanPrefix + "-" + uuidSuffix;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static String getTodayDate(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getTodayDate() {
        return getTodayDate(DEFAULT_DATE_FORMAT);
    }

    /**
     * Calculates a past date relative to today.
     *
     * @param daysAgo Number of days in the past
     * @param pattern Date format pattern (e.g. "yyyy-MM-dd")
     */
    public static String getPastDate(int daysAgo, String pattern) {
        return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getPastDate(int daysAgo) {
        return getPastDate(daysAgo, DEFAULT_DATE_FORMAT);
    }

    /**
     * Calculates a future date relative to today (useful for due dates, expiration dates).
     *
     * @param daysAhead Number of days in the future
     * @param pattern   Date format pattern (e.g. "yyyy-MM-dd")
     */
    public static String getFutureDate(int daysAhead, String pattern) {
        return LocalDate.now().plusDays(daysAhead).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getFutureDate(int daysAhead) {
        return getFutureDate(daysAhead, DEFAULT_DATE_FORMAT);
    }

    /**
     * Formats current date and time with given pattern.
     */
    public static String getCurrentDateTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
