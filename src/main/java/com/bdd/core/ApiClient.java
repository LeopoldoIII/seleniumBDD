package com.bdd.core;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    public static RequestSpecification getRequestSpec(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();
    }

    public static RequestSpecification getAuthenticatedRequestSpec(String baseUrl, String token) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
