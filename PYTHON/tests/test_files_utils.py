import pytest

from src.utilities.files_utils import encrypt_file, decrypt_file
from src.utilities.LanguageManager import LanguageManager
from unittest.mock import MagicMock


def test_encrypt_file_exito(monkeypatch, tmp_path):
    """Prueba que un archivo se cifre correctamente generando su archivo de salida."""
    lang = LanguageManager()
    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO", encoding="utf-8")

    # Definimos la ruta esperada del archivo cifrado que creará tu función
    archivo_salida_esperado = tmp_path / "documento-cifrado.txt"

    # 2. SIMULACIÓN (Mocking): Forzamos a askopenfilename a devolver nuestro archivo falso
    # Pasamos un string porque askopenfilename habitualmente retorna un str con la ruta
    monkeypatch.setattr(
        "tkinter.filedialog.askopenfilename",
        lambda: str(archivo_origen)
    )

    # También simulamos 'Tk()' y 'root.withdraw()' para evitar errores de interfaz gráfica
    monkeypatch.setattr("tkinter.Tk", lambda: MagicMock())

    # Creamos un objeto mock simulado para el objeto 'lang' y su método '.t()'
    # Esto evita fallos al intentar traducir los prints en tu función

    # 3. EJECUCIÓN: Llamamos a tu función pasando una clave y nuestro mock de idioma
    encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)


def test_encrypt_file_fallo(monkeypatch, tmp_path):
    """Prueba que un archivo se cifre correctamente generando su archivo de salida."""
    lang = LanguageManager()
    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA""MUNDO', encoding="utf-8")

    # Definimos la ruta esperada del archivo cifrado que creará tu función
    archivo_salida_esperado = tmp_path / "documento-cifrado.txt"

    # 2. SIMULACIÓN (Mocking): Forzamos a askopenfilename a devolver nuestro archivo falso
    # Pasamos un string porque askopenfilename habitualmente retorna un str con la ruta
    monkeypatch.setattr(
        "tkinter.filedialog.askopenfilename",
        lambda: str(archivo_origen)
    )

    # También simulamos 'Tk()' y 'root.withdraw()' para evitar errores de interfaz gráfica
    monkeypatch.setattr("tkinter.Tk", lambda: MagicMock())

    # Creamos un objeto mock simulado para el objeto 'lang' y su método '.t()'
    # Esto evita fallos al intentar traducir los prints en tu función

    # 3. EJECUCIÓN: Llamamos a tu función pasando una clave y nuestro mock de idioma
    with pytest.raises(ValueError):
        encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)


def test_decrypt_file_exito(monkeypatch, tmp_path):
    """Prueba que un archivo se cifre correctamente generando su archivo de salida."""
    lang = LanguageManager()
    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO", encoding="utf-8")

    # Definimos la ruta esperada del archivo cifrado que creará tu función
    archivo_salida_esperado = tmp_path / "documento-cifrado.txt"

    # 2. SIMULACIÓN (Mocking): Forzamos a askopenfilename a devolver nuestro archivo falso
    # Pasamos un string porque askopenfilename habitualmente retorna un str con la ruta
    monkeypatch.setattr(
        "tkinter.filedialog.askopenfilename",
        lambda: str(archivo_origen)
    )

    # También simulamos 'Tk()' y 'root.withdraw()' para evitar errores de interfaz gráfica
    monkeypatch.setattr("tkinter.Tk", lambda: MagicMock())

    # Creamos un objeto mock simulado para el objeto 'lang' y su método '.t()'
    # Esto evita fallos al intentar traducir los prints en tu función

    # 3. EJECUCIÓN: Llamamos a tu función pasando una clave y nuestro mock de idioma
    decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)


def test_decrypt_file_fallo(monkeypatch, tmp_path):
    """Prueba que un archivo se cifre correctamente generando su archivo de salida."""
    lang = LanguageManager()
    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA""MUNDO', encoding="utf-8")

    # Definimos la ruta esperada del archivo cifrado que creará tu función
    archivo_salida_esperado = tmp_path / "documento-cifrado.txt"

    # 2. SIMULACIÓN (Mocking): Forzamos a askopenfilename a devolver nuestro archivo falso
    # Pasamos un string porque askopenfilename habitualmente retorna un str con la ruta
    monkeypatch.setattr(
        "tkinter.filedialog.askopenfilename",
        lambda: str(archivo_origen)
    )

    # También simulamos 'Tk()' y 'root.withdraw()' para evitar errores de interfaz gráfica
    monkeypatch.setattr("tkinter.Tk", lambda: MagicMock())

    # Creamos un objeto mock simulado para el objeto 'lang' y su método '.t()'
    # Esto evita fallos al intentar traducir los prints en tu función

    # 3. EJECUCIÓN: Llamamos a tu función pasando una clave y nuestro mock de idioma
    with pytest.raises(ValueError):
        decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)