# ⚜️ Orden del Auror — JavaFX

Aplicación de escritorio desarrollada con **JavaFX** para cifrar y descifrar archivos de forma segura mediante el algoritmo **AES**. Proyecto del **Reto 02** — Aitor & Henri.

---

## 📋 Índice

- [Descripción](#-descripción)
- [Características](#-características)
- [Requisitos previos](#-requisitos-previos)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Compilación y ejecución](#-compilación-y-ejecución)
- [Atajos de teclado](#-atajos-de-teclado)
- [Detalles criptográficos](#-detalles-criptográficos)
- [Internacionalización (i18n)](#-internacionalización-i18n)
- [Sistema de logging](#-sistema-de-logging)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)

---

## 📝 Descripción

**Orden del Auror** permite seleccionar un archivo del sistema, introducir una contraseña y cifrarlo o descifrarlo con AES-256 en modo CBC. La aplicación muestra el progreso de la operación en tiempo real mediante una barra de progreso, sin bloquear la interfaz gráfica (las operaciones se ejecutan en hilos separados usando `javafx.concurrent.Task`).

---

## ✨ Características

- **Cifrado / descifrado de archivos** con AES-256 (CBC + PKCS5Padding).
- **Barra de progreso** con porcentaje de avance en tiempo real.
- **Interfaz FXML** con diseño limpio y personalizable mediante CSS.
- **Multilingüe**: Español (es), English (en) y Euskara (eus), con cambio en caliente desde el desplegable.
- **Selección de archivos** mediante `FileChooser`.
- **Validaciones** con alertas: archivo no seleccionado, contraseña vacía, archivo no `.aes` al descifrar.
- **Atajos de teclado** para todas las acciones principales.
- **Logging** con SLF4J + Logback (consola y ficheros, con rotación por tamaño).
- **Ventana** con tamaño mínimo 500×500 y máximo 650×650.

---

## 🔧 Requisitos previos

- **JDK 21** (o superior)
- **Maven 3.8+** (o usar el wrapper incluido: `mvnw` / `mvnw.cmd`)
- No es necesario instalar JavaFX aparte: se descarga automáticamente como dependencia Maven (`javafx-controls` y `javafx-fxml` 21.0.6)

---

## 📂 Estructura del proyecto

```
JAVAFX/
├── pom.xml                         # Configuración Maven
├── mvnw / mvnw.cmd                 # Maven Wrapper
└── src/main/
    ├── java/com/ordenDelAuror/aescipher/
    │   ├── Main.java               # Punto de entrada (Application JavaFX)
    │   ├── controller/
    │   │   └── MainController.java # Lógica de la interfaz (FXML)
    │   ├── crypto/
    │   │   ├── AESService.java     # Servicio de cifrado/descifrado AES
    │   │   └── ProgressCallback.java # Callback de progreso
    │   └── utils/
    │       ├── AlertUtils.java     # Utilidades de alertas
    │       ├── LanguageManager.java # Gestor de idiomas (ResourceBundle)
    │       └── LanguageOption.java  # Opción de idioma (ComboBox)
    └── resources/
        ├── view/main-view.fxml     # Vista principal
        ├── css/style.css           # Estilos de la aplicación
        ├── lang/
        │   ├── messages_es.properties
        │   ├── messages_en.properties
        │   └── messages_eus.properties
        ├── images/icon.png         # Icono de la aplicación
        └── logback.xml             # Configuración de logging
```

---

## 🚀 Compilación y ejecución

### Ejecutar la aplicación

```bash
# Con el wrapper de Maven (recomendado)
./mvnw clean javafx:run        # Linux/macOS
mvnw.cmd clean javafx:run      # Windows

# Con Maven instalado
mvn clean javafx:run
```

### Compilar

```bash
./mvnw clean package
```

### Ejecutar los tests

```bash
./mvnw test
```

---

## ⌨️ Atajos de teclado

| Atajo         | Acción            |
|---------------|-------------------|
| `Ctrl + O`    | Seleccionar archivo |
| `Ctrl + E`    | Cifrar archivo    |
| `Ctrl + D`    | Descifrar archivo |
| `F1`          | Mostrar información |

---

## 🔐 Detalles criptográficos

| Parámetro            | Valor                          |
|----------------------|--------------------------------|
| Algoritmo            | `AES/CBC/PKCS5Padding`         |
| Tamaño de clave      | 256 bits                       |
| KDF                  | `PBKDF2WithHmacSHA256`         |
| Iteraciones PBKDF2   | 65.536                         |
| Salt                 | 16 bytes (aleatoria)           |
| IV                   | 16 bytes (aleatorio)           |
| Tamaño de buffer     | 8.192 bytes                    |

**Formato del archivo cifrado (`.aes`):**

```
[salt (16 B)] [IV (16 B)] [contenido cifrado]
```

> ⚠️ El archivo cifrado se guarda junto al original con la extensión `.aes`. Para descifrarlo, seleccione el archivo `.aes` e introduzca la misma contraseña. Se recomienda no perder la contraseña: sin ella el archivo no puede recuperarse.

---

## 🌐 Internacionalización (i18n)

La traducción se gestiona con `ResourceBundle` (`lang.messages`) y tres ficheros de propiedades:

- `messages_es.properties` — Español (idioma por defecto)
- `messages_en.properties` — English
- `messages_eus.properties` — Euskara

El cambio de idioma se realiza en tiempo real desde el ComboBox de la interfaz, actualizando todos los textos, tooltips y alertas mediante `LanguageManager.setLanguage()` y `updateTexts()`.

---

## 📜 Sistema de logging

Configurado con **SLF4J** + **Logback** (`src/main/resources/logback.xml`), con tres appenders:

| Appender       | Destino                | Detalle                                    |
|----------------|------------------------|--------------------------------------------|
| `CONSOLE`      | Consola                | Patrón con fecha y mensaje                 |
| `FILE_GIGANTE` | `registroDeApp.log`    | Archivo único con todas las trazas         |
| `FILE_ROLLER`  | `registroDeApp1MB.log` | Rotación por tamaño: máximo **1 MB** por archivo (`registroDeApp1MB.%i.log`) |

El logger `com.ordenDelAuror` registra a nivel `INFO`: selección de archivos, cambios de idioma, cifrados/descifrados correctos, avisos de validación y errores.

---

## 🛠️ Tecnologías utilizadas

| Tecnología        | Versión   | Uso                          |
|-------------------|-----------|------------------------------|
| Java              | 21        | Lenguaje de programación     |
| JavaFX            | 21.0.6    | Interfaz gráfica (controls, fxml) |
| Maven             | 3.8+      | Gestión de dependencias y build |
| JUnit Jupiter     | 5.12.1    | Tests unitarios              |
| SLF4J             | 2.0.13    | API de logging               |
| Logback           | 1.5.6     | Implementación de logging    |
| Commons Logging   | 1.3.0     | Compatibilidad de logging    |
