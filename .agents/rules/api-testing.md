# API Automation with REST Assured Rules

## API Testing Guidelines

1. **Isolation from UI Browser**:
   - API tests must never initialize Selenium or trigger browser sessions.
   - Cucumber hooks in `Hooks.java` are tagged with `@Before("@ui or @mobile or not @api")` so API tests run cleanly without opening a browser.

2. **Centralized Client (`ApiClient`)**:
   - Prefer using `com.bdd.api.ApiClient` rather than raw RestAssured config in every step.
   - `ApiClient` automatically applies:
     - Configurable Base URI (`ConfigReader.getApiBaseUri()`)
     - Content-Type: application/json
     - Accept: application/json
     - Authentication helpers (`setAuthToken(token)`, `setBasicAuth(user, pass)`)

3. **Step Organization**:
   - Separate steps logically into:
     - `Given`: Setting base URI, headers, auth tokens, query parameters, or request payload.
     - `When`: Dispatching HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`, `PATCH`).
     - `Then`: Validating HTTP status code, headers, response body, or specific fields using JsonPath.

4. **Assertions**:
   - Use TestNG assertions (`org.testng.Assert`) for validating status codes and extracted fields.
   - Provide clear failure messages in assertions to simplify debugging.

5. **Data Management & Payloads**:
   - For complex JSON payloads, pass multiline DocStrings in feature files or define models in `src/main/java/com/bdd/api/models/`.
