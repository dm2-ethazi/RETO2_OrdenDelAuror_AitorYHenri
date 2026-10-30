from pathlib import Path
from tkinter import Tk, filedialog
from utilities.encrypt_utils import encrypt_text, decrypt_text
import os

__author__ = "Henri Tejada"

def encrypt_file(key):
    """
        Funcion que cifra un archivo dado mediante el método Vigenere
    :param key: Clave con la que se cifra el archivo seleccionado
    """
    root = Tk()
    root.withdraw()

    ruta = filedialog.askopenfilename()
    cont_linea=1

    if ruta:
        original = Path(ruta)

        print(f'Cifrando archivo: {original.name}')

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
                    print(f'Cifrando Linea: {cont_linea}')
                except:
                    raise ValueError(f'El archivo no se puede cifrar,debido a que la linea {cont_linea} '
                                     f'tiene un carácter no valido, se ha detenido la operación')
                cont_linea += 1


def decrypt_file(key):
    """
        Funcion que descifra un archivo dado mediante el método Vigenere
    :param key: Clave con la que se descifra el archivo seleccionado
    """
    root = Tk()
    root.withdraw()

    ruta = filedialog.askopenfilename()
    cont_linea=1
    if ruta:
        original = Path(ruta)

        print(f'descifrando archivo: {original.name}')

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
                    print(f'Descrifrando Linea: {cont_linea}')
                except:
                    raise ValueError(f'El archivo no se puede descifrar,debido a que la linea {cont_linea} '
                                     f'tiene un carácter no valido, se ha detenido la operación')
                cont_linea += 1