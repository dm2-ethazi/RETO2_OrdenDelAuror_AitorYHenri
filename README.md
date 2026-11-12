# 🪄 Orden del Auror

**Reto 02 — Desarrollo de Aplicaciones Multiplataforma**

Proyecto desarrollado por **Aitor Barros y Henri Tejada** que reúne diferentes aplicaciones para trabajar con algoritmos de cifrado, tratamiento de archivos y una experiencia interactiva basada en el universo de Harry Potter.

El proyecto está dividido en cuatro módulos independientes: Python, JavaFX, Android y Unity. Cada uno ofrece una funcionalidad diferente dentro del reto.

---

## 📌 Índice

- [Descripción del proyecto](#-descripción-del-proyecto)
- [Aplicaciones](#-aplicaciones)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Estructura del repositorio](#-estructura-del-repositorio)
- [Ramas de Git](#-ramas-de-git)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Pruebas y calidad](#-pruebas-y-calidad)
- [Documentación](#-documentación)
- [Autores](#-autores)

---

## 📖 Descripción del proyecto

Orden del Auror es un proyecto que combina programación, criptografía, desarrollo multiplataforma y videojuegos.

Su objetivo es implementar diferentes soluciones para cifrar y descifrar información, permitiendo trabajar con mensajes y archivos desde distintas plataformas.

Además, incluye un minijuego desarrollado en Unity que complementa el proyecto mediante una experiencia interactiva.

## 💻 Aplicaciones

### 1. Python — Cifrado Vigenère

Aplicación de consola que permite cifrar y descifrar textos y archivos mediante el algoritmo Vigenère.

**Funcionalidades:**
- Cifrado de mensajes introducidos por consola.
- Descifrado de mensajes.
- Cifrado y descifrado de archivos.
- Selector gráfico de archivos mediante Tkinter.
- Alfabeto ampliado con letras, números y símbolos.
- Conservación de los espacios del texto original.

**Tecnologías:** Python 3.12+, Tkinter y Pytest.

### 2. JavaFX — Aplicación de escritorio AES

Aplicación de escritorio para el tratamiento de archivos mediante cifrado y descifrado AES.

**Funcionalidades:**
- Cifrado y descifrado de archivos.
- Protección mediante contraseña.
- Procesamiento de archivos mediante streams.
- Indicador de progreso durante las operaciones.
- Interfaz gráfica con JavaFX y FXML.
- Internacionalización en español, inglés y euskera.

**Tecnologías:** Java, JavaFX 21, Maven y JUnit 5.

### 3. Android — Aplicación móvil

Aplicación móvil desarrollada en Kotlin que permite realizar operaciones de descifrado desde dispositivos Android.

**Funcionalidades:**
- Descifrado de archivos de texto mediante César.
- Descifrado de imágenes `.png.aes` mediante AES.
- Selección de archivos desde el dispositivo.
- Introducción de desplazamiento o contraseña.
- Procesamiento de archivos en segundo plano.
- Gestión de archivos mediante ContentResolver.
- Interfaz adaptada a orientación vertical y horizontal.
- Recursos en español, inglés y euskera.

**Tecnologías:** Kotlin, Android SDK, Gradle y AndroidX.

### 4. Unity — Minijuego interactivo

Videojuego desarrollado en Unity que incorpora diferentes mecánicas y elementos interactivos.

**Funcionalidades:**
- Menú principal.
- Movimiento de personajes.
- Sistema de vida del troll.
- Gestión de acertijos.
- Control de colisiones con la puerta.
- Estados de victoria y derrota.
- Reinicio de partidas.
- Sistema de idiomas en español, inglés y euskera.

**Tecnologías:** Unity 6 y C#.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Aplicación |
|---|---|
| Python 3.12+ | Cifrado Vigenère |
| Java | Aplicación de escritorio |
| JavaFX 21 | Interfaz de escritorio |
| Maven | Gestión del proyecto Java |
| Kotlin | Aplicación Android |
| Android SDK | Desarrollo móvil |
| Gradle | Construcción Android |
| C# | Desarrollo del videojuego |
| Unity 6 | Minijuego |
| Git y GitHub | Control de versiones |
| GitHub Actions | Integración continua |
| Pytest / JUnit | Pruebas automatizadas |

---

## 📁 Estructura del repositorio

```text
RETO2_OrdenDelAuror_AitorYHenri/
│
├── PYTHON/
│   ├── src/
│   │   ├── main.py
│   │   └── utilities/
│   ├── tests/
│   ├── pyproject.toml
│   └── README.md
│
├── JAVAFX/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   ├── pom.xml
│   └── mvnw
│
├── ANDROID/
│   ├── app/
│   │   └── src/
│   │       ├── main/
│   │       ├── test/
│   │       └── androidTest/
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── UNITY/
│   ├── Assets/
│   │   ├── Scripts/
│   │   ├── Localization/
│   │   └── Animations/
│   ├── Packages/
│   └── ProjectSettings/
│
├── .github/
│   └── workflows/
│
└── .pre-commit-config.yaml
```

---

## 🌿 Ramas de Git

El desarrollo se ha organizado mediante ramas independientes para separar las diferentes partes del proyecto.

| Rama | Contenido |
|---|---|
| `main` | Rama principal |
| `develop` | Rama de integración |
| `feature/python` | Desarrollo de Python |
| `feature/javafx` | Desarrollo de JavaFX |
| `feature/android` | Desarrollo de Android |
| `feature/unity` | Desarrollo de Unity |
| `feature/ci-cd` | Configuración de CI/CD |

Las funcionalidades se desarrollan en sus respectivas ramas y se integran mediante Pull Requests.

---

## ⚙️ Instalación y ejecución

Cada aplicación dispone de sus propias dependencias y requisitos.

### Python

Requisitos: Python 3.12 o superior.

```bash
cd PYTHON
python -m venv .venv
```

En Windows:

```powershell
.venv\Scripts\activate
pip install -e .
python src/main.py
```

Para ejecutar las pruebas:

```bash
python -m pytest
```

### JavaFX

Requisitos: JDK 21 y Maven.

Desde la carpeta `JAVAFX`:

```bash
mvn clean javafx:run
```

También se puede utilizar el Maven Wrapper incluido:

```powershell
.\mvnw.cmd clean javafx:run
```

### Android

Requisitos: Android Studio y Android SDK compatible con la configuración del proyecto.

1. Abrir la carpeta `ANDROID` desde Android Studio.
2. Sincronizar las dependencias de Gradle.
3. Seleccionar un emulador o dispositivo Android compatible.
4. Ejecutar la aplicación desde Android Studio.

Para generar un APK de depuración:

```powershell
.\gradlew.bat assembleDebug
```

### Unity

Requisitos: Unity 6 (versión del proyecto: 6000.3.24f1).

1. Abrir Unity Hub.
2. Seleccionar Open Project.
3. Elegir la carpeta `UNITY`.
4. Esperar a que se importen los recursos.
5. Abrir la escena principal y pulsar Play.

---

## 🧪 Pruebas y calidad

El proyecto incluye herramientas para validar el código y automatizar comprobaciones.

**Python**
- Pruebas unitarias mediante Pytest.

**Android**
- Configuración de pruebas unitarias e instrumentadas mediante JUnit y AndroidX Test.

**JavaFX**
- Dependencias de JUnit 5 configuradas en Maven.

**CI/CD**
- GitHub Actions.
- Validación de estructura del repositorio.
- Ejecución de hooks mediante pre-commit.
- Comprobaciones automáticas en eventos `push` y `pull_request`.

La incorporación de pruebas específicas y la medición de cobertura de todos los módulos forman parte de la validación del proyecto. No se declara alcanzado un porcentaje de cobertura sin un informe que lo demuestre.

---

## 📚 Documentación

La documentación técnica y funcional completa se encuentra en la sección **[Wiki del proyecto](../../wiki)**.

En ella se puede consultar:

- Arquitectura y estructura del proyecto.
- Funcionamiento de los algoritmos criptográficos.
- Documentación de cada aplicación.
- Instalación y configuración.
- Manuales de usuario.
- Decisiones técnicas.
- Pruebas y evidencias.
- Configuración de CI/CD.
- Solución de problemas.

---

## 👥 Autores

**Aitor Barros y Henri Tejada**

Proyecto realizado para el Reto 02 del ciclo formativo de Desarrollo de Aplicaciones Multiplataforma.

**Orden del Auror — 2026**