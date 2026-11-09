package com.dm2.orden_del_auror.crypto

/**
 * Gestiona el descifrado de textos mediante el algoritmo César.
 */
object CesarCrypto {

    /**
     * Descifra un texto utilizando el algoritmo César.
     *
     * Mantiene los espacios, números, signos de puntuación
     * y caracteres que no pertenecen al alfabeto inglés.
     *
     * @param texto Texto cifrado.
     * @param desplazamiento Número de posiciones para descifrar.
     * @return Texto original.
     */
    fun descifrar(
        texto: String,
        desplazamiento: Int
    ): String {

        require(desplazamiento in 0..25) {
            "El desplazamiento debe estar entre 0 y 25."
        }

        val desplazamientoReal = desplazamiento % 26

        return texto.map { caracter ->

            when (caracter) {

                in 'A'..'Z' -> {
                    val posicion = caracter - 'A'
                    val nuevaPosicion =
                        (posicion - desplazamientoReal + 26) % 26

                    'A' + nuevaPosicion
                }

                in 'a'..'z' -> {
                    val posicion = caracter - 'a'
                    val nuevaPosicion =
                        (posicion - desplazamientoReal + 26) % 26

                    'a' + nuevaPosicion
                }

                else -> caracter
            }

        }.joinToString("")
    }
}
