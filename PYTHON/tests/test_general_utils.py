from src.utilities.general_utils import index_letra, obtener_letra
from src.utilities.encrypt_utils import encrypt_text, decrypt_text

def test_index_letra():
    resultado = index_letra("a")
    assert resultado == 0


def test_obtener_letra():
    resultado = obtener_letra(0)
    assert resultado == "a"


def test_encrypt_text():
    resultado = encrypt_text("text_test", "key_test")
    assert resultado == "4i¨//xw.4"


def test_decrypt_text():
    resultado = decrypt_text("4i¨//xw.4", "key_test")
    assert resultado == "text_test"