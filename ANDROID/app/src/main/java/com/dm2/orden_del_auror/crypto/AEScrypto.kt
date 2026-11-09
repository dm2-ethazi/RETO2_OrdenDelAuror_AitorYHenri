
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
    private const val ALGORITMO_HASH = "MD5"

    private const val TAM_CLAVE = 16
    private const val TAM_IV = 16
    private const val TAM_BLOQUE = 16

    /**
     * Descifra un archivo utilizando AES.
     *
     * Los primeros 16 bytes contienen el vector de inicialización (IV).
     * El resto del contenido corresponde a los datos cifrados.
     *
     * @param datosCifrados Contenido completo del archivo cifrado.
     * @param contrasena Contraseña utilizada para generar la clave AES.
     * @return Bytes originales del archivo descifrado.
     *
     * @throws IllegalArgumentException Si la contraseña o el archivo
     * no cumplen los requisitos necesarios.
     * @throws javax.crypto.BadPaddingException Si el descifrado falla
     * al comprobar el relleno, normalmente por una contraseña incorrecta
     * o un archivo dañado.
     */
    fun descifrar(
        datosCifrados: ByteArray,
        contrasena: String
    ): ByteArray {

        // 1. Validar la contraseña.
        require(contrasena.isNotBlank()) {
            "La contraseña no puede estar vacía."
        }

        // 2. Comprobar que el archivo contiene el IV
        // y al menos un bloque de datos cifrados.
        require(datosCifrados.size > TAM_IV) {
            "El archivo cifrado no contiene datos suficientes."
        }

        val tamContenidoCifrado = datosCifrados.size - TAM_IV

        // 3. AES necesita que el contenido cifrado
        // tenga un tamaño múltiplo de 16 bytes.
        require(tamContenidoCifrado % TAM_BLOQUE == 0) {
            "El archivo cifrado está incompleto o tiene un formato incorrecto."
        }

        // 4. Extraer el vector de inicialización (IV).
        val iv = datosCifrados.copyOfRange(
            0,
            TAM_IV
        )

        // 5. Extraer los datos cifrados.
        val contenidoCifrado = datosCifrados.copyOfRange(
            TAM_IV,
            datosCifrados.size
        )

        // 6. Generar la clave AES a partir de la contraseña.
        // Se mantiene MD5 para conservar la compatibilidad
        // con los archivos cifrados anteriormente.
        val claveBytes = MessageDigest
            .getInstance(ALGORITMO_HASH)
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .copyOf(TAM_CLAVE)

        val clave = SecretKeySpec(
            claveBytes,
            ALGORITMO_CLAVE
        )

        // 7. Preparar el vector de inicialización.
        val especificacionIV = IvParameterSpec(iv)

        // 8. Configurar el descifrador AES.
        val cipher = Cipher.getInstance(TRANSFORMACION)

        cipher.init(
            Cipher.DECRYPT_MODE,
            clave,
            especificacionIV
        )

        // 9. Descifrar y devolver los datos originales.
        return cipher.doFinal(contenidoCifrado)
    }
}
