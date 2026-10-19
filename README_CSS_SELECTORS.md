# 🎯 Guía Maestra de Selectores CSS e ID para Selenium & Automatización

> **Cheat Sheet & Acordeón de Consulta Rápida**  
> Documento interactivo con todas las posibles combinaciones de selectores CSS, sintaxis por ID, atributos, combinadores, pseudo-clases y ejemplos listos para copiar en Selenium Java (`@FindBy` y `By.cssSelector`).

---

## 📑 Tabla de Contenido (Acordeón)

> Haz clic en cada sección para desplegar u ocultar los ejemplos y combinaciones.

<details>
<summary><b>1. Selectores Básicos: ID, Tag, Clase y Universal</b></summary>
<br>

Los cimientos de los selectores CSS.

| Sintaxis CSS | Descripción | Ejemplo HTML objetivo |
| :--- | :--- | :--- |
| `#id` | Elemento con un ID específico | `<input id="username">` |
| `tag#id` | Etiqueta específica con ese ID | `<input id="username">` |
| `.clase` | Elemento con una clase específica | `<button class="btn">` |
| `tag.clase` | Etiqueta específica con esa clase | `<button class="btn">` |
| `.clase1.clase2` | Elemento que contiene **ambas** clases simultáneamente | `<button class="btn btn-primary">` |
| `*` | Cualquier elemento (universal) | Todo el DOM |
| `tag` | Por nombre de etiqueta HTML | `<div>`, `<span>`, `<input>` |

#### 💡 Ejemplos Prácticos

```html
<!-- HTML de ejemplo -->
<div id="login-container" class="auth-box card-shadow">
  <input type="text" id="user-email" class="form-control required-field" placeholder="Email">
  <button id="btn-submit" class="btn btn-primary active">Ingresar</button>
</div>
```

```java
// Selenium Java:
@FindBy(css = "#user-email")
private WebElement emailPorId;

@FindBy(css = "input#user-email")
private WebElement emailPorTagEId;

@FindBy(css = ".btn.btn-primary.active")
private WebElement botonConMultiplesClases;

@FindBy(css = "div.auth-box.card-shadow")
private WebElement contenedorConTagYDosClases;
```

> **Regla de oro con clases múltiples:**  
> Si un elemento tiene `class="btn btn-primary active"`, en CSS se encadenan con puntos sin espacios: `.btn.btn-primary.active`. Si pones un espacio (`.btn .btn-primary`), buscará un hijo, no el mismo elemento.

</details>

---

<details>
<summary><b>2. Selectores de Atributos: Exactos, Parciales e IDs Dinámicos</b></summary>
<br>

Ideales cuando los IDs cambian en cada recarga (dinámicos) o para aprovechar atributos de testing (`data-testid`, `name`, `type`, `placeholder`).

| Operador | Significado | Ejemplo Selector | Coincide con... |
| :---: | :--- | :--- | :--- |
| `[attr='val']` | Valor **exacto** | `[data-testid='login-btn']` | `data-testid="login-btn"` |
| `[attr^='val']` | **Comienza con** (Prefijo) | `[id^='session_']` | `id="session_12345"`, `id="session_abc"` |
| `[attr$='val']` | **Termina con** (Sufijo) | `[id$='_input']` | `id="username_input"`, `id="987_input"` |
| `[attr*='val']` | **Contiene** la subcadena | `[class*='alert-danger']` | `class="box alert-danger-custom active"` |
| `[attr~='val']` | Contiene la palabra exacta en lista separada por espacios | `[class~='primary']` | `class="btn primary large"` |
| `[attr\|='val']` | Empieza con el valor o con `val-` | `[lang\|='es']` | `lang="es"`, `lang="es-MX"` |
| `[attr]` | El elemento tiene el atributo presente (sin importar su valor) | `input[disabled]`, `[required]` | `<input required>`, `<button disabled>` |
| `[attr='val' i]` | Insensible a mayúsculas/minúsculas | `[placeholder*='buscar' i]` | `placeholder="BUSCAR..."` |

#### 💡 Ejemplos Prácticos para IDs Dinámicos

En frameworks como Angular, React o Salesforce, los IDs suelen ser generados dinámicamente: `input-ember1234-field`, `ctrl_9823_email`.

```html
<!-- IDs generados dinámicamente -->
<input id="usr_9281736_txt" name="username" data-test="user-input" required>
<button id="btn_submit_99482" data-role="action-button">Aceptar</button>
<div id="container-random123-body">Contenido</div>
```

