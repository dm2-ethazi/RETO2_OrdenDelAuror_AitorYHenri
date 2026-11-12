from src.utilities.general_utils import index_letra, obtener_letra, length_chars, mostrar_menu, func_salir, elegir_idioma
from src.utilities.LanguageManager import LanguageManager
import pytest


def test_mostrar_menu(capsys):
    lang = LanguageManager()
    mostrar_menu(lang)

    salida = capsys.readouterr().out

    assert "===Orden del Auror===" in salida
    assert "1. Cifrar texto" in salida
    assert "2. Descifrar texto" in salida
    assert "3. Cifrar archivo" in salida
    assert "4. Descifrar archivo" in salida
    assert "5. Salir" in salida


def test_func_salir(capsys):
    lang = LanguageManager()

    with pytest.raises(SystemExit):
        func_salir(lang)

    salida = capsys.readouterr().out

    assert "Saliendo..." in salida


def test_length_chars():
    resultado = length_chars()
    assert resultado == 133


def test_index_letra():
    lang = LanguageManager()
    resultado = index_letra("a",lang)
    assert resultado == 0


def test_index_letra_error():
    lang = LanguageManager()
    with pytest.raises(ValueError):
        index_letra('"',lang)


def test_obtener_letra():
    lang = LanguageManager()
    resultado = obtener_letra(0,lang)
    assert resultado == "a"


def test_obtener_letra_error_eus():
    lang = LanguageManager("eus")
    with pytest.raises(ValueError):
        obtener_letra(200,lang)


def test_obtener_letra_error():
    lang = LanguageManager()
    with pytest.raises(ValueError):
        obtener_letra(200,lang)


def test_elegir_idioma_valido_directo(monkeypatch):
    """Prueba que un idioma válido se retorne correctamente a la primera."""
    # Simula que el usuario escribe 'es'
    monkeypatch.setattr('builtins.input', lambda _: 'es')

    resultado = elegir_idioma()
    assert resultado == 'es'


def test_elegir_idioma_con_reintento(monkeypatch):
    """Prueba que si introduce algo inválido, el ciclo 'while' funcione hasta recibir uno válido."""
    # Creamos un iterador para simular múltiples pulsaciones de Enter:
    # 1º intento: 'fr' (inválido) -> Entra al ciclo while
    # 2º intento: 'en' (válido) -> Termina el ciclo
    respuestas = iter(['fr', 'en'])
    monkeypatch.setattr('builtins.input', lambda _: next(respuestas))

    resultado = elegir_idioma()
    assert resultado == 'en'