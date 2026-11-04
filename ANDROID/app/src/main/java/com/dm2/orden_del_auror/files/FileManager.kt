
package com.dm2.orden_del_auror.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Gestiona la lectura y escritura de archivos en el dispositivo.
 */
object FileManager {

    /**
     * Lee el contenido de un archivo seleccionado.
     *
     * @param context Contexto de la aplicación.
     * @param uri Dirección del archivo seleccionado.
     * @return Contenido del archivo en bytes.
     */
    fun leerArchivo(context: Context, uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("No se puede abrir el archivo.")

        return inputStream.use {
            it.readBytes()
        }
    }

    /**
     * Guarda contenido en la ubicación seleccionada por el usuario.
     *
     * @param context Contexto de la aplicación.
     * @param uri Dirección donde guardar el archivo.
     * @param datos Contenido que se desea guardar.
     */
    fun guardarArchivo(
        context: Context,
        uri: Uri,
        datos: ByteArray
    ) {
        val outputStream = context.contentResolver.openOutputStream(uri)
            ?: throw IllegalStateException("No se puede guardar el archivo.")

        outputStream.use {
            it.write(datos)
            it.flush()
        }
    }

    /**
     * Obtiene el nombre de un archivo a partir de su URI.
     *
     * @param context Contexto de la aplicación.
     * @param uri Dirección del archivo.
     * @return Nombre del archivo.
     */
    fun obtenerNombreArchivo(context: Context, uri: Uri): String {

        var nombre = "archivo"

        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {
                val columna = cursor.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

                if (columna != -1) {
                    nombre = cursor.getString(columna) ?: nombre
                }
            }
        }

        return nombre
    }

    /**
     * Genera el nombre del archivo descifrado.
     *
     * @param nombreOriginal Nombre del archivo original.
     * @return Nombre del archivo de salida.
     */
    fun generarNombreDescifrado(nombreOriginal: String): String {

        val extension = nombreOriginal.substringAfterLast(
            ".",
            ""
        )

        val nombreBase = nombreOriginal.substringBeforeLast(
            ".",
            nombreOriginal
        )

        return if (extension.isNotEmpty()) {
            "${nombreBase}_descifrado.$extension"
        } else {
            "${nombreOriginal}_descifrado"
        }
    }

    /**
     * Obtiene el tipo MIME de un archivo.
     *
     * @param context Contexto de la aplicación.
     * @param uri Dirección del archivo.
     * @return Tipo MIME o application/octet-stream.
     */
    fun obtenerTipoMime(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri)
            ?: "application/octet-stream"
    }
}
