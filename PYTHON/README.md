# Orden del Auror — Python

Implementación en Python del **Reto 02 "Orden del Auror"** (Aitor y Henri): un cifrado
[Vigenère](https://es.wikipedia.org/wiki/Cifrado_de_Vigen%C3%A8re) aplicado sobre un alfabeto
ampliado, disponible tanto para **textos introducidos por consola** como para **ficheros
completos** seleccionados mediante un cuadro de diálogo gráfico.

El cifrado repite la clave hasta igualar la longitud del texto y desplaza cada carácter según su
posición en el alfabeto, de forma que `caracter_cifrado = (índice_texto + índice_clave) % total_caracteres`
(al cifrar) y restando en lugar de sumar (al descifrar).

## Características

- Cifrado y descifrado Vigenère de **texto** por consola.
- Cifrado y descifrado de **ficheros** línea a línea (usando `tkinter` para elegir el archivo).
- Alfabeto ampliado definido en una única constante (`caracteres`): minúsculas con ñ,
  mayúsculas con ñ, dígitos y símbolos.
- Los espacios del texto original se conservan sin cifrar y no desplazan la clave.
- Interfaz de menú interactiva basada en `match/case`.

## Requisitos

- Python **3.12 o superior** (declarado en `pyproject.toml`).
- [uv](https://docs.astral.sh/uv/) (opcional, para gestionar el entorno) o `pip`.

## Instalación

Con `uv` (recomendado):

```bash
cd PYTHON
uv sync
```

Con `venv` + `pip`:

```bash
cd PYTHON
python -m venv .venv
.venv\Scripts\activate        # en Windows
pip install -e .
```

La única dependencia del proyecto es `pytest` (para los tests); el resto es librería estándar
(`sys`, `pathlib`, `tkinter`).

## Estructura del proyecto

```
PYTHON/
├── pyproject.toml              # Metadatos del paquete, dependencias y config de pytest
├── uv.lock                     # Dependencias bloqueadas (uv)
├── README.md
├── src/
│   ├── __init__.py
│   ├── main.py                 # Punto de entrada: menú principal (match/case)
│   └── utilities/
│       ├── __init__.py
│       ├── general_utils.py    # Menú, salida, alfabeto y funciones índice↔letra
│       ├── encrypt_utils.py    # Lógica de cifrado/descifrado Vigenère de textos
│       └── files_utils.py      # Cifrado/descifrado de ficheros (selector con tkinter)
└── tests/
    └── test_general_utils.py   # Tests unitarios de utilidades y cifrado
```

## Ejecución

Desde la carpeta `PYTHON/`:

```bash
python src/main.py
```

o bien entrando dentro de `src/` (los imports son `from utilities...`, por lo que el módulo
`utilities` debe estar en el `PYTHONPATH`, algo que ocurre automáticamente al ejecutar
`main.py` desde esa ubicación):

```bash
cd src
python main.py
```

## Uso

Al arrancar se muestra el menú:

```
===Orden del Auror===
1. Cifrar texto
2. Descifrar texto
3. Cifrar Archivo
4. Descifrar Archivo
5. Salir
```

- **Opción 1 / 2**: pide el texto y la clave, y muestra el resultado por consola.
- **Opción 3 / 4**: pide la clave y abre un selector de archivos para elegir el fichero.
  El resultado se guarda junto al original como `<nombre>-cifrado.<ext>` o
  `<nombre>-descifrado.<ext>` (se lee y escribe en UTF-8, línea a línea).

Ejemplo:

```text
Texto: orden del auror
Clave: hogwarts
Texto Cifrado: v7j1n ux4 h0x,r

Texto: v7j1n ux4 h0x,r
Clave: hogwarts
Texto Descifrado: orden del auror
```

## Tests

Los tests están en `tests/` y se ejecutan desde la raíz de `PYTHON/`
(`pyproject.toml` configura `pythonpath = ["."]` para que los imports `src...` funcionen):

```bash
cd PYTHON
python -m pytest
```

## Consideraciones

- Solo se pueden cifrar caracteres incluidos en la constante `caracteres` de
  `src/utilities/general_utils.py`; cualquier otro carácter lanza `ValueError` en tiempo de
  cifrado.
- El cifrado es sensible al orden y tipo de carácter del alfabeto (mayúsculas y minúsculas
  son posiciones distintas).
- Al cifrar ficheros se eliminan los espacios iniciales y finales de cada línea (`strip()`) y
  cada línea se trata de forma independiente.

## Autores

- Henri Tejada (`__author__` de los módulos)
- Aitor y Henri — Reto 02, Orden del Auror
