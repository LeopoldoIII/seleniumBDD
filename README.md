# Selenium/RESTAssured Testing Framework

## Overview

This project is a Selenium-based & RESTAssured automated testing framework written in Java. It follows the Page Object Model (POM) and Behavior Driven Development (BDD) principles to provide a structured and maintainable way to write and execute tests. The framework uses WebDriverManager to manage browser drivers and supports running tests in headless mode for continuous integration environments.


## Setup

### Prerequisites

- Java Development Kit (JDK) 22
- Maven
- Chrome or Firefox browser

### Installation

1. Clone the repository:

2. Install dependencies:
    ```bash
    mvn clean install
    ```

   ```bash
   mvn clean test -Dtest=UiTestRunner
    ```

    ```bash
   mvn clean test -Dtest=ApiTestRunner
    ```

### Configuration

Edit the `src/main/resources/config.json` file to set up the configuration parameters, such as the URL, username, password, and whether to run in headless mode.

**Example `config.json`:**

```json
{
    "url": "https://www.example.com",
    "username": "testuser",
    "password": "testpassword",
    "headless": true
}


