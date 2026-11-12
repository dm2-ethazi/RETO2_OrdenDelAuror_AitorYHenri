import pytest
from unittest.mock import MagicMock
from main import ejecutar_menu


def test_menu_flujo_completo(monkeypatch):
    # 1. MOCK DE INPUTS
    # Volvemos a dejar solo las 4 entradas principales
    entradas_usuario = iter([
        "1",
        "hola mundo",
        "clave123",
        "5"  # Al procesar el "5", llamará a func_salir
    ])
    monkeypatch.setattr("builtins.input", lambda _: next(entradas_usuario))

    # 2. MOCK DE DEPENDENCIAS
    mock_lang = MagicMock()
    mock_lang.t.return_value = "Texto simulado"
    monkeypatch.setattr("main.LanguageManager", lambda _: mock_lang)
    monkeypatch.setattr("main.elegir_idioma", lambda: "es")

    mock_encrypt_text = MagicMock(return_value="TEXTO_CIFRADO")
    mock_mostrar_menu = MagicMock()

    monkeypatch.setattr("main.encrypt_text", mock_encrypt_text)
    monkeypatch.setattr("main.mostrar_menu", mock_mostrar_menu)

    # --- AQUÍ ESTÁ EL CAMBIO CLAVE ---
    # En lugar de dejar que ejecute sys.exit(), hacemos que func_salir lance
    # una excepción propia del test para romper el 'while True' de forma segura.
    def mock_salir_error(lang):
        raise KeyboardInterrupt  # Usamos una excepción estándar para frenar el bucle

    monkeypatch.setattr("main.func_salir", mock_salir_error)
    # ---------------------------------

    # 3. EJECUCIÓN (Esperamos que termine con nuestra excepción de salida)
    with pytest.raises(KeyboardInterrupt):
        ejecutar_menu()

    # 4. ASERCIONES
    # El test llegará aquí de forma segura porque capturamos la interrupción
    mock_encrypt_text.assert_called_once_with("hola mundo", "clave123", mock_lang)
