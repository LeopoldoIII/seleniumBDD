package com.bdd.api;

import com.bdd.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Centralized REST Assured API client supporting GET, POST, PUT, DELETE, PATCH
 * with configurable base URI, headers, and authentication.
 */
public class ApiClient {

    private String baseUri;
    private RequestSpecification requestSpec;

    public ApiClient(String baseUri) {
        this.baseUri = baseUri != null ? baseUri : ConfigReader.getApiBaseUri();
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(this.baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public ApiClient() {
        this(ConfigReader.getApiBaseUri());
    }

    public void setHeader(String name, String value) {
        this.requestSpec.header(name, value);
    }

    public void setAuthToken(String token) {
        this.requestSpec.header("Authorization", "Bearer " + token);
    }

    public void setBasicAuth(String username, String password) {
        this.requestSpec.auth().preemptive().basic(username, password);
    }

    public Response get(String endpoint) {
        return given().spec(requestSpec).when().get(endpoint);
    }

    public Response get(String endpoint, Map<String, ?> queryParams) {
        return given().spec(requestSpec).queryParams(queryParams).when().get(endpoint);
    }

    public Response post(String endpoint, Object body) {
        return given().spec(requestSpec).body(body).when().post(endpoint);
    }

    public Response put(String endpoint, Object body) {
        return given().spec(requestSpec).body(body).when().put(endpoint);
    }

    public Response patch(String endpoint, Object body) {
        return given().spec(requestSpec).body(body).when().patch(endpoint);
    }

    public Response delete(String endpoint) {
        return given().spec(requestSpec).when().delete(endpoint);
    }

    public String getBaseUri() {
        return baseUri;
    }
}
