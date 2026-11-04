package com.dm2.orden_del_auror.crypto

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Gestiona el descifrado de archivos mediante AES.
 */
object AESCrypto {

    private const val TRANSFORMACION = "AES/CBC/PKCS5Padding"
    private const val ALGORITMO_CLAVE = "AES"

    private const val TAM_CLAVE = 16
    private const val TAM_IV = 16

    /**
     * Descifra un archivo utilizando AES.
     *
     * Los primeros 16 bytes contienen el IV.
     * El resto del contenido corresponde a los datos cifrados.
     *
     * @param datosCifrados Contenido completo del archivo cifrado.
     * @param contrasena Contraseña utilizada para generar la clave AES.
     * @return Bytes originales del archivo descifrado.
     */
    fun descifrar(
        datosCifrados: ByteArray,
        contrasena: String
    ): ByteArray {

        require(datosCifrados.size > TAM_IV) {
            "El archivo cifrado no contiene datos suficientes."
        }

        require(contrasena.isNotEmpty()) {
            "La contraseña no puede estar vacía."
        }

        // 1. Extraer el IV (primeros 16 bytes).
        val iv = datosCifrados.copyOfRange(0, TAM_IV)

        // 2. Extraer los datos cifrados.
        val contenidoCifrado =
            datosCifrados.copyOfRange(TAM_IV, datosCifrados.size)

        // 3. Generar la clave AES a partir de la contraseña.
        val claveBytes = MessageDigest
            .getInstance("MD5")
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .copyOf(TAM_CLAVE)

        val clave = SecretKeySpec(claveBytes, ALGORITMO_CLAVE)

        // 4. Inicializar el vector de inicialización.
        val especificacionIV = IvParameterSpec(iv)

        // 5. Configurar el descifrador AES.
        val cipher = Cipher.getInstance(TRANSFORMACION)

        cipher.init(
            Cipher.DECRYPT_MODE,
            clave,
            especificacionIV
        )

        // 6. Recuperar los datos originales.
        return cipher.doFinal(contenidoCifrado)
    }
}
