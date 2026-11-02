import sys

__author__ = "Henri Tejada"

caracteres = ("abcdefghijklmnñopqrstuvwxyz1234567890<,.->;:_´ç{}¨`+[]^*º\ª!·$%&/()=?¿|@#~€¬áéíóúàèìòùäëïöüABCDEFGHIJKL"
              "MNÑOPQRSTUVWXYZÁÉÍÓÚÀÈÌÒÙÄËÏÖÜ")


def mostrar_menu(lang):
    """
        Funcion que permite mostrar el menu
    """
    print("\n===Orden del Auror===")
    print(lang.t("menu_cifrar_text"))
    print(lang.t("menu_descifrar_text"))
    print(lang.t("menu_cifrar_arch"))
    print(lang.t("menu_descifrar_arch"))
    print(lang.t("menu_salir"))

def func_salir(lang):
    """
        Funcion que permite cerrar la aplicacion desde el menu
    :param lang: idioma seleccionado por el usuario
    """
    print(lang.t("salir_msg"))
    sys.exit()

def lenght_chars():
    """
        Funcion que devuelve la cantidad de caracteres compatibles con el cifrado
    :return: Cantidad de caracteres compatibles con el cifrado
    """
    return len(caracteres)

def index_letra(letra, lang):
    """
        Funcion que recibe una letra y devuelve el indice de la misma, A=0, B=1. C=2...
    :param letra: letra de la que se quiere obtener el indice
    :param lang: idioma seleccionado por el usuario
    :return: indice de la letra
    """
    if not letra in caracteres:
        raise ValueError(f'{lang.t("gen_utl_letra_inv_pt1")}{letra}{lang.t("gen_utl_letra_inv_pt2")}')
    return caracteres.index(letra)


def obtener_letra(index,lang):
    """
        Funcion que recibe un indice y devuelve la letra correspondiente al mismo, A=0, B=1, C=2...
    :param index: Indice de la letra que se quiere obtener
    :param lang: idioma seleccionado por el usuario
    :return: letra que corresponde al indice
    """
    if index < 0 or index > lenght_chars():
        raise ValueError(f'{lang.t("gen_utl_index_inv")}{lenght_chars()}')
    return caracteres[index]

def elegir_idioma():
    print("\n===Elegir Idioma===")
    print("es: Español")
    print("eus: Euskera")
    print("en: English")
    idioma = input("ingrese el idioma: ").lower()
    while idioma not in ("es", "en", "eus"):
        print("Idioma invalido")
        idioma = input("ingrese el idioma: ")
    return idioma