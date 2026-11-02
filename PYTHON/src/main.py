from utilities.general_utils import mostrar_menu, func_salir, elegir_idioma
from utilities.encrypt_utils import encrypt_text, decrypt_text
from utilities.files_utils import encrypt_file, decrypt_file
from utilities.LanguageManager import LanguageManager

__author__ = "Henri Tejada"
def ejecutar_menu():
    lang = LanguageManager(elegir_idioma())
    while True:
        mostrar_menu(lang)
        opcion = input(lang.t("menu_opcion"))

        match opcion:
            case "1":
                text = input(lang.t("menu_texto"))
                key = input(lang.t("menu_key"))
                try:
                    print(f'{lang.t("menu_txt_cifr")}{encrypt_text(text,key,lang)}')
                except ValueError as e:
                    print(e)

            case "2":
                text = input(lang.t("menu_texto"))
                key = input(lang.t("menu_key"))
                try:
                    print(f'{lang.t("menu_txt_descifr")}{decrypt_text(text, key, lang)}')
                except ValueError as e:
                    print(e)

            case "3":
                key = input(lang.t("menu_key"))
                try:
                    encrypt_file(key,lang)
                except ValueError as e:
                    print(e)

            case "4":
                key = input(lang.t("menu_key"))
                try:
                    decrypt_file(key,lang)
                except ValueError as e:
                    print(e)

            case "5":
                func_salir(lang)

            case _:
                print(lang.t("menu_opc_inv"))


if __name__ == "__main__":
    ejecutar_menu()