```java
// Selenium Java:

// Prefijo: ID que empieza con 'usr_'
@FindBy(css = "input[id^='usr_']")
private WebElement usuarioIdDinamicoPrefijo;

// Sufijo: Botón cuyo ID termina en '_txt'
@FindBy(css = "input[id$='_txt']")
private WebElement usuarioIdDinamicoSufijo;

// Contiene: Elemento cuyo ID contiene 'random'
@FindBy(css = "div[id*='random']")
private WebElement contenedorIdDinamicoContiene;

// Por atributo de prueba dedicado (Best Practice)
@FindBy(css = "[data-test='user-input']")
private WebElement usuarioPorDataTest;

// Elemento que tiene el atributo 'required'
@FindBy(css = "input[required]")
private WebElement campoRequerido;
```

</details>

---

<details>
<summary><b>3. Combinación de Múltiples Atributos simultáneos (AND lógico)</b></summary>
<br>

Cuando un solo atributo es ambiguo o se repite en la página, puedes encadenar 2 o más atributos juntos en el mismo selector.

| Sintaxis | Descripción |
| :--- | :--- |
| `tag[attr1='val1'][attr2='val2']` | Coincide si cumple la etiqueta Y ambos atributos |
| `tag#id[attr='val']` | Coincide con el ID Y el atributo adicional |
| `tag.clase[attr='val']` | Coincide con la clase Y el atributo |
| `tag.clase[attr1*='val1'][attr2^='val2']` | Clase + atributo que contiene + atributo que empieza |

#### 💡 Ejemplos Prácticos

```html
<form id="checkout-form">
  <input type="text" name="address" class="input-field" placeholder="Calle 1" data-index="1">
  <input type="text" name="address" class="input-field" placeholder="Calle 2" data-index="2">
  <input type="hidden" name="address" value="token_secret">
</form>
```

```java
// Selenium Java:

// Solo el input de tipo texto que además tenga nombre 'address' y data-index '1'
@FindBy(css = "input[type='text'][name='address'][data-index='1']")
private WebElement direccionPrincipal;

// Input con clase 'input-field' que contenga 'Calle 2' en su placeholder
@FindBy(css = "input.input-field[placeholder*='Calle 2']")
private WebElement direccionSecundaria;

// Tag + Clase + Tipo + Requerido
@FindBy(css = "input.input-field[type='text'][required]")
private WebElement campoTextoObligatorio;
```

</details>

---

<details>
<summary><b>4. Combinadores Jerárquicos: Padres, Hijos y Hermanos</b></summary>
<br>

Permiten navegar por el árbol DOM en función de la relación entre elementos.

| Símbolo | Tipo de Relación | Sintaxis | Explicación |
| :---: | :--- | :--- | :--- |
| *(Espacio)* | **Descendiente** | `A B` | `B` está en cualquier nivel dentro de `A` (hijo, nieto, bisnieto) |
| `>` | **Hijo Directo** | `A > B` | `B` es hijo inmediato de `A` (primer nivel únicamente) |
| `+` | **Hermano Adyacente** | `A + B` | `B` es el elemento inmediatamente posterior a `A` al mismo nivel |
| `~` | **Hermano General** | `A ~ B` | `B` es cualquier hermano que viene después de `A`, no necesariamente contiguo |

#### 💡 Diagrama Visual de Relaciones

```text
div#padre
 │
 ├── p.hijo-1 (Hijo directo: div#padre > p.hijo-1)
 ├── p.hijo-2 (Hermano adyacente de hijo-1: p.hijo-1 + p.hijo-2)
 ├── ul
 │    └── li.nieto (Descendiente: div#padre li.nieto)
 └── p.hijo-3 (Hermano general de hijo-1: p.hijo-1 ~ p.hijo-3)
```

#### 💡 Ejemplos Prácticos

```html
<div id="modal-content" class="modal-body">
  <h3>Título del Diálogo</h3>
  <label for="pwd">Contraseña:</label>
  <input type="password" id="pwd" class="form-input">
  <span class="error-msg">Contraseña obligatoria</span>
  
  <div class="footer-actions">
    <button class="btn btn-cancel">Cancelar</button>
    <button class="btn btn-save">Guardar</button>
  </div>
</div>
```

