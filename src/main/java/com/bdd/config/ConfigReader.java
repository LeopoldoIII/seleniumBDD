package com.bdd.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class ConfigReader {

    private static JsonObject config;

    static {
        try {
            FileReader reader = new FileReader("src/main/resources/config.json");
            config = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return config.get(key).getAsString();
    }

    public static boolean getBooleanProperty(String key) {
        return config.has(key) ? config.get(key).getAsBoolean() : false;
    }
}