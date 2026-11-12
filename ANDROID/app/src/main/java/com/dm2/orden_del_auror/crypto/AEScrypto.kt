package com.dm2.orden_del_auror.crypto

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Gestiona el descifrado de archivos mediante AES.
 *
 * Formato compatible con los archivos existentes:
 * AES/CBC/PKCS5Padding, clave MD5 de la contraseña e IV inicial de 16 bytes.
 */
object AESCrypto {

    private const val TRANSFORMACION = "AES/CBC/PKCS5Padding"
    private const val ALGORITMO_CLAVE = "AES"
    private const val ALGORITMO_HASH = "MD5"
    private const val TAM_CLAVE = 16
    private const val TAM_IV = 16
    private const val TAM_BLOQUE = 16
    private const val TAM_BUFFER = 64 * 1024

    /**
     * Descifra un ByteArray. Se conserva para compatibilidad con otros usos
     * y pruebas unitarias. Para archivos grandes, utilizar descifrarArchivo().
     */
    fun descifrar(datosCifrados: ByteArray, contrasena: String): ByteArray {
        require(contrasena.isNotBlank()) {
            "La contraseña no puede estar vacía."
        }
        require(datosCifrados.size > TAM_IV) {
            "El archivo cifrado no contiene datos suficientes."
        }

        val tamContenidoCifrado = datosCifrados.size - TAM_IV
        require(tamContenidoCifrado % TAM_BLOQUE == 0) {
            "El archivo cifrado está incompleto o tiene un formato incorrecto."
        }

        val iv = datosCifrados.copyOfRange(0, TAM_IV)
        val contenidoCifrado = datosCifrados.copyOfRange(TAM_IV, datosCifrados.size)

        val cipher = crearCipher(contrasena, iv)
        return cipher.doFinal(contenidoCifrado)
    }

    /**
     * Descifra un archivo por bloques, sin cargarlo entero en memoria.
     *
     * Los primeros 16 bytes de input contienen el IV. El resto es el
     * contenido AES-CBC con padding PKCS5/PKCS7.
     *
     * No cierra los streams recibidos: el llamador controla su ciclo de vida.
     */
    @Throws(Exception::class)
    fun descifrarArchivo(
        input: InputStream,
        output: OutputStream,
        contrasena: String
    ) {
        require(contrasena.isNotBlank()) {
            "La contraseña no puede estar vacía."
        }

        val iv = ByteArray(TAM_IV)
        var ivLeidos = 0

        while (ivLeidos < TAM_IV) {
            val leidos = input.read(iv, ivLeidos, TAM_IV - ivLeidos)
            if (leidos == -1) {
                throw IOException("El archivo no contiene un IV completo.")
            }
            ivLeidos += leidos
        }

        val cipher = crearCipher(contrasena, iv)
        val buffer = ByteArray(TAM_BUFFER)
        var totalCifrado = 0L

        while (true) {
            val leidos = input.read(buffer)
            if (leidos == -1) break

            if (leidos > 0) {
                totalCifrado += leidos
                val descifrado = cipher.update(buffer, 0, leidos)
                if (descifrado != null && descifrado.isNotEmpty()) {
                    output.write(descifrado)
                }
            }
        }

        if (totalCifrado == 0L) {
            throw IOException("El archivo no contiene datos cifrados.")
        }

        if (totalCifrado % TAM_BLOQUE != 0L) {
            throw IOException("El archivo cifrado está incompleto.")
        }

        // doFinal comprueba el padding y lanza BadPaddingException
        // si la contraseña es incorrecta o el archivo está dañado.
        val ultimoBloque = cipher.doFinal()
        if (ultimoBloque.isNotEmpty()) {
            output.write(ultimoBloque)
        }
        output.flush()
    }

    private fun crearCipher(contrasena: String, iv: ByteArray): Cipher {
        val claveBytes = MessageDigest
            .getInstance(ALGORITMO_HASH)
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .copyOf(TAM_CLAVE)

        val clave = SecretKeySpec(claveBytes, ALGORITMO_CLAVE)
        val especificacionIV = IvParameterSpec(iv)
        return Cipher.getInstance(TRANSFORMACION).apply {
            init(Cipher.DECRYPT_MODE, clave, especificacionIV)
        }
    }
}