```java
// Selenium Java:

// Descendiente: Cualquier botón dentro del modal con ID 'modal-content'
@FindBy(css = "#modal-content button.btn-save")
private WebElement botonGuardarEnModal;

// Hijo directo: Botón que es hijo directo del footer
@FindBy(css = "div.footer-actions > button.btn-cancel")
private WebElement botonCancelarHijoDirecto;

// Hermano adyacente (+): El input que está justo después de su <label>
@FindBy(css = "label[for='pwd'] + input")
private WebElement inputInmediatoAlLabel;

// Hermano adyacente (+): Mensaje de error justo después del input
@FindBy(css = "input#pwd + span.error-msg")
private WebElement errorDespuesDePassword;

// Hermano general (~): Cualquier botón que venga después del botón cancelar
@FindBy(css = "button.btn-cancel ~ button")
private WebElement botonesPosterioresACancelar;
```

</details>

---

<details>
<summary><b>5. Pseudo-clases Estructurales e Índices: `nth-child`, `first-child`, `last-child`</b></summary>
<br>

Ideales para tablas, listas desplegables, tarjetas de productos o filas dinámicas.

| Pseudo-clase | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `:first-child` | El primer hijo de su padre | `ul#menu > li:first-child` |
| `:last-child` | El último hijo de su padre | `ul#menu > li:last-child` |
| `:only-child` | Elemento que es el único hijo de su padre | `div.alert > span:only-child` |
| `:nth-child(n)` | El hijo en la posición `n` (1-indexed) | `tr:nth-child(2)` *(Segunda fila)* |
| `:nth-child(odd)` | Todos los hijos impares (1, 3, 5...) | `tbody > tr:nth-child(odd)` |
| `:nth-child(even)` | Todos los hijos pares (2, 4, 6...) | `tbody > tr:nth-child(even)` |
| `:nth-child(2n+1)` | Fórmula algebraica de repetición | `li:nth-child(3n+1)` |
| `:nth-last-child(n)` | Cuenta desde el final hacia el principio | `tr:nth-last-child(1)` *(Última fila)* |
| `:first-of-type` | El primer elemento **de su mismo tag** | `div > p:first-of-type` |
| `:last-of-type` | El último elemento **de su mismo tag** | `div > p:last-of-type` |
| `:nth-of-type(n)` | El n-ésimo elemento **de su mismo tag** | `table td:nth-of-type(3)` *(Columna 3)* |
| `:only-of-type` | El único de su tipo de etiqueta entre hermanos | `article > h1:only-of-type` |

#### 💡 Ejemplos Prácticos con Tablas HTML

```html
<table id="users-table">
  <thead>
    <tr> <th>ID</th> <th>Nombre</th> <th>Rol</th> <th>Acciones</th> </tr>
  </thead>
  <tbody>
    <tr> <td>1</td> <td>Alice</td> <td>Admin</td> <td><button class="edit">✏️</button></td> </tr>
    <tr> <td>2</td> <td>Bob</td>   <td>User</td>  <td><button class="edit">✏️</button></td> </tr>
    <tr> <td>3</td> <td>Charlie</td> <td>User</td> <td><button class="edit">✏️</button></td> </tr>
  </tbody>
</table>
```

```java
// Selenium Java:

// Primera fila de la tabla
@FindBy(css = "#users-table tbody > tr:first-child")
private WebElement primerFilaUsuario;

// Segunda fila, columna 2 (Nombre: "Bob")
@FindBy(css = "#users-table tbody > tr:nth-child(2) > td:nth-of-type(2)")
private WebElement nombreSegundoUsuario;

// Botón editar de la última fila
@FindBy(css = "#users-table tbody > tr:last-child button.edit")
private WebElement botonEditarUltimoUsuario;

// La 3ra columna (Rol) de todas las filas
@FindBy(css = "#users-table tbody tr > td:nth-child(3)")
private List<WebElement> todosLosRoles;
```

</details>

---

<details>
<summary><b>6. Pseudo-clases de Estado de Formularios (`:enabled`, `:disabled`, `:checked`)</b></summary>
<br>

Permiten localizar elementos basándose en su estado interactivo en el navegador.

| Selector de Estado | Descripción |
| :--- | :--- |
| `:enabled` | Elementos de formulario activos y habilitados para interacción |
| `:disabled` | Elementos deshabilitados (`disabled`) |
| `:checked` | Radio buttons o Checkboxes marcados/tildados |
| `:focus` | Elemento que tiene el foco actual del teclado/cursor |
| `:required` | Inputs marcados con atributo obligatorio |
| `:optional` | Inputs sin requerimiento obligatorio |
| `:valid` / `:invalid` | Elementos con validación HTML5 aprobada o fallida |
| `:read-only` | Elementos con atributo `readonly` |

#### 💡 Ejemplos Prácticos

