# Configuración inicial de GitHub y calidad del código — Reto 2

## 1. Objetivo

Documentar la configuración inicial del repositorio para mejorar el flujo de trabajo del equipo, proteger las ramas principales y automatizar comprobaciones de calidad del código.

Se han trabajado las siguientes configuraciones:

- GitHub Actions para la integración continua (CI).
- Protección de las ramas `main` y `develop`.
- Hooks locales de `pre-commit`.
- Hook local de `pre-push`.
- Control de versiones mediante Git y commits convencionales.

## 2. Ramas utilizadas

Las ramas existentes en el repositorio son:

- `main`: rama principal del proyecto.
- `develop`: rama de integración del equipo.
- `feature/unity`: rama de desarrollo de Unity.
- `feature/ci-cd`: rama utilizada para configurar GitHub Actions y los hooks de Git.

Para consultar las ramas locales:

```powershell
git branch
```

Para consultar las ramas remotas:

```powershell
git branch -r
```

## 3. Configuración de GitHub Actions

Se ha creado el archivo:

```text
.github/workflows/tests.yml
```

El workflow se activa mediante los eventos `push` y `pull_request`.

La configuración inicial incluye dos trabajos:

- **Tests de Python:** preparado para instalar dependencias y ejecutar pruebas con `pytest`.
- **Tests de Java:** preparado para ejecutar pruebas mediante Maven.

El objetivo es automatizar las comprobaciones cuando se suben cambios o se actualizan las Pull Requests.

### Estado actual

El workflow se ha ejecutado en GitHub Actions, pero los trabajos han fallado porque las rutas configuradas para los proyectos de Python y Java no coinciden con la estructura real del repositorio.

Por tanto, queda pendiente adaptar las rutas y comprobar la ejecución de los tests reales.

También queda pendiente configurar los tests de Unity y comprobar la cobertura mínima del 80 % exigida por el pliego.

## 4. Protección de ramas en GitHub

Se han creado dos rulesets desde:

**Settings → Rules → Rulesets**

### 4.1. Proteger main

Nombre de la regla: `Proteger main`

Rama protegida: `main`

Reglas activadas:

- `Restrict deletions`: impide eliminar la rama.
- `Require a pull request before merging`: exige una Pull Request para integrar cambios.
- `Block force pushes`: bloquea los envíos forzados.

### 4.2. Proteger develop

Nombre de la regla: `Proteger develop`

Rama protegida: `develop`

Se han activado las mismas tres reglas que en `main`.

La opción `Require status checks to pass` se ha dejado desactivada temporalmente porque el workflow todavía no funciona correctamente.

## 5. Instalación de pre-commit

Se ha instalado la herramienta `pre-commit` mediante Python:

```powershell
pip install pre-commit
```

En este equipo, el comando `pre-commit` no se reconoce directamente porque su directorio de instalación no está añadido al `PATH`.

Se ha comprobado que funciona ejecutándolo mediante Python:

```powershell
py -3.14 -m pre_commit --version
```

Resultado obtenido:

```text
pre-commit 4.7.0
```

## 6. Configuración de pre-commit

Se ha creado el archivo `.pre-commit-config.yaml` en la raíz del repositorio.

Contenido:

```yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v5.0.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-merge-conflict
```

### Función de los hooks

| Hook                   | Función                                               |
| ---------------------- | ----------------------------------------------------- |
| `trailing-whitespace`  | Elimina espacios innecesarios al final de las líneas. |
| `end-of-file-fixer`    | Asegura que los archivos terminen correctamente.      |
| `check-yaml`           | Comprueba la sintaxis de los archivos YAML.           |
| `check-merge-conflict` | Detecta marcadores de conflictos de Git.              |

### Instalación y ejecución

Para instalar el hook en el repositorio local:

```powershell
py -3.14 -m pre_commit install
```

Para ejecutar las comprobaciones en todos los archivos:

```powershell
py -3.14 -m pre_commit run --all-files
```

Durante la primera ejecución se corrigieron automáticamente espacios y finales de archivo en varios archivos del proyecto. Después, los hooks pasaron correctamente al ejecutarse durante el commit de configuración.

Estos hooks realizan comprobaciones de formato y sintaxis, pero todavía no ejecutan los tests unitarios.

## 7. Configuración del hook pre-push

Se ha creado el archivo local:

```text
.git/hooks/pre-push
```

Su contenido es:

```sh
#!/bin/sh
branch=$(git symbolic-ref --short HEAD 2>/dev/null)

if [ "$branch" = "main" ] || [ "$branch" = "develop" ]; then
    echo "ERROR: No se permite hacer push directo a $branch."
    echo "Crea una rama de trabajo y utiliza una Pull Request."
    exit 1
fi

exit 0
```

Este hook comprueba la rama actual antes de realizar un `push`.

Si la rama es `main` o `develop`, el hook cancela la operación. En las demás ramas permite continuar.

**Importante:** el archivo está dentro de `.git/hooks`, por lo que no se incluye en los commits ni se comparte automáticamente con el resto del equipo. Cada integrante debe configurarlo en su copia local del repositorio.

La protección del servidor sigue dependiendo de los rulesets de GitHub.

## 8. Commits realizados

### 8.1. Configuración inicial de CI/CD

Mensaje utilizado:

```text
CHORE: configurar pipeline CI/CD inicial
```

### 8.2. Configuración de pre-commit

Mensaje utilizado:

```text
CHORE: configura hooks pre-commit
```

El segundo commit quedó registrado con el identificador abreviado:

```text
feb36299
```

Ambos cambios se han trabajado en la rama `feature/ci-cd`.

## 9. Comandos utilizados

### Consultar el estado del repositorio

```powershell
git status
```

### Consultar las ramas

```powershell
git branch
git branch -r
```

### Preparar y guardar los cambios de configuración

```powershell
git add .pre-commit-config.yaml
git commit -m "chore: configura hooks pre-commit"
```

### Subir la rama a GitHub

```powershell
git push origin feature/ci-cd
```

El último envío comprobado devolvió:

```text
Everything up-to-date
```

Esto indica que la rama local y la remota estaban sincronizadas en ese momento.

## 10. Tareas pendientes

- [ ] Corregir las rutas del workflow de GitHub Actions.
- [ ] Comprobar que los tests de Python se ejecutan correctamente.
- [ ] Comprobar que los tests de Java se ejecutan correctamente.
- [ ] Configurar la ejecución automatizada de tests de Unity.
- [ ] Configurar y verificar la cobertura mínima del 80 %.
- [ ] Hacer que las comprobaciones se ejecuten correctamente en las Pull Requests.
- [ ] Activar los controles de estado obligatorios cuando el workflow funcione.
- [ ] Crear y revisar la Pull Request de `feature/ci-cd` hacia `develop`.
- [ ] Compartir las instrucciones de instalación de los hooks con el resto del equipo.
- [ ] Tomar capturas de pantalla como evidencia de las configuraciones realizadas.

## 11. Conclusión

Se han configurado las reglas de protección de `main` y `develop`, se ha creado un workflow inicial de GitHub Actions y se han instalado hooks locales para mejorar la calidad del código.

La configuración inicial está subida a la rama `feature/ci-cd`. Antes de considerar terminada la integración continua, es necesario corregir las rutas de los proyectos y verificar que las pruebas automatizadas cumplen los requisitos del pliego.
