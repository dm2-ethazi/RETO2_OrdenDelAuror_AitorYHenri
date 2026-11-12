package com.dm2.orden_del_auror.crypto

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.CodingErrorAction

/** Gestiona el descifrado de textos mediante el algoritmo César. */
object CesarCrypto {

    /** Descifra un texto en memoria (útil también para las pruebas unitarias). */
    fun descifrar(texto: String, desplazamiento: Int): String {
        require(desplazamiento in 0..25) { "El desplazamiento debe estar entre 0 y 25." }
        val resultado = StringBuilder(texto.length)
        texto.forEach { resultado.append(descifrarCaracter(it, desplazamiento)) }
        return resultado.toString()
    }

    /**
     * Descifra un TXT por bloques. No carga el archivo completo en memoria.
     * La entrada se interpreta como UTF-8 estricto y se rechazan controles binarios.
     */
    @Throws(IOException::class)
    fun descifrarArchivo(entrada: InputStream, salida: OutputStream, desplazamiento: Int) {
        require(desplazamiento in 0..25) { "El desplazamiento debe estar entre 0 y 25." }

        val decoder = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)

        var contieneTexto = false
        val reader = BufferedReader(InputStreamReader(entrada, decoder), 8192)
        val writer = BufferedWriter(OutputStreamWriter(salida, Charsets.UTF_8), 8192)
        val buffer = CharArray(8192)

        while (true) {
            val cantidad = reader.read(buffer)
            if (cantidad == -1) break

            for (i in 0 until cantidad) {
                val caracter = buffer[i]
                if (!caracter.isWhitespace()) contieneTexto = true
                if (Character.isISOControl(caracter) && caracter != '\n' && caracter != '\r' && caracter != '\t') {
                    throw IOException("El archivo contiene caracteres no válidos para un TXT.")
                }
                writer.write(descifrarCaracter(caracter, desplazamiento).code)
            }
        }

        if (!contieneTexto) throw IOException("El archivo está vacío.")
        writer.flush()
    }

    private fun descifrarCaracter(caracter: Char, desplazamiento: Int): Char {
        val desplazamientoReal = desplazamiento % 26
        return when (caracter) {
            in 'A'..'Z' -> 'A' + ((caracter - 'A' - desplazamientoReal + 26) % 26)
            in 'a'..'z' -> 'a' + ((caracter - 'a' - desplazamientoReal + 26) % 26)
            else -> caracter
        }
    }
}