```html
<form id="order-form">
  <input type="checkbox" id="terms" checked>
  <input type="checkbox" id="newsletter">
  
  <input type="text" id="coupon" disabled placeholder="Cupón desactivado">
  <input type="text" id="phone" required placeholder="Teléfono (Obligatorio)">
  
  <button id="submit-btn" disabled class="btn">Pagar</button>
  <button id="reset-btn" class="btn">Limpiar</button>
</form>
```

```java
// Selenium Java:

// Checkbox que está actualmente seleccionado
@FindBy(css = "#order-form input[type='checkbox']:checked")
private WebElement checkboxSeleccionado;

// Botón que se encuentra deshabilitado
@FindBy(css = "#order-form button:disabled")
private WebElement botonDeshabilitado;

// Botón habilitado para clic
@FindBy(css = "#order-form button:enabled")
private WebElement botonHabilitado;

// Campo de texto obligatorio
@FindBy(css = "#order-form input:required")
private WebElement campoRequerido;
```

</details>

---

<details>
<summary><b>7. Pseudo-clases Lógicas: Negación `:not()` y Relacional `:has()`</b></summary>
<br>

Selectores avanzados de filtrado y navegación inversa hacia padres.

| Selector | Nombre | Función |
| :--- | :--- | :--- |
| `:not(selector)` | Negación | Excluye elementos que coincidan con el selector dentro de los paréntesis |
| `:has(selector)` | Relacional *(CSS Level 4)* | Selecciona el **padre/ancestro** que contenga a dicho hijo |
| `:is(s1, s2)` | Agrupador | Coincide con cualquiera de los selectores dados |
| `:where(s1, s2)` | Agrupador neutro | Idéntico a `:is()` pero sin aumentar especificidad |

#### 💡 La Revolución de `:has()` (Seleccionar al Padre en CSS)
> Históricamente en CSS no se podía seleccionar un elemento padre basado en sus hijos (había que recurrir a XPath `//div[..//button]`). **¡Hoy en día `:has()` ya es soportado de forma nativa en todos los navegadores modernos!**

#### 💡 Ejemplos Prácticos

```html
<div class="user-card" id="card-1">
  <h4>Juan Pérez</h4>
  <span class="badge status-pending">Pendiente</span>
  <button class="btn-approve">Aprobar</button>
</div>

<div class="user-card" id="card-2">
  <h4>María Gómez</h4>
  <span class="badge status-active">Activo</span>
  <button class="btn-delete">Eliminar</button>
</div>

<form>
  <input type="text" name="user" class="input valid">
  <input type="password" name="pass" class="input">
  <input type="hidden" name="csrf" value="12345">
</form>
```

```java
// Selenium Java:

// 1. NEGACIÓN :not()
// Todos los inputs que NO sean de tipo hidden
@FindBy(css = "form input:not([type='hidden'])")
private List<WebElement> inputsVisibles;

// Botones que NO tengan la clase 'disabled'
@FindBy(css = "button:not(.disabled)")
private List<WebElement> botonesHabilitados;

// 2. RELACIONAL :has() -> SELECCIÓN DE PADRE
// La tarjeta de usuario (.user-card) que contenga el botón con clase 'btn-delete'
@FindBy(css = ".user-card:has(button.btn-delete)")
private WebElement tarjetaUsuarioConBotonEliminar;

// La fila de una tabla que contenga el texto/estado 'Activo'
@FindBy(css = "tr:has(span.status-active)")
private WebElement filaConUsuarioActivo;

// 3. AGRUPADOR :is()
// Encabezados h1, h2 o h3 dentro del main
@FindBy(css = "main :is(h1, h2, h3)")
private List<WebElement> encabezadosPrincipales;
```

</details>

---

<details>
<summary><b>8. Catálogo de Combinaciones Complejas (Casos Reales de Automatización)</b></summary>
<br>

Aquí tienes una colección de las combinaciones más frecuentes y robustas utilizadas en pruebas end-to-end con Selenium WebDriver.

### Combinación 1: ID Dinámico + Clase + Atributo Específico
```css
/* Busca un input cuyo ID empiece con 'usr_', tenga la clase 'form-control' y sea de tipo 'email' */
input[id^='usr_'].form-control[type='email']
```

### Combinación 2: Contenedor Padre por ID + Hijo Directo + Atributo Parcial
```css
/* El botón de guardar dentro del modal específico que termina en '_container' */
div[id$='_container'] > div.modal-footer > button[data-action*='save']
```

