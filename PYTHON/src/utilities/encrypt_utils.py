from utilities.general_utils import index_letra, obtener_letra, lenght_chars

__author__ = "Henri Tejada"

def encrypt_text(text, key):
    """
    Funcion que encripta un texto dado usando una clave mediante el metodo de cifrado Vigenere
    :param text: Texto a cifrar
    :param key: Clave que se va a usar para cifrar
    :return: Texto cifrado Vigenere
    """
    length_text = len(text)
    length_key = len(key)
    key_final = key

    total_chars = lenght_chars()
    if length_key < length_text:
        while len(key_final) < length_text:
            key_final += key
    cont_text = 0
    cont_index = 0
    encrypted_text=""
    while cont_text<length_text:
        if text[cont_text]==" ":
            encrypted_text +=text[cont_text]
        else:
            text_index = index_letra(text[cont_text])
            key_index = index_letra(key_final[cont_index])
            encrypted_index = (text_index + key_index) % total_chars
            encrypted_text += obtener_letra(encrypted_index)
            cont_index+=1
        cont_text+=1
    return encrypted_text


def decrypt_text(text, key):
    """
    Funcion que descifra un texto dado usando una clave mediante el metodo de cifrado Vigenere
    :param text: Texto a descifrar
    :param key: Clave que se va a usar para descifrar
    :return: Texto descifrado Vigenere
    """
    length_text = len(text)
    length_key = len(key)
    key_final = key
    total_chars = lenght_chars()
    if length_key < length_text:
        while len(key_final) < length_text:
            key_final += key

    cont_text = 0
    cont_index = 0
    decrypted_text=""
    while cont_text<length_text:
        if text[cont_text]==" ":
            decrypted_text +=text[cont_text]
        else:
            text_index = index_letra(text[cont_text])
            key_index = index_letra(key_final[cont_index])
            if (text_index - key_index) >= 0:
                decrypted_index = (text_index - key_index) % total_chars
            else:
                decrypted_index = (text_index - key_index + total_chars) % total_chars
            decrypted_text +=obtener_letra(decrypted_index)
            cont_index+=1
        cont_text+=1
    return decrypted_text
