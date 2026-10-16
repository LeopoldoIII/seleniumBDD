# Omnichannel BDD Test Automation Framework (Java 21)

Enterprise-grade test automation framework built for **Web UI**, **REST API**, **Mobile (Android/iOS via Appium)**, and **Database (SQL/JDBC)** testing with **Cucumber BDD**, **TestNG**, and support for **Sauce Labs** cloud execution.

---

## 🚀 Quick Start for Live Coding (60 min)

### 1. Levantar el servicio local (`installments.jar`)
Si te proporcionan el archivo `.jar` en la entrevista:
```bash
java -jar installments.jar
# (Típicamente corre en http://localhost:8080)
```

### 2. Ejecutar Pruebas UI
```bash
# Ejecutar contra localhost (o sobreescribir la URL base al vuelo):
mvn clean test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080

# Ejecutar con navegador visible (no-headless):
mvn test -Dtest=UiTestRunner -Dheadless=false

# Filtrar por tags (ej. @smoke o @regression):
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke"
```

### 3. Ejecutar Pruebas API (REST Assured)
```bash
mvn clean test -Dtest=ApiTestRunner

# Sobreescribiendo la URL base del API:
mvn test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api
```

### 4. Ejecutar en Sauce Labs (Cloud)
```bash
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dsaucelabs.username="TU_USUARIO" \
  -Dsaucelabs.accessKey="TU_ACCESS_KEY" \
  -Dsaucelabs.region="us-west-1"
```

### 5. Ejecutar Pruebas Móviles (Android / iOS)
```bash
# Android Local (Appium):
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android

# iOS Local (Appium):
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=iOS
```

---

## 🏛️ Arquitectura del Proyecto

```text
src/
├── main/
│   ├── java/com/bdd/
│   │   ├── config/ConfigReader.java      # Configuración multinivel (CLI > Env > JSON)
│   │   ├── core/
│   │   │   ├── DriverManager.java        # ThreadLocal<WebDriver> (Thread-safe para paralelismo)
│   │   │   ├── driver/
│   │   │   │   ├── DriverFactory.java    # Factoría para Chrome, Firefox, Android, iOS y Sauce Labs
│   │   │   │   ├── SauceLabsManager.java # Opciones de Sauce Labs y actualización de job status
│   │   │   │   ├── DriverType.java       # Enum de navegadores y plataformas
│   │   │   │   └── ExecutionTarget.java  # Enum LOCAL vs SAUCELABS
│   │   │   └── database/
│   │   │       └── DbManager.java        # Conector SQL (Postgres, MySQL, H2) con executeQuery
│   │   ├── api/
│   │   │   └── ApiClient.java            # Cliente centralizado REST Assured
│   │   └── pages/
│   │       ├── BasePage.java             # Navaja Suiza: esperas explícitas, selects, tablas, clicks JS
│   │       ├── LoginPage.java            # Page Object estándar (sin static)
│   │       └── SearchPage.java           # Page Object estándar
│   └── resources/
│       └── config.json                   # Configuración central (Web, API, Mobile, DB, SauceLabs)
└── test/
    ├── java/com/bdd/
    │   ├── hooks/Hooks.java              # Screenshots automáticos en fallo y reporte a Sauce Labs
    │   ├── runners/                      # TestNG Runners (UiTestRunner, ApiTestRunner)
    │   └── steps/                        # Step definitions (ui/, api/, database/)
    └── resources/features/
        ├── UI/login.feature              # Escenarios BDD para interfaz de usuario
        └── API/api.feature               # Escenarios BDD para servicios REST
```

---

## 💡 Puntos Clave para la Entrevista ("Walking Through Your Decisions")

1. **Paralelización Segura**: Uso de `ThreadLocal<WebDriver>` en `DriverManager`, lo que permite que `UiTestRunner` ejecute escenarios en paralelo (`parallel = true`) sin colisiones entre navegadores.
2. **Resiliencia (`BasePage`)**: Cero `Thread.sleep()`. Uso de `WebDriverWait` explícito con fallback automático a JavaScript Click cuando animaciones o modales bloquean el clic tradicional.
3. **Soporte para Tablas Dinámicas**: `BasePage` incluye métodos preconstruidos (`getTableCellText`, `findRowIndexContainingText`) diseñados para aplicaciones de planes de cuotas (*installments*).
4. **Validación End-to-End**: Capacidad de validar la persistencia en Base de Datos mediante `DbManager` (JDBC) y combinar flujos API + UI.
5. **Sauce Labs Ready**: Integración con `sauce:options` y actualización automática del estado (`passed`/`failed`) directamente en el dashboard de Sauce Labs vía `Hooks.java`.
