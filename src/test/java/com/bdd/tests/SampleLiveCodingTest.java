package com.bdd.tests;

import com.bdd.config.ConfigReader;
import com.bdd.core.ApiClient;
import com.bdd.core.DriverManager;
import com.bdd.pages.LoginPage;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Direct TestNG test class for fast execution during live coding sessions.
 * Can be run individually from IntelliJ / VS Code with a single click.
 */
public class SampleLiveCodingTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.closeDriver();
    }

    @Test(description = "Verify UI login page loads and interacts correctly")
    public void testUiFlow() {
        driver.get(ConfigReader.getProperty("url"));
        new LoginPage(driver);
        Assert.assertNotNull(driver.getTitle(), "Page title should not be null");
    }

    @Test(description = "Verify API endpoint responds with expected status and payload")
    public void testApiEndpoint() {
        String baseUrl = "https://jsonplaceholder.typicode.com";
        Response response = given()
                .spec(ApiClient.getRequestSpec(baseUrl))
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String name = response.jsonPath().getString("name");
        Assert.assertEquals(name, "Leanne Graham");
    }
}
