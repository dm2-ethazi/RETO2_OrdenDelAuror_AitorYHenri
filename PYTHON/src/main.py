from utilities.general_utils import mostrar_menu,func_salir
from utilities.encrypt_utils import encrypt_text, decrypt_text
from utilities.files_utils import encrypt_file, decrypt_file

__author__ = "Henri Tejada"
while True:
    mostrar_menu()
    opcion = input("Escribe una opción: ")

    match opcion:
        case "1":
            text = input("Texto: ")
            key = input("Clave: ")
            try:
                print(f'Texto Cifrado: {encrypt_text(text,key)}')
            except ValueError as e:
                print(e)

        case "2":
            text = input("Texto: ")
            key = input("Clave: ")
            try:
                print(f'Texto Descifrado: {decrypt_text(text, key)}')
            except ValueError as e:
                print(e)

        case "3":
            key = input("Clave: ")
            try:
                encrypt_file(key)
            except ValueError as e:
                print(e)

        case "4":
            key = input("Clave: ")
            try:
                decrypt_file(key)
            except ValueError as e:
                print(e)

        case "5":
            func_salir()

        case _:
            print("Opción inválida")

