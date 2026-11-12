from src.utilities.encrypt_utils import encrypt_text, decrypt_text


def test_encrypt_text():
    resultado = encrypt_text("text_test", "key_test","es")
    assert resultado == "4i¨//xw.4"


def test_encrypt_text_espacios():
    resultado = encrypt_text("text test", "key test","es")
    assert resultado == "4i¨- xw.4"


def test_decrypt_text():
    resultado = decrypt_text("4i¨//xw.4", "key_test","es")
    assert resultado == "text_test"


def test_decrypt_text_espacios():
    resultado = decrypt_text("4i¨- xw.4", "key test","es")
    assert resultado == "text test"


def test_decrypt_text_key_ind_mayor():
    resultado = decrypt_text("a","o","es")
    assert resultado == "Á"