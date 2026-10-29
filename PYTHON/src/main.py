from utilities.general_utils import mostrar_menu,func_salir
from utilities.encrypt_utils import encrypt_text, decrypt_text

while True:
    mostrar_menu()
    opcion = input("Escribe una opcion: ")

    match opcion:
        case "1":
            text = input("Texto: ")
            key = input("Clave: ")
            print(f'Texto Cifrado: {encrypt_text(text,key)}')

        case "2":
            text = input("Texto: ")
            key = input("Clave: ")
            print(f'Texto Descifrado: {decrypt_text(text,key)}')

        case "3":
            func_salir()

        case _:
            print("opcion invalida")

