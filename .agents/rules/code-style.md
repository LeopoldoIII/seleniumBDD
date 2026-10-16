# Code Style & Quality Standards

## Java Conventions

1. **Naming Conventions**:
   - **Classes**: PascalCase (e.g., `LoginPage`, `UiTestRunner`, `ConfigReader`).
   - **Methods & Variables**: camelCase (e.g., `login(username, password)`, `driverManager`).
   - **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEOUT_SECONDS`).
   - **Step Methods**: Descriptive names matching the step intent or camelCase equivalents (e.g., `i_am_on_the_login_page()` or `iAmOnTheLoginPage()`).

2. **Packages & Imports**:
   - Keep classes strictly within their corresponding package boundaries:
     - `com.bdd.config`
     - `com.bdd.core`
     - `com.bdd.pages`
     - `com.bdd.steps.ui`
     - `com.bdd.steps.api`
     - `com.bdd.hooks`
     - `com.bdd.runners`
   - Avoid wildcard imports (`import org.openqa.selenium.*`). Import only specific types.
   - Remove unused imports before committing.

3. **Gherkin Feature Files**:
   - Use declarative language rather than imperative clicking instructions.
   - Keep feature files in standard Cucumber structure:
     - Feature title and narrative
     - Background (for shared preconditions)
     - Scenario / Scenario Outline with Examples table
   - File extension must be `.feature`.
   - Place UI features in `src/test/resources/features/UI/`.
   - Place API features in `src/test/resources/features/API/`.

4. **Exception Handling & Cleanliness**:
   - Avoid throwing raw `Exception` where specific exceptions can be caught or propagated.
   - Clean up resources properly.
   - Do not commit debugging `System.out.println()` statements; use proper logging or assertions.