### Combinación 3: Elemento Hermano Inmediato con Filtro Negado
```css
/* El span de alerta que está inmediatamente después de un input que NO es válido */
input:invalid + span.error-message:not(.hidden)
```

### Combinación 4: Fila Específica de Tabla por Estado del Hijo
```css
/* Selecciona el botón de descarga solo en la fila que tiene un badge de estado 'completed' */
table#orders-table tbody tr:has(span.badge-completed) button.btn-download
```

### Combinación 5: Menú Desplegable - Item N con Clase Excluida
```css
/* El tercer elemento de una lista desplegable que no esté deshabilitado */
ul.dropdown-menu > li:nth-child(3):not(.disabled) > a
```

### Combinación 6: Selector Multi-Atributo Case-Insensitive
```css
/* Buscar input por placeholder que contenga 'search' o 'SEARCH' */
input.search-input[placeholder*='search' i][type='search']
```

### Combinación 7: Padre que tiene un Checkbox Marcado
```css
/* El contenedor de fila (.item-row) cuyo checkbox interno esté tildado */
div.item-row:has(input[type='checkbox']:checked)
```

</details>

---

<details>
<summary><b>9. CSS vs XPath: Guía de Decisión Rápida</b></summary>
<br>

| Criterio | Selector CSS | XPath | Recomendación |
| :--- | :---: | :---: | :--- |
| **Velocidad de ejecución** | ⚡ Más rápido (nativo del motor CSS del navegador) | 🐢 Ligeramente más lento en navegadores antiguos | **CSS** para el 90% de los casos |
| **Legibilidad & Mantenimiento** | Limpio, compacto y estándar web | Verboso y propenso a romperse (`//div[2]/span/..`) | **CSS** |
| **IDs dinámicos (Prefijo/Sufijo)** | `[id^='x']`, `[id$='y']` | `starts-with()`, `ends-with()` | **CSS** (sintaxis más limpia) |
| **Navegar hacia arriba (Padre)** | `:has(...)` | `/..` o `parent::` o `ancestor::` | **CSS con `:has()`** o **XPath** si el motor no soporta :has() |
| **Búsqueda por Texto Visible Directo** | ❌ No soportado de forma estándar directa | `//*[text()='Ingresar']` | **XPath** (única ventaja exclusiva) |

</details>

---

<details>
<summary><b>10. Plantilla Java de Referencia para Page Objects (`BasePage`)</b></summary>
<br>

Cómo incorporar estos selectores directamente en tus clases de Página en `src/main/java/com/bdd/pages/`:

```java
package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class LoginPage extends BasePage {

    // 1. Selector por ID simple
    @FindBy(css = "#username")
    private WebElement usernameInput;

    // 2. Selector por Atributo de testing exacto
    @FindBy(css = "[data-testid='password-field']")
    private WebElement passwordInput;

    // 3. Selector con ID Dinámico (Prefijo 'btn_login_') y múltiples clases
    @FindBy(css = "button[id^='btn_login_'].btn.btn-primary")
    private WebElement submitButton;

    // 4. Selector con Negación
    @FindBy(css = ".alert-box:not(.d-none)")
    private WebElement visibleErrorMessage;

    // 5. Selector Relacional :has() para localizar la tarjeta activa
    @FindBy(css = ".account-card:has(span.badge-active)")
    private WebElement activeAccountCard;

    // 6. Lista de elementos por jerarquía e índices
    @FindBy(css = "table#results-table tbody > tr:nth-child(odd)")
    private List<WebElement> oddResultRows;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // Método con By dinámico en tiempo de ejecución
    public WebElement getRowByColumnIndex(int colIndex) {
        By dynamicCss = By.cssSelector(String.format("table tbody tr > td:nth-child(%d)", colIndex));
        return getDriver().findElement(dynamicCss);
    }

    public void enterCredentials(String user, String pass) {
        type(usernameInput, user);
        type(passwordInput, pass);
        click(submitButton);
    }
}
```

</details>

---

## 🚀 Resumen Mnemotécnico

Para recordar rápidamente los símbolos de atributos:

- `#` : **ID** (`#mi-id`)
- `.` : **Clase** (`.mi-clase`)
- `^=` : **Empieza** como el acento circunflejo al inicio (*Top / Inicio*)
- `$=` : **Termina** como el símbolo de dólar (*Final de la línea en Regex*)
- `*=` : **Contiene** como el asterisco comodín (*Wildcard*)
- `>` : **Hijo directo**
- `+` : **Hermano contiguo**
- `~` : **Hermano general**
- `:has()` : **Padre que contiene a**
- `:not()` : **Excluir elementos**
