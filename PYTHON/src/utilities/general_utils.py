import sys

caracteres = ("abcdefghijklmnñopqrstuvwxyz1234567890<,.->;:_´ç{}¨`+[]^*º\ª!·$%&/()=?¿|@#~€¬áéíóúàèìòùäëïöüABCDEFGHIJKL"
              "MNÑOPQRSTUVWXYZÁÉÍÓÚÀÈÌÒÙÄËÏÖÜ")

__author__ = "Henri Tejada"

def mostrar_menu():
    """
        Funcion que permite mostrar el menu
    """
    print("\n===Orden del Auror===")
    print("1. Cifrar texto")
    print("2. Descifrar texto")
    print("3. Cifrar Archivo")
    print("4. Descifrar Archivo")
    print("5. Salir")

def func_salir():
    """
        Funcion que permite cerrar la aplicacion desde el menu
    """
    print("Saliendo...")
    sys.exit()

def lenght_chars():
    """
        Funcion que devuelve la cantidad de caracteres compatibles con el cifrado
    :return: Cantidad de caracteres compatibles con el cifrado
    """
    return len(caracteres)

def index_letra(letra):
    """
        Funcion que recibe una letra y devuelve el indice de la misma, A=0, B=1. C=2...
    :param letra: letra de la que se quiere obtener el indice
    :return: indice de la letra
    """
    if not letra in caracteres:
        raise ValueError(f'Carácter {letra} no compatible con el cifrado')
    return caracteres.index(letra)


def obtener_letra(index):
    """
        Funcion que recibe un indice y devuelve la letra correspondiente al mismo, A=0, B=1, C=2...
    :param index: Indice de la letra que se quiere obtener
    :return: letra que corresponde al indice
    """
    if index < 0 or index > lenght_chars():
        raise ValueError(f'El indice debe ser mayor o igual a 0 y menor o igual a {lenght_chars()}')
    return caracteres[index]
