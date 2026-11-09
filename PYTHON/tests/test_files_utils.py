import pytest

from src.utilities.files_utils import encrypt_file, decrypt_file
from src.utilities.encrypt_utils import encrypt_text, decrypt_text
from src.utilities.LanguageManager import LanguageManager
from unittest.mock import MagicMock


def simular_dialogo(monkeypatch, archivo):
    """
    Simula la ventana Tk y el diálogo de selección de archivos
    para probar las funciones sin abrir ventanas gráficas reales.
    :param archivo: ruta del archivo que "selecciona" el usuario ("" = cancela)
    """
    monkeypatch.setattr(
        "src.utilities.files_utils.filedialog.askopenfilename",
        lambda: str(archivo)
    )
    monkeypatch.setattr(
        "src.utilities.files_utils.Tk",
        lambda: MagicMock()
    )


def test_encrypt_file_exito(monkeypatch, tmp_path, capsys):
    """Prueba que un archivo se cifre correctamente generando su archivo de salida."""
    lang = LanguageManager()

    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO\n", encoding="utf-8")

    # 2. SIMULACIÓN (Mocking): el diálogo "selecciona" nuestro archivo falso
    simular_dialogo(monkeypatch, archivo_origen)

    # 3. EJECUCIÓN: ciframos el archivo
    encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    # 4. VERIFICACIÓN: existe el archivo de salida y contiene el texto cifrado
    archivo_salida = tmp_path / "documento-cifrado.txt"
    assert archivo_salida.exists()

    esperado = encrypt_text("HOLA MUNDO", "MI_CLAVE_VIGENERE", lang) + "\n"
    assert archivo_salida.read_text(encoding="utf-8") == esperado

    # Comprobamos también los mensajes que se imprimen por pantalla
    salida = capsys.readouterr().out
    assert "Cifrando Archivo: documento.txt" in salida
    assert "Cifrando Linea: 1" in salida


def test_encrypt_file_fallo(monkeypatch, tmp_path):
    """Prueba que un carácter no compatible con el cifrado lance ValueError indicando la línea."""
    lang = LanguageManager()

    # El carácter '"' no está en la lista de caracteres compatibles
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA""MUNDO\n', encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)

    with pytest.raises(ValueError) as excinfo:
        encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    # El mensaje debe indicar en qué línea está el carácter inválido
    assert "la linea 1" in str(excinfo.value)


def test_decrypt_file_exito(monkeypatch, tmp_path, capsys):
    """Prueba que un archivo se descifre correctamente generando su archivo de salida."""
    lang = LanguageManager()

    # 1. PREPARACIÓN: Crear un archivo de origen falso en un directorio temporal
    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO\n", encoding="utf-8")

    # 2. SIMULACIÓN (Mocking): el diálogo "selecciona" nuestro archivo falso
    simular_dialogo(monkeypatch, archivo_origen)

    # 3. EJECUCIÓN: desciframos el archivo
    decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    # 4. VERIFICACIÓN: existe el archivo de salida y contiene el texto descifrado
    archivo_salida = tmp_path / "documento-descifrado.txt"
    assert archivo_salida.exists()

    esperado = decrypt_text("HOLA MUNDO", "MI_CLAVE_VIGENERE", lang) + "\n"
    assert archivo_salida.read_text(encoding="utf-8") == esperado

    salida = capsys.readouterr().out
    assert "Descifrando Archivo: documento.txt" in salida
    assert "Descifrando Linea: 1" in salida


def test_decrypt_file_fallo(monkeypatch, tmp_path):
    """Prueba que un carácter no compatible con el descifrado lance ValueError indicando la línea."""
    lang = LanguageManager()

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA""MUNDO\n', encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)

    with pytest.raises(ValueError) as excinfo:
        decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    assert "la linea 1" in str(excinfo.value)


def test_archivo_no_seleccionado(monkeypatch, tmp_path):
    """Prueba que si el usuario cancela el diálogo no se cree ningún archivo."""
    lang = LanguageManager()

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO\n", encoding="utf-8")

    # El usuario cancela el diálogo: askopenfilename devuelve ""
    simular_dialogo(monkeypatch, "")

    encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)
    decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    assert list(tmp_path.glob("*-cifrado*")) == []
    assert list(tmp_path.glob("*-descifrado*")) == []


def test_varias_lineas(monkeypatch, tmp_path, capsys):
    """Prueba que un archivo con varias líneas se cifre línea a línea."""
    lang = LanguageManager()

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO\nADIOS MUNDO\n", encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)

    encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    lineas = (
        tmp_path / "documento-cifrado.txt"
    ).read_text(encoding="utf-8").splitlines()

    assert len(lineas) == 2
    assert lineas[0] == encrypt_text("HOLA MUNDO", "MI_CLAVE_VIGENERE", lang)
    assert lineas[1] == encrypt_text("ADIOS MUNDO", "MI_CLAVE_VIGENERE", lang)

    salida = capsys.readouterr().out
    assert "Cifrando Linea: 1" in salida
    assert "Cifrando Linea: 2" in salida


def test_error_en_segunda_linea(monkeypatch, tmp_path):
    """Prueba que el mensaje de error indique la línea exacta donde está el carácter inválido."""
    lang = LanguageManager()

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA MUNDO\nBAD"LINEA\n', encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)

    with pytest.raises(ValueError) as excinfo:
        encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    assert "la linea 2" in str(excinfo.value)


def test_ida_y_vuelta(monkeypatch, tmp_path):
    """Prueba que cifrar y volver a descifrar devuelva el contenido original."""
    lang = LanguageManager()

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text("HOLA MUNDO\n", encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)
    encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    archivo_cifrado = tmp_path / "documento-cifrado.txt"
    simular_dialogo(monkeypatch, archivo_cifrado)
    decrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    archivo_descifrado = tmp_path / "documento-cifrado-descifrado.txt"
    assert archivo_descifrado.read_text(encoding="utf-8") == "HOLA MUNDO\n"


def test_encrypt_file_error_eus(monkeypatch, tmp_path):
    """Prueba que las claves de traducción eus existen en eus.json (no se muestra la clave cruda)."""
    lang = LanguageManager("eus")

    archivo_origen = tmp_path / "documento.txt"
    archivo_origen.write_text('HOLA""MUNDO\n', encoding="utf-8")

    simular_dialogo(monkeypatch, archivo_origen)

    with pytest.raises(ValueError) as excinfo:
        encrypt_file(key="MI_CLAVE_VIGENERE", lang=lang)

    mensaje = str(excinfo.value)
    assert "lerroak" in mensaje  # traducción real de fl_utl_cifr_error_char_pt2
    assert "fl_utl_cifr_error_char_pt1" not in mensaje  # sin claves sin traducir
