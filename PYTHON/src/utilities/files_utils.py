from pathlib import Path
from tkinter import Tk, filedialog
from utilities.encrypt_utils import encrypt_text, decrypt_text
import os

__author__ = "Henri Tejada"

def encrypt_file(key,lang):
    """
        Funcion que cifra un archivo dado mediante el método Vigenere
    :param key: Clave con la que se cifra el archivo seleccionado
    :param lang: idioma seleccionado por el usuario
    """
    root = Tk()
    root.withdraw()

    ruta = filedialog.askopenfilename()
    cont_linea=1

    if ruta:
        original = Path(ruta)

        print(f'{lang.t("fl_utl_cifr_arch")}{original.name}')

        salida = (
            original.parent /
            f"{original.stem}-cifrado{original.suffix}"
        )

        with open(original, "r", encoding="utf-8") as entrada, \
             open(salida, "w", encoding="utf-8") as salida_file:

            for linea in entrada:

                try:
                    salida_file.write(
                        encrypt_text(linea.strip(), key) + "\n"
                    )
                    print(f'{lang.t("fl_utl_cifr_line")}{cont_linea}')
                except:
                    raise ValueError(f'{lang.t("fl_utl_cifr_error_char_pt1")}{cont_linea}'
                                     f'{lang.t("fl_utl_cifr_error_char_pt2")}')
                cont_linea += 1


def decrypt_file(key,lang):
    """
        Funcion que descifra un archivo dado mediante el método Vigenere
    :param key: Clave con la que se descifra el archivo seleccionado
    :param lang: idioma seleccionado por el usuario
    """
    root = Tk()
    root.withdraw()

    ruta = filedialog.askopenfilename()
    cont_linea=1
    if ruta:
        original = Path(ruta)

        print(f'{lang.t("fl_utl_descifr_arch")}{original.name}')

        salida = (
            original.parent /
            f"{original.stem}-descifrado{original.suffix}"
        )

        with open(original, "r", encoding="utf-8") as entrada, \
             open(salida, "w", encoding="utf-8") as salida_file:

            for linea in entrada:
                try:
                    salida_file.write(
                        decrypt_text(linea.strip(), key) + "\n"
                    )
                    print(f'{lang.t("fl_utl_descifr_line")}{cont_linea}')
                except:
                    raise ValueError(f'{lang.t("fl_utl_descifr_error_char_pt1")}{cont_linea}'
                                     f'{lang.t("fl_utl_descifr_error_char_pt2")}')
                cont_linea += 1